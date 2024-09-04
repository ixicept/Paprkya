
import android.util.Log
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

    private val _casemakings = MutableLiveData<List<Casemaking>>()
    val casemakings: LiveData<List<Casemaking>> get() = _casemakings

    private val _corrections = MutableLiveData<List<Correction>>()
    val corrections: LiveData<List<Correction>> get() = _corrections

    fun fetchCasemaking(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val casemaking = NetworkUtils.apiService.getJobsAssistant("Bearer $accessToken")
                _casemakings.postValue(casemaking)
            } catch (e: Exception) {
                Log.e("JobListViewModel", "Failed to fetch casemaking", e)
            }
        }
    }

    fun fetchCorrection(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val correction = NetworkUtils.apiService.getCorrectionSchedules("Bearer $accessToken")
                _corrections.postValue(correction)
            } catch (e: Exception) {
                Log.e("JobListViewModel", "Failed to fetch correction", e)
            }
        }
    }
}
