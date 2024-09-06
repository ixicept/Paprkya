package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel : ViewModel() {

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchClassTransaction(username: String, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val timeoutDuration = 10_000L
            try {
                val response = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getClassTransactionByAssistantUsername(
                        "Bearer $accessToken", username
                    )
                }

                if (response.isNullOrEmpty()) {
                    _errorMessage.postValue("No teaching transactions found.")
                } else {
                    response.forEach {
                        it.Type = "Teaching"
                        it.ShiftCode = getShiftNumber(it.Shift)
                    }
                    val currentList = _schedules.value?.toMutableList() ?: mutableListOf()
                    currentList.addAll(response)
                    _schedules.postValue(currentList)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get class transactions")
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
}
