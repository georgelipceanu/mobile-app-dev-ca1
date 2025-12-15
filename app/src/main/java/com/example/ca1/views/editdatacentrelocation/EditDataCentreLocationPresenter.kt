package com.example.ca1.views.editdatacentrelocation

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ca1.models.DataCentreLocation

class EditDataCentreLocationPresenter (val view: EditDataCentreLocationView) {

    var dataCentreLocation = DataCentreLocation()

    init {
        //dataCentreLocation = view.intent.extras?.getParcelable("dataCentreLocation",DataCentreLocation::class.java)!!
        dataCentreLocation = view.intent.extras?.getParcelable("dataCentreLocation")!!
    }

    fun initMap(map: GoogleMap) {
        val loc = LatLng(dataCentreLocation.lat, dataCentreLocation.lng)
        val options = MarkerOptions()
            .title("Placemark")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, dataCentreLocation.zoom))
        map.setOnMarkerDragListener(view)
        map.setOnMarkerClickListener(view)
    }

    fun doUpdateDataCentreLocation(lat: Double, lng: Double, zoom: Float) {
        dataCentreLocation.lat = lat
        dataCentreLocation.lng = lng
        dataCentreLocation.zoom = zoom
    }

    fun doOnBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("dataCentreLocation", dataCentreLocation)
        view.setResult(Activity.RESULT_OK, resultIntent)
        view.finish()
    }

    fun doUpdateMarker(marker: Marker) {
        val loc = LatLng(dataCentreLocation.lat, dataCentreLocation.lng)
        marker.snippet = "GPS : $loc"
    }
}
