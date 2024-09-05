package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentInitialBinding
import edu.bluejack24_1.papryka.models.AssistantSchedule
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDayOfWeek
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

abstract class ScheduleBaseFragment : Fragment() {

    protected lateinit var vBinding: FragmentInitialBinding
    protected var initials = mutableListOf<String>()
    protected var schedules = mutableListOf<Schedule>()
    protected var astSchedules = mutableListOf<AssistantSchedule>()
    protected val schedulesLiveData = MutableLiveData<List<AssistantSchedule>>()
    protected lateinit var assistantScheduleAdapter: AssistantScheduleAdapter

    protected var date: String? = null
    protected var day: String? = null
    protected var shift: String? = null
    protected var midCode: String? = null

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
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        assistantScheduleAdapter = AssistantScheduleAdapter(astSchedules)
        vBinding.rvSchedule.adapter = assistantScheduleAdapter
        vBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        vBinding.btnView.setOnClickListener {
            initials.clear()
            astSchedules.clear()
            schedulesLiveData.value = emptyList()
            getInitials()
            initSchedules()
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

    protected abstract fun getInitials()

    protected fun initSchedules() {
        initials.forEach {
            astSchedules.add(
                AssistantSchedule(
                    it,
                    Schedule("Available", "", 0, 0F, "", "", "", "")
                )
            )
            checkInitial(it)
        }
    }

    protected fun checkInitial(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<String>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getAssistantRoles(username)
                }

                withContext(Dispatchers.Main) {
                    if (response == null) {
                        Toast.makeText(
                            requireContext(),
                            "Request timed out or failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (response.isEmpty()) {
                        println("test")
                        astSchedules.removeIf {
                            it.initial == username
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get assistant roles",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    protected fun fetchAssistantClassTransaction(username: String, accessToken: String?) {
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

                        if (schedules.isNotEmpty()) {
                            astSchedules.find {
                                it.initial == username
                            }?.let {
                                it.schedule = schedules[0]
                            }
                        }
                        schedulesLiveData.value = astSchedules
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get class transactions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    protected fun fetchCollegeSchedule(nim: String, accessToken: String?, date: String) {

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

                            if (schedules.isNotEmpty()) {
                                astSchedules.find {
                                    it.initial == nim
                                }?.let {
                                    it.schedule = schedules[0]
                                }
                            }
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

    companion object {
        private const val ARG_DATE = "date"
        private const val ARG_DAY = "day"
        private const val ARG_SHIFT = "shift"
        private const val ARG_MID_CODE = "midCode"

        fun createArguments(date: String, day: String, shift: String, midCode: String): Bundle {
            return Bundle().apply {
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
}