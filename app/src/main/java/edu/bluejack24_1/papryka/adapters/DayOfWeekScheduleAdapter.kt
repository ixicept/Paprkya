package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardDayOfWeekBinding
import edu.bluejack24_1.papryka.models.DayOfWeekSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.getDayFromInt
import java.time.LocalDate

class DayOfWeekScheduleAdapter(private val scheduleList: Map<String, List<Schedule>>) :
    RecyclerView.Adapter<DayOfWeekScheduleAdapter.DayOfWeekScheduleViewHolder>() {

    private lateinit var binding: CardDayOfWeekBinding

    class DayOfWeekScheduleViewHolder(private val binding: CardDayOfWeekBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Int, schedules: List<Schedule>) {
            binding.tvDay.text = getDayFromInt(day)

            val scheduleAdapter = ScheduleAdapter(schedules)
            binding.rvSchedule.adapter = scheduleAdapter
            binding.rvSchedule.layoutManager = GridLayoutManager(binding.root.context, 1)
            binding.rvSchedule.setHasFixedSize(true)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekScheduleViewHolder {
        binding = CardDayOfWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayOfWeekScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return scheduleList.keys.size
    }

    override fun onBindViewHolder(holder: DayOfWeekScheduleViewHolder, position: Int) {
        val day = scheduleList.keys.elementAt(position)
        val schedules = scheduleList[day] ?: emptyList()
        holder.bind(day.toInt(), schedules)
    }
}