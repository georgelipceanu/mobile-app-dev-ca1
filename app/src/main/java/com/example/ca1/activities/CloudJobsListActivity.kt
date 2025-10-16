package com.example.ca1.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.ActivityCloudJobsListBinding
import com.example.ca1.databinding.CardCloudJobBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel

class CloudJobsListActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityCloudJobsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudJobsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = CloudJobAdapter(app.cloudJobs)
    }
}

class CloudJobAdapter constructor(private var cloudJobs: List<CloudJobModel>) :
    RecyclerView.Adapter<CloudJobAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCloudJobBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val cloudJob = cloudJobs[holder.adapterPosition]
        holder.bind(cloudJob)
    }

    override fun getItemCount(): Int = cloudJobs.size

    class MainHolder(private val binding : CardCloudJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cloudJob: CloudJobModel) {
            binding.cloudjobTitle.text = cloudJob.title
            binding.description.text = cloudJob.description
        }
    }
}