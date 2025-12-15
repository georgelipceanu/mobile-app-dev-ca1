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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CloudJobListPresenter(val view: CloudJobListView) {

    var app: MainApp
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    init {
        app = view.application as MainApp
        registerRefreshCallback()
        registerMapCallback()
    }

    fun getCloudJobs() = app.cloudJobs.findAll()
    fun doAddCloudJob() {
        val launcherIntent = Intent(view, CloudJobView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }
    fun doEditCloudJob(job: CloudJobModel) {
        val launcherIntent = Intent(view, CloudJobView::class.java)
        launcherIntent.putExtra("cloud_job_edit", job)
        refreshIntentLauncher.launch(launcherIntent)
    }
    fun doShowCloudJobsMap() {
        val launcherIntent = Intent(view, CloudJobMapsActivity::class.java)
        mapIntentLauncher.launch(launcherIntent)
    }
    fun doDeleteCloudJob(job: CloudJobModel) {
        app.cloudJobs.delete(job)
        view.onRefresh()
        refreshEmissions()
    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    android.app.Activity.RESULT_OK, 99 -> {
                        refreshEmissions()
                        view.onRefresh()
                    }
                }
            }
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
                    view.onRefresh()
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
        val list = if (query.isNullOrBlank()) app.cloudJobs.findAll()
        else app.cloudJobs.findByTitle(query)
        view.showJobs(list)
    }
}
