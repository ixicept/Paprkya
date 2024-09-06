package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.DayOfWeekScheduleAdapter
import edu.bluejack24_1.papryka.adapters.ScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentThisWeekBinding
import edu.bluejack24_1.papryka.models.Schedule
import java.time.LocalDate

class ThisWeekFragment : Fragment() {

    private lateinit var schedules: List<Schedule>
    private lateinit var vBinding: FragmentThisWeekBinding
    private lateinit var dayScheduleMap: Map<String, List<Schedule>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            schedules = it.getParcelableArrayList(ARG_SCHEDULES) ?: emptyList()
        }
        dayScheduleMap = schedules.groupBy { it.Day.toString() }.toSortedMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentThisWeekBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return vBinding.root
    }

    private fun setupRecyclerView() {
        val dayOfWeekScheduleAdapter = DayOfWeekScheduleAdapter(dayScheduleMap, object : ScheduleAdapter.IOnItemClickCallback {
            override fun onItemClicked(schedule: Schedule) {
                if (schedule.Type == "College") return

                val detailFragment = TeachingDetailFragment.newInstance(schedule)
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        vBinding.rvThisWeek.apply {
            adapter = dayOfWeekScheduleAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    companion object {
        private const val ARG_SCHEDULES = "schedules"

        fun newInstance(schedules: List<Schedule>): ThisWeekFragment {
            val fragment = ThisWeekFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_SCHEDULES, ArrayList(schedules))
            }
            fragment.arguments = args
            return fragment
        }
    }
}
