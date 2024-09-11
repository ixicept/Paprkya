package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.models.AssistantSchedule
import edu.bluejack24_1.papryka.models.CollegeDetail
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.User
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

    private val _userId = MutableLiveData<List<String>>()
    val userId: LiveData<List<String>> get() = _userId

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
                        val userId = fetchUserId(initial)
                        val collegeSchedule = fetchCollegeSchedule(userId, accessToken, date, date, shift)
                        if (collegeSchedule != null) {
                            val subject = collegeSchedule.CourseCode + " - " + collegeSchedule.CourseTitle
                            val shift = getShiftFromStartAt(collegeSchedule.StartAt, collegeSchedule.EndAt)
                            assistantSchedules.add(
                                AssistantSchedule(
                                    initial,
                                    Schedule(subject, collegeSchedule.ClassName, 0, shift, "", collegeSchedule.ClassName, "College", "")
                                )
                            )
                        } else {
                            assistantSchedules.add(
                                AssistantSchedule(
                                    initial,
                                    Schedule("Free", "", 0, 0F, "", "", "", "")
                                )
                            )
                        }
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

    fun fetchInitialsByGeneration(generation: String) {
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

    fun fetchInitialsByPosition(position: String) {
        viewModelScope.launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<User>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getAssistantByRole(position)
                }
                println("Test response: $response")
                println("Test generation: $position")
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

    private suspend fun fetchUserId(initial: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val generation = initial.split("-").first()
                val response = apiService.getAssistantByGeneration(initial, generation)
                response.firstOrNull()?.UserId ?: ""
            } catch (e: Exception) {
                _error.postValue("Failed to fetch user ID")
                ""
            }
        }
    }


    private suspend fun fetchClassTransaction(
        initial: String,
        date: String,
        shift: String,
        accessToken: String
    ): Schedule? {
        return withContext(Dispatchers.IO) {
            try {
                var response = apiService.getClassTransactionByAssistantUsername(
                    "Bearer $accessToken",
                    initial
                )
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

    private suspend fun fetchCollegeSchedule(
        userId: String,
        accessToken: String,
        startDate: String,
        endDate: String,
        shift: String
    ): CollegeDetail? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCollegeSchedules(
                    "Bearer $accessToken",
                    userId,
                    startDate = startDate,
                    endDate = endDate
                )
                println("College schedule response: $response")
                response.filter {
                    val scheduleShift = getShiftFromStartAt(it.StartAt, it.EndAt)
                    println("Schedule shift: $scheduleShift")
                    scheduleShift.toInt() == shift.toInt()
                }.firstOrNull()
            } catch (e: Exception) {
                _error.postValue("Failed to fetch college schedule")
                null
            }
        }
    }

    fun getShiftFromStartAt(startAt: String,endAt: String): Float {
        val startTime = startAt.split("T")[1].substring(0, 5)
        val endTime = endAt.split("T")[1].substring(0, 5)
        val time = startTime + " - " + endTime
        println(time)
        return getShiftNumber(time)
    }

}
