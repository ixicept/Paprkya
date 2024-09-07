package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.models.AssistantSchedule
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.User
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDayOfWeek
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class ScheduleViewModel : ViewModel() {

    private val _assistantSchedules = MutableLiveData<List<AssistantSchedule>>()
    val assistantSchedules: LiveData<List<AssistantSchedule>> get() = _assistantSchedules

    private val _initials = MutableLiveData<List<String>>()
    val initials: LiveData<List<String>> get() = _initials

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val apiService = NetworkUtils.apiService

    fun loadSchedules(initials: List<String>, date: String, shift: String, accessToken: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val assistantSchedules = mutableListOf<AssistantSchedule>()
            println("Load schedules: $initials, $date, $shift, $accessToken")

            for (initial in initials) {
                val isValid = checkInitial(initial)
                if (isValid) {
                    val schedule = fetchClassTransaction(initial, date, shift, accessToken)
                    if (schedule != null) {
                        assistantSchedules.add(AssistantSchedule(initial, schedule))
                    } else {
                        assistantSchedules.add(
                            AssistantSchedule(
                                initial,
                                Schedule("Available", "", 0, 0F, "", "", "", "")
                            )
                        )
//                        val collegeSchedule = fetchCollegeSchedule(initial, accessToken)
//                        if (collegeSchedule != null) {
//                            assistantSchedules.add(AssistantSchedule(initial, collegeSchedule))
//                        } else {
//                            // Assume free schedule if no class or college schedule found
//                            assistantSchedules.add(AssistantSchedule(initial, Schedule("Free", 0, 0)))
//                        }
                    }
                }
            }

            _assistantSchedules.value = assistantSchedules
            _isLoading.postValue(false)
        }
    }

    private suspend fun checkInitial(initial: String): Boolean {
        println("Check initial: $initial")
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAssistantRoles(initial)
                response.isNotEmpty()
            } catch (e: Exception) {
                _error.postValue("Failed to check initial")
                false
            }
        }
    }

    fun fetchInitials(generation: String) {
        viewModelScope.launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<User>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getAssistantByGeneration(generation = generation)
                }
                println("Test response: $response")
                println("Test generation: $generation")
                if (!response.isNullOrEmpty()) {
                    _initials.postValue(response.map { it.Username })
                } else {
                    _initials.postValue(emptyList())
                }
            } catch (e: Exception) {
                _error.postValue("Failed to fetch assistant initials")
            }
        }
    }


    private suspend fun fetchClassTransaction(initial: String, date: String, shift: String, accessToken: String): Schedule? {
        return withContext(Dispatchers.IO) {
            try {
                var response = apiService.getClassTransactionByAssistantUsername(
                    "Bearer $accessToken",
                    initial
                )
                println(response)
                response.forEach {
                    it.Type = "Teaching"
                    it.ShiftCode = getShiftNumber(it.Shift)
                }

                response = response.filter {
                    it.Day == getDayOfWeek("yyyy-MM-dd", date) &&
                            it.ShiftCode.toInt() == shift.toInt()
                }.toMutableList()

                response.firstOrNull()
            } catch (e: Exception) {
                _error.postValue("Failed to fetch class transactions")
                null
            }
        }
    }

//    private suspend fun fetchCollegeSchedule(initial: String, accessToken: String): Schedule? {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.getStudentSchedule("Bearer $accessToken", initial, "startDate", "endDate")
//                response?.let { processCollegeSchedule(it) } // Replace with actual logic to select the appropriate schedule
//            } catch (e: Exception) {
//                _error.postValue("Failed to fetch college schedule")
//                null
//            }
//        }
//    }
}
