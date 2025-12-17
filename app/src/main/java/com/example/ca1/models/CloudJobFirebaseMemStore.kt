package com.example.ca1.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CloudJobFirebaseMemStore {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cache = mutableMapOf<String, CloudJobModel>() // ref: https://www.geeksforgeeks.org/kotlin/kotlin-mutablemapof/

    private fun jobsRef() = db.collection("users")
        .document(requireNotNull(auth.currentUser?.uid) { "Not signed in" })
        .collection("cloudJobs") // users/{uid}/cloudJobs

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
        onDone: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        jobsRef()
            .add(job)
            .addOnSuccessListener { ref ->
                cache[ref.id] = job
                onDone(ref.id)
            }
            .addOnFailureListener { onError(it) }
    }

    fun update(
        jobId: String,
        job: CloudJobModel,
        onDone: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        jobsRef()
            .document(jobId)
            .set(job)
            .addOnSuccessListener {
                cache[jobId] = job
                onDone()
            }
            .addOnFailureListener { onError(it) }
    }

    fun update(job: CloudJobModel) { // for easy operability for the conversion from JSON to Firestore
        val entry = cache.entries.firstOrNull { it.value == job } ?: return
        update(entry.key, job)
    }
    fun delete(
        jobId: String,
        onDone: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        jobsRef()
            .document(jobId)
            .delete()
            .addOnSuccessListener {
                cache.remove(jobId)
                onDone()
            }
            .addOnFailureListener { onError(it) }
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
