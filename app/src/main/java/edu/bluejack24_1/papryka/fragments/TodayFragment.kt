package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.ScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentTodayBinding
import edu.bluejack24_1.papryka.models.Schedule
import java.time.LocalDate

class TodayFragment : Fragment() {

    private lateinit var schedules: List<Schedule>
    private lateinit var vBinding: FragmentTodayBinding
    private lateinit var todaySchedules: List<Schedule>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            schedules = it.getParcelableArrayList(ARG_SCHEDULES) ?: emptyList()
        }
        todaySchedules = getToday(schedules).sortedBy { it.ShiftCode }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vBinding = FragmentTodayBinding.inflate(inflater, container, false)

        vBinding.noSchedule.visibility = if (todaySchedules.isEmpty()) View.VISIBLE else View.GONE

        setupRecyclerView()

        return vBinding.root
    }

    private fun setupRecyclerView() {
        val scheduleAdapter = ScheduleAdapter(todaySchedules)
        vBinding.rvToday.adapter = scheduleAdapter
        vBinding.rvToday.layoutManager = LinearLayoutManager(context)
        vBinding.rvToday.setHasFixedSize(true)

        scheduleAdapter.setOnItemClickCallback(object : ScheduleAdapter.IOnItemClickCallback {
            override fun onItemClicked(schedule: Schedule) {
                if (schedule.Type == "College") return
                val detailFragment = TeachingDetailFragment.newInstance(schedule)

                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }

    private fun getToday(schedules: List<Schedule>): List<Schedule> {
        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.dayOfWeek.value
        return schedules.filter { it.Day == dayOfWeek }
    }

    companion object {
        private const val ARG_SCHEDULES = "schedules"

        fun newInstance(schedules: List<Schedule>): TodayFragment {
            val fragment = TodayFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_SCHEDULES, ArrayList(schedules))
            }
            fragment.arguments = args
            return fragment
        }
    }
}
