package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardScheduleBinding
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.getShiftNumber

class ScheduleAdapter(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    interface IOnItemClickCallback {
        fun onItemClicked(schedule: Schedule)
    }

    private lateinit var onItemClickCallback: IOnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: IOnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private lateinit var binding: CardScheduleBinding

    class ScheduleViewHolder(private val binding: CardScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.schedule = schedule
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        binding = CardScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedules = scheduleList[position]
        holder.bind(schedules)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(scheduleList[position])
        }
    }
}