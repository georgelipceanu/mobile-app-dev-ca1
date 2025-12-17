package com.example.ca1.views.cloudjob
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.example.ca1.main.MainApp
import com.example.ca1.models.DataCentreLocation
import com.example.ca1.models.CloudJobModel
import com.example.ca1.views.editdatacentrelocation.EditDataCentreLocationView
import timber.log.Timber
import java.util.Calendar

class CloudJobPresenter(private val view: CloudJobView) {
    var cloudJob = CloudJobModel()
    var app: MainApp = view.application as MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false
    private var durationFieldValue = 30
    private var id: String? = null

    init {
        if (view.intent.hasExtra("cloud_job_edit")) {
            edit = true
            cloudJob = view.intent.extras?.getParcelable("cloud_job_edit")!!
            id = view.intent.getStringExtra("cloud_job_id")
            view.showCloudJob(cloudJob)
            durationFieldValue = if (cloudJob.duration > -1) cloudJob.duration else 30
        }
        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doCancel() {
        view.finish()
    }

    fun doDelete() {
        val id = id ?: return
        app.cloudJobs.delete(id, onDone = {
            view.setResult(99)
            view.finish()
        }, onError = {
            // TODO: add snackbar
        })
    }

    fun doSelectImage() {
        //   showImagePicker(imageIntentLauncher,view)
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
    }

    fun doSetLocation() {
        val dataCentreLocation = DataCentreLocation(52.245696, -7.139102, 15f)
        if (cloudJob.zoom != 0f) {
            dataCentreLocation.lat =  cloudJob.lat
            dataCentreLocation.lng = cloudJob.lng
            dataCentreLocation.zoom = cloudJob.zoom
        }
        val launcherIntent = Intent(view, EditDataCentreLocationView::class.java)
            .putExtra("dataCentreLocation", dataCentreLocation)
        mapIntentLauncher.launch(launcherIntent)
    }

    fun cacheCloudJob (title: String, description: String) {
        cloudJob.title = title
        cloudJob.description = description
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            try{
                view.contentResolver
                    .takePersistableUriPermission(it!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION )
                cloudJob.image = it // The returned Uri
                Timber.i("IMG :: ${cloudJob.image}")
                view.updateImage(cloudJob.image)
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            //val location = result.data!!.extras?.getParcelable("location",Location::class.java)!!
                            val dataCentreLocation = result.data!!.extras?.getParcelable<DataCentreLocation>("dataCentreLocation")!!
                            Timber.i("Location == $dataCentreLocation")
                            cloudJob.lat = dataCentreLocation.lat
                            cloudJob.lng = dataCentreLocation.lng
                            cloudJob.zoom = dataCentreLocation.zoom
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    fun initForm() {
        view.setReplicaPickerBounds(1, 20)

        // CPU dropdown
        val cpus = view.resources.getStringArray(R.array.cpu_types)
        val adapter = ArrayAdapter(view, R.layout.cpu_dropdown, cpus)
        view.bindCpuAdapter(adapter)

        // duration initial value
        view.showDuration(durationFieldValue)
    }

    fun doIncreaseDuration() {
        durationFieldValue += 5
        view.showDuration(durationFieldValue)
    }

    fun doDecreaseDuration() {
        if (durationFieldValue > 0) durationFieldValue -= 5
        view.showDuration(durationFieldValue)
    }

    fun doClearDeadline() {
        cloudJob.deadline = ""
        view.showDeadline("")
    }

    fun doPickDeadline() {
        val cal = Calendar.getInstance()

        val deadline = cloudJob.deadline
        if (!deadline.isNullOrBlank() && deadline.length >= 10) {
            val y = deadline.substring(0, 4).toInt()
            val m = deadline.substring(5, 7).toInt() - 1
            val d = deadline.substring(8, 10).toInt()
            cal.set(y, m, d)
        }

        DatePickerDialog(
            view,
            { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) // format "yyyy-MM-dd", ref: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/format.html
                cloudJob.deadline = date
                view.showDeadline(date)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun doAddOrSave(
        title: String,
        description: String,
        cpuTypeText: String,
        replicas: Int,
        isIndefinite: Boolean
    ) {
        cloudJob.title = title
        cloudJob.description = description
        cloudJob.CPUType = if (cpuTypeText == "Choose CPU" || cpuTypeText.isBlank()) "Micro" else cpuTypeText
        cloudJob.replicas = replicas
        cloudJob.duration = if (isIndefinite) -1 else durationFieldValue

        val onSuccess = {
            view.setResult(RESULT_OK)
            view.finish()
        }
        val onFailure: (Exception) -> Unit = { e ->
            view.showError(e.message ?: "Save failed")
        }
        if (edit) {
            val id = id ?: run { view.showError("Missing id"); return }
            app.cloudJobs.update(
                id = id,
                job = cloudJob,
                newImageUri = cloudJob.image,     // <- IMPORTANT
                onDone = onSuccess,
                onError = onFailure
            )
        } else {
            app.cloudJobs.create(
                job = cloudJob,
                imageUri = cloudJob.image,        // <- IMPORTANT
                onDone = { newId ->
                    id = newId
                    onSuccess()
                },
                onError = onFailure
            )
        }
    }
}
