package edu.bluejack24_1.papryka.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.models.User
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel(){

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    fun fetchUserInformation(accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                _user.postValue(response)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user info", e)
                _loginError.postValue("Failed to get user information")
            }
        }
    }

    fun loginUser(initial: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = LoginRequest(initial, password)
                val response = NetworkUtils.apiService.login(loginRequest)
                val accessToken = response.access_token
                _accessToken.postValue(accessToken)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Login failed", e)
                _loginError.postValue("Wrong credentials")
            }
        }
    }
}