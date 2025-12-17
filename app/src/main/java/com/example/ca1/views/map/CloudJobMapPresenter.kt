package com.example.ca1.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ca1.main.MainApp

class CloudJobMapPresenter(val view: CloudJobMapView) {
    var app: MainApp

    init {
        app = view.application as MainApp
    }

    fun doPopulateMap(map: GoogleMap) {
        map.uiSettings.setZoomControlsEnabled(true)
        map.setOnMarkerClickListener { marker ->
            doMarkerSelected(marker)
            true
        }
        app.cloudJobs.findAllPairs().forEach { (id, job) ->
            val loc = LatLng(job.lat, job.lng)
            val options = MarkerOptions().title(job.title).position(loc)
            map.addMarker(options)?.tag = id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, job.zoom))
        }
    }

    fun doMarkerSelected(marker: Marker) {
        val id = marker.tag as? String ?: return
        android.util.Log.d("MAP", "Clicked marker tag=$id")
        val job = app.cloudJobs.findById(id)
        if (job != null) view.showCloudJob(job)
    }
}