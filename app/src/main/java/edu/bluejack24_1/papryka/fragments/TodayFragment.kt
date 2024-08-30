package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.ScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentTodayBinding
import edu.bluejack24_1.papryka.models.Schedule

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

        val scheduleAdapter = ScheduleAdapter(schedules)

        vBinding.rvToday.adapter = scheduleAdapter
        vBinding.rvToday.layoutManager = GridLayoutManager(context, 1)
        vBinding.rvToday.setHasFixedSize(true)

        return vBinding.root
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