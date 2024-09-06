package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.CardDayOfWeekBinding
import edu.bluejack24_1.papryka.fragments.TeachingDetailFragment
import edu.bluejack24_1.papryka.models.DayOfWeekSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.getDayFromInt
import java.time.LocalDate

class DayOfWeekScheduleAdapter(
    private val scheduleList: Map<String, List<Schedule>>,
    private val onItemClickCallback: ScheduleAdapter.IOnItemClickCallback
) : RecyclerView.Adapter<DayOfWeekScheduleAdapter.DayOfWeekScheduleViewHolder>() {

    class DayOfWeekScheduleViewHolder(private val binding: CardDayOfWeekBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Int, schedules: List<Schedule>, onItemClickCallback: ScheduleAdapter.IOnItemClickCallback) {
            binding.tvDay.text = getDayFromInt(binding.root.context, day)

            binding.tvNoSchedule.visibility = if (schedules.isEmpty()) View.VISIBLE else View.GONE

            val sortedSchedules = schedules.sortedBy { it.ShiftCode }

            val scheduleAdapter = ScheduleAdapter(sortedSchedules).apply {
                setOnItemClickCallback(onItemClickCallback)
            }
            binding.rvSchedule.apply {
                adapter = scheduleAdapter
                layoutManager = GridLayoutManager(binding.root.context, 1)
                setHasFixedSize(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekScheduleViewHolder {
        val binding = CardDayOfWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayOfWeekScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int = 6

    override fun onBindViewHolder(holder: DayOfWeekScheduleViewHolder, position: Int) {
        val day = position + 1
        val schedules = scheduleList[day.toString()] ?: emptyList()
        holder.bind(day, schedules, onItemClickCallback)
    }
}
