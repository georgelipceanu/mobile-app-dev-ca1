package com.example.ca1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.CardCloudJobBinding
import com.example.ca1.models.CloudJobModel

interface CloudJobListener {
    fun onCloudJobClick(cloudjob: CloudJobModel)
    fun onCloudJobDeleteIconClick(cloudjob: CloudJobModel)
}

class CloudJobAdapter (private var cloudJobs: List<CloudJobModel>,
                       private val listener: CloudJobListener) :
    RecyclerView.Adapter<CloudJobAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCloudJobBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val cloudJob = cloudJobs[holder.adapterPosition]
        holder.bind(cloudJob, listener)
    }

    override fun getItemCount(): Int = cloudJobs.size

    class MainHolder(private val binding: CardCloudJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cloudJob: CloudJobModel, listener: CloudJobListener) {
            binding.cloudjobTitle.text = cloudJob.title
            binding.description.text = cloudJob.description
            binding.root.setOnClickListener { listener.onCloudJobClick(cloudJob) }
            binding.deleteButton.setOnClickListener { listener.onCloudJobDeleteIconClick(cloudJob) }
        }
    }
}