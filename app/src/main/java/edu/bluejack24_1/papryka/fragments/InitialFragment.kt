package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentInitialBinding
import edu.bluejack24_1.papryka.models.AssistantSchedule
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.utils.getDayOfWeek
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class InitialFragment : Fragment() {

    private lateinit var vBinding: FragmentInitialBinding
    private var initials = mutableListOf<String>()
    private var schedules = mutableListOf<Schedule>()
    private var astSchedules = mutableListOf<AssistantSchedule>()
    private val schedulesLiveData = MutableLiveData<List<AssistantSchedule>>()
    private lateinit var assistantScheduleAdapter: AssistantScheduleAdapter

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
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        val etInitial = vBinding.etInitial

        assistantScheduleAdapter = AssistantScheduleAdapter(astSchedules)
        vBinding.rvSchedule.adapter = assistantScheduleAdapter
        vBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        vBinding.btnView.setOnClickListener {
            initials.clear()
            astSchedules.clear()
            schedulesLiveData.value = emptyList()
            val parts = etInitial.text.toString().split(";")
            initials.addAll(parts)
            initials.forEach {
                fetchAssistantClassTransaction(it, accessToken)
                fetchCollegeSchedule(it, accessToken, date!!)
            }
        }

        schedulesLiveData.observe(viewLifecycleOwner, Observer { schedules ->
            assistantScheduleAdapter.notifyDataSetChanged()
            println("All Assistant Schedules: $schedules")
        })

        return vBinding.root
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

    fun updateDate(newDate: String) {
        date = newDate
    }

    fun updateShift(newShift: String) {
        shift = newShift
    }

    private fun fetchAssistantClassTransaction(username: String, accessToken: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<Schedule>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getClassTransactionByAssistantUsername(
                        "Bearer $accessToken",
                        username
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response == null) {
                        Toast.makeText(
                            requireContext(),
                            "Request timed out or failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (response.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No class transactions found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        schedules.clear()

                        response.forEach {
                            it.Type = "Teaching"
                            it.ShiftCode = getShiftNumber(it.Shift)

                            schedules.add(it)
                        }

                        schedules = schedules.filter {
                            it.Day == getDayOfWeek("yyyy-MM-dd", date!!) &&
                                    it.ShiftCode.toInt() == shift?.toInt()
                        }.toMutableList()

                        if (schedules.isNotEmpty()) astSchedules.add(AssistantSchedule(username, schedules[0]))
                        schedulesLiveData.value = astSchedules
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to get class transactions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchCollegeSchedule(nim: String, accessToken: String?, date: String) {

        if (isAdded) {
            CoroutineScope(Dispatchers.IO).launch {
                val timeoutDuration = 20_000L
                try {
                    val response: CollegeSchedule? = withTimeoutOrNull(timeoutDuration) {
                        NetworkUtils.apiService.getStudentSchedule(
                            "Bearer $accessToken",
                            nim,
                            date,
                            date
                        )
                    }

                    withContext(Dispatchers.Main) {
                        if (response == null) {
                            Toast.makeText(
                                requireContext(),
                                "Request timed out or failed. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (response.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "No college transactions found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            schedules.clear()
                            schedules.addAll(processCollegeSchedule(response))
                            schedules = schedules.filter {
                                it.Day == getDayOfWeek("yyyy-MM-dd", date) &&
                                        it.ShiftCode.toInt() == shift?.toInt()
                            }.toMutableList()

                            if (schedules.isNotEmpty()) astSchedules.add(AssistantSchedule(nim, schedules[0]))
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get college transactions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

}