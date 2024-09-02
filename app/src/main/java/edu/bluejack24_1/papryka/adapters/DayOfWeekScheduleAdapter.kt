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

class DayOfWeekScheduleAdapter(private val scheduleList: Map<String, List<Schedule>>) :
    RecyclerView.Adapter<DayOfWeekScheduleAdapter.DayOfWeekScheduleViewHolder>() {

    private lateinit var binding: CardDayOfWeekBinding

    class DayOfWeekScheduleViewHolder(private val binding: CardDayOfWeekBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Int, schedules: List<Schedule>, onItemClickCallback: ScheduleAdapter.IOnItemClickCallback) {
            binding.tvDay.text = getDayFromInt(binding.root.context, day)

            if (schedules.isEmpty()) {
                binding.tvNoSchedule.visibility = View.VISIBLE
            } else {
                binding.tvNoSchedule.visibility = View.GONE
            }

            val scheduleAdapter = ScheduleAdapter(schedules)
            scheduleAdapter.setOnItemClickCallback(onItemClickCallback)
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
        return 7
    }

    override fun onBindViewHolder(holder: DayOfWeekScheduleViewHolder, position: Int) {
        val schedules = scheduleList[(position + 1).toString()] ?: emptyList()

        holder.bind(position + 1, schedules, object : ScheduleAdapter.IOnItemClickCallback {
            override fun onItemClicked(schedule: Schedule) {
                (holder.itemView.context as? AppCompatActivity)?.let { activity ->
                    val detailFragment = TeachingDetailFragment.newInstance(schedule)
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, detailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        })
    }




}