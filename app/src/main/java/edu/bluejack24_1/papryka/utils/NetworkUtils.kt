package edu.bluejack24_1.papryka.utils

import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.models.LoginResponse
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

object NetworkUtils {
    private const val BASE_URL = "https://bluejack.binus.ac.id/lapi/api/"

    private val retrofit by lazy { Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }

    interface ApiService {
        @POST("Account/LogOn")
        suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

        @GET("Account/Me")
        suspend fun getUserInfo(@Header("Authorization") token: String): User

        @GET("Assistant/GetClassTransactionByAssistantUsername")
        suspend fun getClassTransactionByAssistantUsername(
            @Header("Authorization") token: String,
            @Query("username") username: String,
            @Query("semesterId") semesterId: String = "be992b30-4b38-4361-8404-25f2d6912754"
        ): List<Schedule>
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

}