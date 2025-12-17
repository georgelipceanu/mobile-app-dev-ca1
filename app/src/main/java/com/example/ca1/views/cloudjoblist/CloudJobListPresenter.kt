package com.example.ca1.views.cloudjoblist

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ca1.BuildConfig
import com.example.ca1.views.cloudjob.CloudJobView
import com.example.ca1.activities.CloudJobMapsActivity
import com.example.ca1.api.CarbonIntensityResponse
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import com.google.firebase.firestore.ListenerRegistration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CloudJobListPresenter(val view: CloudJobListView) {

    var app: MainApp
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private var listener: ListenerRegistration? = null
    init {
        app = view.application as MainApp
        registerRefreshCallback()
        registerMapCallback()
    }

    fun startListening() {
        listener = app.cloudJobs.listenAll(
            onData = { pairs ->
                view.showJobs(pairs)
            },
            onError = {
                view.showError(it.message ?: "Failed to load jobs")
            }
        )
    }

    fun stopListening() {
        listener?.remove() // ref: https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/ListenerRegistration
    }

    fun doDeleteCloudJob(id: String) {
        app.cloudJobs.delete(id, onDone = {}, onError = { view.showError(it.message ?: "Delete failed") })
        refreshEmissions()
    }

    fun doAddCloudJob() {
        val launcherIntent = Intent(view, CloudJobView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }
    fun doEditCloudJob(id: String, cloudjob: CloudJobModel) {
        refreshIntentLauncher.launch(
            Intent(view, CloudJobView::class.java).putExtra("cloud_job_id", id).putExtra("cloud_job_edit", cloudjob)
        )
    }

    fun doShowCloudJobsMap() {
        val launcherIntent = Intent(view, CloudJobMapsActivity::class.java)
        mapIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher = view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { }
    }
    private fun refreshEmissions() {
        val jobs = app.cloudJobs.findAll()
        for (job in jobs) {
            if (job.duration <= 0) continue

            calculateEmissions(
                cpuType = job.CPUType,
                durationMinutes = job.duration,
                replicas = job.replicas,
                onSuccess = { grams ->
                    job.emissions = grams
                    app.cloudJobs.update(job)
                },
                onError = { }
            )
        }
    }

    private fun calculateEmissions(
        cpuType: String,
        durationMinutes: Int,
        replicas: Int,
        onSuccess: (Double) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val token = BuildConfig.ELECTRICITYMAPS_API_KEY

        RetrofitInstance.retrofit
            .getCarbonIntensity(token, "IE")
            .enqueue(object : Callback<CarbonIntensityResponse?> {
                override fun onResponse(
                    call: Call<CarbonIntensityResponse?>,
                    response: Response<CarbonIntensityResponse?>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        onError(IllegalStateException("HTTP ${response.code()}"))
                        return
                    }

                    val intensity = response.body()!!.carbonIntensity
                    val cpuPowerMap = mapOf(
                        "micro" to 0.01,
                        "small" to 0.05,
                        "medium" to 0.10,
                        "large" to 0.20
                    )

                    val hours = durationMinutes / 60.0
                    val emissions = intensity * (cpuPowerMap[cpuType.lowercase()] ?: 0.10) * hours * replicas
                    onSuccess(emissions)
                }

                override fun onFailure(call: Call<CarbonIntensityResponse?>, t: Throwable) {
                    onError(t)
                }
            })
    }

    fun doSearch(query: String?) {
        val pairs = if (query.isNullOrBlank()) app.cloudJobs.findAllPairs()
        else app.cloudJobs.findByTitlePairs(query)
        view.showJobs(pairs)
    }
}
