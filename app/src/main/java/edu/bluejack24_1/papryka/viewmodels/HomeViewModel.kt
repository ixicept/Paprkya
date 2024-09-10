package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private val _userInitial = MutableLiveData<String>()
    val userInitial: LiveData<String> get() = _userInitial

    private val _nim = MutableLiveData<String>()
    val nim: LiveData<String> get() = _nim

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> get() = _schedules

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> get() = _successMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun fetchUserInformation(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                withContext(Dispatchers.Main) {
                    _userInitial.value = response.Username
                    _nim.value = response.UserId
                    storeData(response.Username, response.Name)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to get user information"
                }
            } finally {
                stopLoading()
            }
        }
    }

    fun fetchAllSchedules(username: String, nim: String, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            try {
                val teachingDeferred = async {
                    NetworkUtils.apiService.getClassTransactionByAssistantUsername(
                        "Bearer $accessToken", username
                    )
                }

                val collegeDeferred = async {
                    val (startDate, endDate) = getDateRange("weekly")
                    NetworkUtils.apiService.getCollegeSchedules(
                        "Bearer $accessToken",
                        nim,
                        startDate = startDate,
                        endDate = endDate
                    )
                }

                val teachingResponse = teachingDeferred.await()
                val collegeResponse = collegeDeferred.await()

                val combinedSchedules = mutableListOf<Schedule>()

                teachingResponse?.let {
                    combinedSchedules.addAll(it.map { schedule ->
                        schedule.Type = "Teaching"
                        schedule.ShiftCode = getShiftNumber(schedule.Shift)
                        schedule
                    })
                }

                collegeResponse?.let {
                    combinedSchedules.addAll(it.map { collegeSchedule ->
                        val startTime = collegeSchedule.StartAt.split("T")[1].substring(0, 5)
                        val endTime = collegeSchedule.EndAt.split("T")[1].substring(0, 5)
                        val time = "$startTime - $endTime"

                        val dateString = collegeSchedule.StartAt.split("T")[0]
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val date = LocalDate.parse(dateString, formatter)

                        val dayOfWeek = date.dayOfWeek.value
                        val subject = "${collegeSchedule.CourseCode} - ${collegeSchedule.CourseTitle}"

                        Schedule(
                            subject,
                            collegeSchedule.ClassName,
                            dayOfWeek,
                            0F,
                            time,
                            collegeSchedule.ClassName,
                            "College",
                            ""
                        )
                    })
                }

                withContext(Dispatchers.Main) {
                    if (combinedSchedules.isEmpty()) {
                        _errorMessage.value = "No schedules found"
                    } else {
                        _schedules.value = combinedSchedules
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to get schedules"
                }
            } finally {
                stopLoading()
            }
        }
    }

    private var activeTasks = 0

    private fun startLoading() {
        activeTasks++
        _isLoading.postValue(true)
    }

    private fun stopLoading() {
        activeTasks--
        if (activeTasks == 0) {
            _isLoading.postValue(false)
        }
    }

    private fun storeData(initial: String, name: String) {
        val user = hashMapOf(
            "initial" to initial,
            "name" to name
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                databaseReference.child(initial).setValue(user)
                withContext(Dispatchers.Main) {
                    _successMessage.value = "User data stored successfully"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to store user data: ${e.message}"
                }
            }
        }
    }

}