package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.adapters.ScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentInitialBinding
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager.getAccessToken
import edu.bluejack24_1.papryka.viewmodels.ScheduleViewModel

class InitialFragment : Fragment() {

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private lateinit var vBinding: FragmentInitialBinding
    private lateinit var adapter: AssistantScheduleAdapter

    private var date: String? = null
    private var day: String? = null
    private var shift: String? = null
    private var midCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE)
            day = it.getString(ARG_DAY)
            shift = it.getString(ARG_SHIFT)
            midCode = it.getString(ARG_MID_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentInitialBinding.inflate(inflater, container, false)

        adapter = AssistantScheduleAdapter(listOf())
        vBinding.rvSchedule.adapter = adapter
        vBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        scheduleViewModel.assistantSchedules.observe(viewLifecycleOwner) { schedules ->
            println(schedules)
            adapter = AssistantScheduleAdapter(schedules)
            vBinding.rvSchedule.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        scheduleViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as MainActivity).showProgressBar()
            } else {
                (activity as MainActivity).hideProgressBar()
            }
        }

        scheduleViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            SnackBarUtils.showSnackBar(vBinding.root, errorMessage)
        }

        val accessToken = getAccessToken(requireActivity()) ?: ""

        vBinding.btnView.setOnClickListener {
            val initials = getInitials()
            scheduleViewModel.loadSchedules(initials, date!!, shift!!, accessToken)
        }

        return vBinding.root
    }

    private fun getInitials(): List<String> {
        val etInitial = vBinding.etInitial
        val parts = etInitial.text.toString().trim().split(";")
        return parts.filter {
            it.isNotEmpty()
        }
    }

    fun updateDate(newDate: String) {
        date = newDate
    }

    fun updateShift(newShift: String) {
        shift = newShift
    }

    companion object {
        private const val ARG_DATE = "date"
        private const val ARG_DAY = "day"
        private const val ARG_SHIFT = "shift"
        private const val ARG_MID_CODE = "midCode"

        fun newInstance(date: String, day: String, shift: String, midCode: String) =
            InitialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                    putString(ARG_DAY, day)
                    putString(ARG_SHIFT, shift)
                    putString(ARG_MID_CODE, midCode)
                }
            }
    }

}
