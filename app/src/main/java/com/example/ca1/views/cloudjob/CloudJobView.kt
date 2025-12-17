package com.example.ca1.views.cloudjob

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
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
        presenter.initForm()

        binding.deadlineField.setOnClickListener { presenter.doPickDeadline() }
        binding.clearDeadlineButton.setOnClickListener { presenter.doClearDeadline() }
        binding.btnIncrease.setOnClickListener { presenter.doIncreaseDuration() }
        binding.btnDecrease.setOnClickListener { presenter.doDecreaseDuration() }

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
                presenter.doAddOrSave(
                    title = binding.cloudjobTitle.text.toString(),
                    description = binding.description.text.toString(),
                    cpuTypeText = binding.CPUAutoComplete.text.toString(),
                    replicas = binding.replicaPicker.value,
                    isIndefinite = binding.indefinteCheckbox.isChecked
                )
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
                confirmDelete {
                    presenter.doDelete()
                }
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

        when {
            cloudJob.image != Uri.EMPTY -> {
                Picasso.get().load(cloudJob.image).into(binding.cloudjobImage)
                binding.chooseImage.setText(R.string.change_cloudjob_image)
            }
            cloudJob.imageUrl.isNotBlank() -> {
                Picasso.get().load(cloudJob.imageUrl).into(binding.cloudjobImage)
                binding.chooseImage.setText(R.string.change_cloudjob_image)
            }
            else -> {
                binding.cloudjobImage.setImageDrawable(null)
                binding.chooseImage.setText(R.string.change_cloudjob_image)
            }
        }
    }

    fun updateImage(image: Uri){
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.cloudjobImage)
        binding.chooseImage.setText(R.string.change_cloudjob_image)
    }

    fun setReplicaPickerBounds(min: Int, max: Int) {
        binding.replicaPicker.minValue = min
        binding.replicaPicker.maxValue = max
    }

    fun bindCpuAdapter(adapter: ArrayAdapter<String>) {
        binding.CPUAutoComplete.setAdapter(adapter)
    }

    fun showDuration(value: Int) {
        binding.durationValue.text = value.toString()
    }

    fun showDeadline(value: String) {
        binding.deadlineField.setText(value)
    }

    fun showError(message: String) {
        // TODO: add snackbar
    }

    fun confirmDelete(
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(this).setTitle(R.string.delete_cloudJob).setMessage(R.string.delete_cloudjob_message) // ref: https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin
            .setPositiveButton(R.string.delete) { _, _ ->
                onConfirm()
            }.setNegativeButton(android.R.string.cancel, null).show()
    }
}