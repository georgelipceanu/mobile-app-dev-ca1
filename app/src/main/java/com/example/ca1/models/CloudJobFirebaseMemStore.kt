package com.example.ca1.models

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference // ref: https://firebase.blog/posts/2016/07/5-tips-for-firebase-storage/
import java.util.UUID // needed for random unique values, ref to ai chat: https://chatgpt.com/share/6942b5dc-d540-8013-8acc-c51be744f048

class CloudJobFirebaseMemStore {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cache = mutableMapOf<String, CloudJobModel>() // ref: https://www.geeksforgeeks.org/kotlin/kotlin-mutablemapof/
    private val storage = FirebaseStorage.getInstance()
    private fun uid() = requireNotNull(auth.currentUser?.uid) { "Not signed in" }

    private fun jobsRef() = db.collection("users")
        .document(requireNotNull(auth.currentUser?.uid) { "Not signed in" })
        .collection("cloudJobs") // users/{uid}/cloudJobs
    private fun imageRef(id: String): StorageReference { // users/{uid}/cloudJobs/{id}/{random}.jpg
        val fileName = "${UUID.randomUUID()}.jpg" // prevent overlaps, ref:
        return storage.reference.child("users/${uid()}/cloudJobs/$id/$fileName")
    }

    fun findById(id: String): CloudJobModel? =
        cache[id]
    fun findAll(): List<CloudJobModel> =
        cache.values.toList()
    fun findAllPairs(): List<Pair<String, CloudJobModel>> =
        cache.map { (id, job) -> id to job }
    fun findByTitlePairs(query: String): List<Pair<String, CloudJobModel>> =
        cache.filter { (_, job) -> job.title.contains(query, ignoreCase = true) }.map { (id, job) -> id to job }

    fun create(
        job: CloudJobModel,
        imageUri: Uri?,
        onDone: (String) -> Unit,
        onError: (Exception) -> Unit
    )  {
        val docRef = jobsRef().document() // ref: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/StorageReference
        val id = docRef.id
        if (imageUri == null || imageUri == Uri.EMPTY) {  // if no image selected, just save as is
            docRef.set(job).addOnSuccessListener {
                cache[id] = job
                onDone(id)
            }.addOnFailureListener { onError(it) }
            return
        }
        val ref = imageRef(id)
        ref.putFile(imageUri).continueWithTask { task ->  // upload, get downloadUrl, save Firestore with imageUrl, ref: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/StorageReference#putFile(android.net.Uri)
            if (!task.isSuccessful) throw task.exception!!
            ref.downloadUrl
        }.addOnSuccessListener { downloadUrl ->
            val updated = job.copy(imageUrl = downloadUrl.toString())
            docRef.set(updated).addOnSuccessListener {
                cache[id] = updated
                onDone(id)
            }.addOnFailureListener { onError(it) }
        }.addOnFailureListener { onError(it) }
    }

    fun update(
        id: String,
        job: CloudJobModel,
        newImageUri: Uri?,
        onDone: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val docRef = jobsRef().document(id) // ref: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/StorageReference
        if (newImageUri == null || newImageUri == Uri.EMPTY) { // if no image selected, just save as is
            docRef.set(job).addOnSuccessListener {
                cache[id] = job
                onDone()
            }.addOnFailureListener { onError(it) }
            return
        }
        val ref = imageRef(id)
        ref.putFile(newImageUri).continueWithTask { task -> // upload, get downloadUrl, save Firestore with imageUrl, ref: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/StorageReference#putFile(android.net.Uri)
            if (!task.isSuccessful) throw task.exception!!
            ref.downloadUrl
        }.addOnSuccessListener { downloadUrl ->
            val updated = job.copy(imageUrl = downloadUrl.toString())
            docRef.set(updated).addOnSuccessListener {
                cache[id] = updated
                onDone()
            }.addOnFailureListener { onError(it) }
        }.addOnFailureListener { onError(it) }
    }

    fun update(job: CloudJobModel) { // for easy operability for the conversion from JSON to Firestore
        val entry = cache.entries.firstOrNull { it.value == job } ?: return
        update(entry.key, job, null)
    }
    fun delete(
        id: String,
        onDone: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        jobsRef().document(id).delete().addOnSuccessListener {
            cache.remove(id)
            onDone()
        }.addOnFailureListener { onError(it) }
    }

    fun listenAll( // ref: https://firebase.google.com/docs/firestore/query-data/listen
        onData: (List<Pair<String, CloudJobModel>>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration { // ref: https://medium.com/firebase-tips-tricks/how-to-effortlessly-get-real-time-updates-from-firestore-on-android-bcb823f45f20
        return jobsRef().addSnapshotListener { snap, e ->
            if (e != null) {
                onError(e)
                return@addSnapshotListener
            }
            if (snap == null) {
                onData(emptyList())
                return@addSnapshotListener
            }
            cache.clear()
            val list = snap.documents.mapNotNull { doc ->
                val job = doc.toObject(CloudJobModel::class.java) // ref: https://medium.com/@ncubes1999/how-to-use-firebase-firestore-with-kotlin-coroutines-8d1f498f9c94
                if (job != null) {
                    cache[doc.id] = job
                    doc.id to job
                } else null
            }
            onData(list)
        }
    }
}
