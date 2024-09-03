package edu.bluejack24_1.papryka.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.ScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentTodayBinding
import edu.bluejack24_1.papryka.models.Schedule
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalDate

class TodayFragment : Fragment() {

    private lateinit var schedules: List<Schedule>
    private lateinit var vBinding: FragmentTodayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            schedules = it.getParcelableArrayList(ARG_SCHEDULES) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vBinding = FragmentTodayBinding.inflate(inflater, container, false)

        if (getToday(schedules).isEmpty()) {
            vBinding.noSchedule.visibility = View.VISIBLE
        } else {
            vBinding.noSchedule.visibility = View.GONE
        }

        val scheduleAdapter = ScheduleAdapter(getToday(schedules))

        vBinding.rvToday.adapter = scheduleAdapter
        vBinding.rvToday.layoutManager = LinearLayoutManager(context)
        vBinding.rvToday.setHasFixedSize(true)

        scheduleAdapter.setOnItemClickCallback(object : ScheduleAdapter.IOnItemClickCallback {
            override fun onItemClicked(schedule: Schedule) {
                val detailFragment = TeachingDetailFragment.newInstance(schedule)

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })


        return vBinding.root
    }

    private fun getToday(schedules: List<Schedule>): List<Schedule> {
        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.dayOfWeek.value
        val todaySchedules = schedules.filter {
            it.Day == dayOfWeek
        }
        return todaySchedules
    }

    companion object {
        private const val ARG_SCHEDULES = "schedules"

        fun newInstance(schedules: List<Schedule>): TodayFragment {
            val fragment = TodayFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_SCHEDULES, ArrayList(schedules))
            fragment.arguments = args
            return fragment
        }
    }

}