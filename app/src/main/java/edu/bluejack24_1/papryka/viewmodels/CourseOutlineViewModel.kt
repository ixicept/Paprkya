package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.papryka.models.TeachingDetailResponse
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull


class CourseOutlineViewModel : ViewModel() {
    private val _courseOutlineDetails = MutableLiveData<HashMap<String, List<String>>>()
    val courseOutlineDetails: LiveData<HashMap<String, List<String>>> get() = _courseOutlineDetails

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchCourseOutlineDetails(accessToken: String, courseOutlineId: String) {
        CoroutineScope(Dispatchers.IO).launch() {
            val timeoutDuration = 10_000L
            try {
                val response: TeachingDetailResponse? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getCourseOutlineDetail(
                        "Bearer $accessToken",
                        courseOutlineId
                    )
                }

                if (response != null) {
                    processCourseOutlineDetails(response)
                } else {
                    _errorMessage.postValue("Request timed out or failed. Please try again.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get Teaching Detail transactions")
            }
        }
    }

    private fun processCourseOutlineDetails(response: TeachingDetailResponse) {
        val expandableListDetail = HashMap<String, List<String>>()

        response.Laboratory.forEach { session ->
            val title = "Session ${session.Session}: ${session.Topics}"
            expandableListDetail[title] = session.SubTopics.map { subTopic -> subTopic.Value }
        }

        val sortedExpandableListDetail = expandableListDetail.entries
            .sortedBy {
                it.key.substringAfter("Session ").substringBefore(":").trim().toIntOrNull() ?: Int.MAX_VALUE
            }.associate { it.toPair() }

        _courseOutlineDetails.postValue(sortedExpandableListDetail as HashMap<String, List<String>>?)
    }
}