package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardAssistantScheduleBinding
import edu.bluejack24_1.papryka.models.AssistantSchedule

class AssistantScheduleAdapter(private val schedules: List<AssistantSchedule>) : RecyclerView.Adapter<AssistantScheduleAdapter.AssistantScheduleViewHolder>() {
    private lateinit var binding: CardAssistantScheduleBinding

    class AssistantScheduleViewHolder(private val binding: CardAssistantScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: AssistantSchedule) {
            binding.schedule = schedule
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssistantScheduleViewHolder {
        binding = CardAssistantScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return AssistantScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onBindViewHolder(holder: AssistantScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.bind(schedule)
    }

}