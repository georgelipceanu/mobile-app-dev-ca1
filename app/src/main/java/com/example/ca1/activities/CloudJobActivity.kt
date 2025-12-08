package com.example.ca1.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.helpers.showImagePicker
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.util.Calendar
import timber.log.Timber.i
class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    var cloudJob = CloudJobModel()
    var edit = false
    lateinit var app : MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            cloudJob.image = result.data!!.data!!
                            Picasso.get()
                                .load(cloudJob.image)
                                .into(binding.cloudjobImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerImagePickerCallback()

        binding.toolbarAdd.title = "Cloud Job Config"
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.replicaPicker.minValue = 1
        binding.replicaPicker.maxValue = 20

        if (intent.hasExtra("cloud_job_edit")) {
            edit = true
            cloudJob = intent.extras?.getParcelable("cloud_job_edit")!!
            binding.cloudjobTitle.setText(cloudJob.title)
            binding.description.setText(cloudJob.description)
            binding.deadlineField.setText(cloudJob.deadline)
            binding.CPUAutoComplete.setText(cloudJob.CPUType)
            binding.replicaPicker.value = cloudJob.replicas
            if (cloudJob.duration > -1) binding.durationValue.text = cloudJob.duration.toString()
            binding.btnAdd.setText(R.string.save_cloud_job)
            Picasso.get()
                .load(cloudJob.image)
                .into(binding.cloudjobImage)
        }

        var durationFieldValue = 30
        binding.durationValue.text = durationFieldValue.toString()

        binding.clearDeadlineButton.setOnClickListener {
            binding.deadlineField.text?.clear()
        }

        binding.deadlineField.setOnClickListener {
            val cal = Calendar.getInstance() // format "yyyy-MM-dd"
            val deadline = cloudJob.deadline
            if (!deadline.isNullOrBlank()) {
                val y = deadline.substring(0, 4).toInt()
                val m = deadline.substring(5, 7).toInt() - 1 // months follow 0-11 for DatePicker
                val d = deadline.substring(8, 10).toInt()
                cal.set(y, m, d)
            }

            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) // format "yyyy-MM-dd", ref: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/format.html
                    binding.deadlineField.setText(date)
                    cloudJob.deadline = date
                },
                y, m, d).show()
        }

        val cpus = resources.getStringArray(R.array.cpu_types)      // ref for dropdowns: https://www.geeksforgeeks.org/kotlin/exposed-drop-down-menu-in-android/
        val cpuArrayAdapter = ArrayAdapter(this, R.layout.cpu_dropdown, cpus)
        val cpuAC = findViewById<AutoCompleteTextView>(R.id.CPUAutoComplete)
        cpuAC.setAdapter(cpuArrayAdapter)

        binding.btnIncrease.setOnClickListener {
            durationFieldValue += 5 //up and down increments of 5
            binding.durationValue.text = durationFieldValue.toString()
        }
        binding.btnDecrease.setOnClickListener {
            if (durationFieldValue > 0) durationFieldValue -= 5
            binding.durationValue.text = durationFieldValue.toString()
        }

        binding.btnAdd.setOnClickListener() {
            cloudJob.title = binding.cloudjobTitle.text.toString()
            cloudJob.description = binding.description.text.toString()
            cloudJob.deadline = binding.deadlineField.text.toString()
            cloudJob.CPUType = if (binding.CPUAutoComplete.text.toString().equals("Choose CPU")) "Micro" else  binding.CPUAutoComplete.text.toString()
            cloudJob.replicas = binding.replicaPicker.value
            cloudJob.duration = if (binding.indefinteCheckbox.isChecked) -1 else binding.durationValue.text.toString().toIntOrNull()!!
            if (cloudJob.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_cloud_job_title, Snackbar.LENGTH_LONG).show()
            } else {
                if (edit) {
                    app.cloudJobs.update(cloudJob.copy())
                } else {
                    app.cloudJobs.create(cloudJob.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cloud_job_menu, menu)
        menu.findItem(R.id.item_delete)?.isVisible = edit           // changes based on if it job is being edited or not (ref: https://stackoverflow.com/questions/47764335/android-menu-item-visibility-change-in-runtime)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                if (edit) {
                    app.cloudJobs.delete(cloudJob)
                    setResult(RESULT_OK)
                    finish()
                }
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}