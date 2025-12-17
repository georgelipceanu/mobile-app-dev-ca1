package com.example.ca1.views.map

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.example.ca1.databinding.ActivityCloudJobMapsBinding
import com.example.ca1.databinding.ContentCloudJobMapsBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel

class CloudJobMapView : AppCompatActivity() , GoogleMap.OnMarkerClickListener{

    private lateinit var binding: ActivityCloudJobMapsBinding
    private lateinit var contentBinding: ContentCloudJobMapsBinding
    lateinit var app: MainApp
    lateinit var presenter: CloudJobMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        binding = ActivityCloudJobMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        presenter = CloudJobMapPresenter(this)

        contentBinding = ContentCloudJobMapsBinding.bind(binding.root)

        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{
            presenter.doPopulateMap(it)
        }
    }
    fun showCloudJob(cloudJob: CloudJobModel) {
        contentBinding.currentTitle.text = cloudJob.title
        contentBinding.currentDescription.text = cloudJob.description
        contentBinding.latitude.text = "Lat: %.2f".format(cloudJob.lat)
        contentBinding.longitude.text = "Long: %.2f".format(cloudJob.lng)

        when {
            cloudJob.imageUrl.isNotBlank() -> {
                Picasso.get()
                    .load(cloudJob.imageUrl)
                    .into(contentBinding.imageView)
            }
            cloudJob.image != Uri.EMPTY -> {
                Picasso.get()
                    .load(cloudJob.image)
                    .into(contentBinding.imageView)
            }
            else -> {
                contentBinding.imageView.setImageDrawable(null)
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }
}