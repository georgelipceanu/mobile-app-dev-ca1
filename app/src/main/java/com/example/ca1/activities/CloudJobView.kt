package com.example.ca1.activities

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.example.ca1.main.MainApp
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.models.CloudJobModel
import timber.log.Timber.i

class CloudJobView : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    private lateinit var presenter: CloudJobPresenter
    var cloudJob = CloudJobModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        presenter = CloudJobPresenter(this)

        binding.chooseImage.setOnClickListener {
            presenter.cacheCloudJob(binding.cloudjobTitle.text.toString(), binding.description.text.toString())
            presenter.doSelectImage()
        }

        binding.cloudjobLocation.setOnClickListener {
            presenter.cacheCloudJob(binding.cloudjobTitle.text.toString(), binding.description.text.toString())
            presenter.doSetLocation()
        }

        binding.btnAdd.setOnClickListener {
            if (binding.cloudjobTitle.text.toString().isEmpty()) {
                Snackbar.make(binding.root, R.string.enter_cloud_job_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                // presenter.cachecloudJob(binding.cloudJobTitle.text.toString(), binding.description.text.toString())  
                presenter.doAddOrSave(binding.cloudjobTitle.text.toString(), binding.description.text.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cloud_job_menu, menu)
        val deleteMenu: MenuItem = menu.findItem(R.id.item_delete)
        deleteMenu.isVisible = presenter.edit
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                presenter.doDelete()
            }
            R.id.item_cancel -> {
                presenter.doCancel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showCloudJob(cloudJob: CloudJobModel) {
        binding.cloudjobTitle.setText(cloudJob.title)
        binding.description.setText(cloudJob.description)
        binding.btnAdd.setText(R.string.save_cloud_job)
        Picasso.get()
            .load(cloudJob.image)
            .into(binding.cloudjobImage)
        if (cloudJob.image != Uri.EMPTY) {
            binding.chooseImage.setText(R.string.change_cloudjob_image)
        }

    }

    fun updateImage(image: Uri){
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.cloudjobImage)
        binding.chooseImage.setText(R.string.change_cloudjob_image)
    }
}