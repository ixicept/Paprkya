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

        vBinding = FragmentThisWeekBinding.inflate(inflater, container, false)

        val dayScheduleMap: Map<String, List<Schedule>> = schedules.groupBy { it.Day.toString() }
        dayScheduleMap.toSortedMap()

        val scheduleAdapter = DayOfWeekScheduleAdapter(dayScheduleMap)

        vBinding.rvThisWeek.adapter = scheduleAdapter
        vBinding.rvThisWeek.layoutManager = LinearLayoutManager(context)
        vBinding.rvThisWeek.setHasFixedSize(true)

        return vBinding.root
    }

    companion object {
        private const val ARG_SCHEDULES = "schedules"

        fun newInstance(schedules: List<Schedule>): ThisWeekFragment {
            val fragment = ThisWeekFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_SCHEDULES, ArrayList(schedules))
            fragment.arguments = args
            return fragment
        }
    }

}