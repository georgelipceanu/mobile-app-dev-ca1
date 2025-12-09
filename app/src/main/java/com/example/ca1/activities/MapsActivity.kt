package com.example.ca1.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ca1.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ca1.databinding.ActivityMapsBinding
import com.example.ca1.models.DataCentreLocation

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var dataCentreLocation = DataCentreLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //location = intent.extras?.getParcelable("location",Location::class.java)!!
        dataCentreLocation = intent.extras?.getParcelable<DataCentreLocation>("dataCentreLocation")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, dataCentreLocation.zoom))
    }
}