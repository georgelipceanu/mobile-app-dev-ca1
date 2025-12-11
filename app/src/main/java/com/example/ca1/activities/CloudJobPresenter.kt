package com.example.ca1.activities
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.main.MainApp
import com.example.ca1.models.DataCentreLocation
import com.example.ca1.models.CloudJobModel
import com.example.ca1.views.editlocation.EditLocationView
import timber.log.Timber

class CloudJobPresenter(private val view: CloudJobView) {
    var cloudJob = CloudJobModel()
    var app: MainApp = view.application as MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false

    init {
        if (view.intent.hasExtra("cloudJob_edit")) {
            edit = true
            //cloudJob = view.intent.getParcelableExtra("cloudJob_edit",CloudJobModel::class.java)!!
            cloudJob = view.intent.extras?.getParcelable("cloudJob_edit")!!
            view.showCloudJob(cloudJob)
        }
        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doAddOrSave(title: String, description: String) {
        cloudJob.title = title
        cloudJob.description = description
        if (edit) {
            app.cloudJobs.update(cloudJob)
        } else {
            app.cloudJobs.create(cloudJob)
        }
        view.setResult(RESULT_OK)
        view.finish()
    }

    fun doCancel() {
        view.finish()
    }

    fun doDelete() {
        view.setResult(99)
        app.cloudJobs.delete(cloudJob)
        view.finish()
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
        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("location", dataCentreLocation)
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
                    AppCompatActivity.RESULT_OK -> {
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
}
