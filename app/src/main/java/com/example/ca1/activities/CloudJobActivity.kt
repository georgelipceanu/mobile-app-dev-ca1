package com.example.ca1.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    var cloudJob = CloudJobModel()
    var edit = false
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        if (intent.hasExtra("cloud_job_edit")) {
            edit = true
            cloudJob = intent.extras?.getParcelable("cloud_job_edit")!!
            binding.cloudjobTitle.setText(cloudJob.title)
            binding.description.setText(cloudJob.description)
            binding.btnAdd.setText(R.string.save_cloud_job)
            binding.deadlineField.setText(cloudJob.deadline)
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
                    val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.deadlineField.setText(date)
                    cloudJob.deadline = date
                },
                y, m, d).show()
        }

        binding.btnAdd.setOnClickListener() {
            cloudJob.title = binding.cloudjobTitle.text.toString()
            cloudJob.description = binding.description.text.toString()
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