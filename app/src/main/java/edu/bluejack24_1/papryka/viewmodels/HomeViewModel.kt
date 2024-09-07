package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel : ViewModel() {

    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    private val _userInitial = MutableLiveData<String>()
    val userInitial: LiveData<String> get() = _userInitial

    private val _nim = MutableLiveData<String>()
    val nim: LiveData<String> get() = _nim

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> get() = _schedules

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserInformation(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                withContext(Dispatchers.Main) {
                    _userInitial.value = response.Username
                    _nim.value = response.BinusianNumber
                    storeData(response.Username, response.Name)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to get user information"
                }
            }
        }
    }

    fun fetchClassTransaction(username: String, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val timeoutDuration = 10_000L
                val response: List<Schedule>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getClassTransactionByAssistantUsername(
                        "Bearer $accessToken", username
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response.isNullOrEmpty()) {
                        _errorMessage.value = "No teaching transactions found"
                    } else {
                        val updatedSchedules = response.map {
                            it.Type = "Teaching"
                            it.ShiftCode = getShiftNumber(it.Shift)
                            it
                        }
                        _schedules.value = updatedSchedules
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to get class transactions"
                }
            }
        }
    }

    fun fetchCollegeSchedule(nim: String, accessToken: String, timeframe: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val (startDate, endDate) = getDateRange(timeframe)
            val timeoutDuration = 20_000L
            try {
                val response = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getStudentSchedule(
                        "Bearer $accessToken",
                        nim,
                        startDate,
                        endDate
                    )
                }

                if (response.isNullOrEmpty()) {
                    _errorMessage.postValue("No college transactions found.")
                } else {
                    val currentList = _schedules.value?.toMutableList() ?: mutableListOf()
                    currentList.addAll(processCollegeSchedule(response))
                    _schedules.postValue(currentList)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get college transactions")
            }
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
                    _errorMessage.value = "User data stored successfully"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to store user data: ${e.message}"
                }
            }
        }
    }

}