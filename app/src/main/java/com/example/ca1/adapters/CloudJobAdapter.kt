package com.example.ca1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.CardCloudJobBinding
import com.example.ca1.models.CloudJobModel
import com.squareup.picasso.Picasso

interface CloudJobListener {
    fun onCloudJobClick(id: String, cloudjob: CloudJobModel)
    fun onCloudJobDeleteIconClick(id: String, cloudjob: CloudJobModel)
}

class CloudJobAdapter (private var cloudJobs: List<Pair<String, CloudJobModel>>, // ref: https://www.baeldung.com/kotlin/pair-class
                       private val listener: CloudJobListener) :
    RecyclerView.Adapter<CloudJobAdapter.MainHolder>() {
    private val displayedJobs = cloudJobs.toMutableList() // ref: https://stackoverflow.com/questions/46846025/how-to-clone-or-copy-a-list-in-kotlin

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCloudJobBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val (id, cloudJob) = displayedJobs[holder.adapterPosition]
        holder.bind(id, cloudJob, listener)
    }

    override fun getItemCount(): Int = displayedJobs.size

    fun submitList(newList: List<Pair<String, CloudJobModel>>) {
        displayedJobs.clear()
        displayedJobs.addAll(newList)
        notifyDataSetChanged()
    }

    class MainHolder(private val binding: CardCloudJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(id: String, cloudJob: CloudJobModel, listener: CloudJobListener) {
            binding.cloudjobTitle.text = cloudJob.title
            binding.description.text = cloudJob.description
            binding.deadline.text = if (!cloudJob.deadline.isNullOrBlank()) "Deadline: ${cloudJob.deadline}" else "No Deadline"
            binding.cpu.text = "CPU: ${cloudJob.CPUType}"
            binding.replicaCount.text = "Replica Count: ${cloudJob.replicas}"
            binding.duration.isVisible = false
            if (cloudJob.duration > -1) {
                binding.duration.isVisible = true
                binding.duration.text = "${cloudJob.duration} mins"
            }
            binding.emissions.text = if (cloudJob.emissions != null) "Emissions: ${cloudJob.emissions} g CO₂" else "Emissions: Couldn't Calculate"
            if (cloudJob.imageUrl.isNotBlank()) {
                Picasso.get().load(cloudJob.imageUrl).resize(200, 200).into(binding.imageIcon)
            } else {
                binding.imageIcon.setImageDrawable(null)
            }
            binding.root.setOnClickListener { listener.onCloudJobClick(id, cloudJob) }
            binding.deleteButton.setOnClickListener { listener.onCloudJobDeleteIconClick(id, cloudJob) }
        }
    }
}