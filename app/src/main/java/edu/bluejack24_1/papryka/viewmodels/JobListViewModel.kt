package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class JobListViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _casemakings = MutableLiveData<List<Casemaking>>()
    val casemakings: LiveData<List<Casemaking>> get() = _casemakings

    private val _corrections = MutableLiveData<List<Correction>>()
    val corrections: LiveData<List<Correction>> get() = _corrections

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchCasemaking(accessToken: String) {
        viewModelScope.launch {
            startLoading()
            try {
                val casemaking = NetworkUtils.apiService.getJobsAssistant("Bearer $accessToken")
                casemaking.forEach {
                    it.Type = getDescription(it.Description)
                    it.Variation = getVariation(it.Description)
                }
                _casemakings.value = casemaking.filter {
                    it.isCaseMaking
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch casemaking: $e")
            } finally {
                stopLoading()
            }
        }
    }

    fun fetchCorrection(accessToken: String) {
        viewModelScope.launch {
            startLoading()
            try {
                val correction = NetworkUtils.apiService.getCorrectionSchedules("Bearer $accessToken")
                _corrections.value = correction
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch correction: $e")
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

    private fun getDescription(description: String): String {
        val words = description.split(" ")
        return if (words.size >= 2) words[words.size - 2] else "Unknown"
    }

    private fun getVariation(description: String): String {
        val words = description.split(" ")
        return if (words.isNotEmpty()) words.last() else "Unknown"
    }
}

