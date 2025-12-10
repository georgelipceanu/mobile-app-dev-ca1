package com.example.ca1.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.example.ca1.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ca1.databinding.ActivityMapsBinding
import com.example.ca1.models.DataCentreLocation
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var dataCentreLocation = DataCentreLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //dataCentreLocation = intent.extras?.getParcelable("dataCentreLocation",DataCentreLocation::class.java)!!
        dataCentreLocation = intent.extras?.getParcelable<DataCentreLocation>("dataCentreLocation")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        onBackPressedDispatcher.addCallback(this ) {
            val resultIntent = Intent()
            resultIntent.putExtra("dataCentreLocation", dataCentreLocation)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loc = LatLng(dataCentreLocation.lat, dataCentreLocation.lng)
        val options = MarkerOptions()
            .title("Data Centre")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.setOnMarkerClickListener(this)
        map.setOnMarkerDragListener(this)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, dataCentreLocation.zoom))
    }

    override fun onMarkerDrag(p0: Marker) { }

    override fun onMarkerDragEnd(marker: Marker) {
        dataCentreLocation.lat = marker.position.latitude
        dataCentreLocation.lng = marker.position.longitude
        dataCentreLocation.zoom = map.cameraPosition.zoom
    }

    override fun onMarkerDragStart(p0: Marker) { }

    override fun onMarkerClick(marker: Marker): Boolean {
        val loc = LatLng(dataCentreLocation.lat, dataCentreLocation.lng)
        marker.snippet = "GPS : $loc"
        return false
    }
}