package edu.bluejack24_1.papryka.utils

import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Correction
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.models.LoginResponse
import edu.bluejack24_1.papryka.models.Room
import edu.bluejack24_1.papryka.models.RoomTransaction
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.TeachingDetailResponse
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

        @GET("Schedule/GetBinusmayaStudentSchedule")
        suspend fun getStudentSchedule(
            @Header("Authorization") token: String,
            @Query("nim") nim: String,
            @Query("startDate") startDate: String,
            @Query("endDate") endDate: String
        ): CollegeSchedule

        @GET("Room/GetRooms")
        suspend fun getRooms(@Header("Authorization") token: String): List<Room>

        @GET("Room/GetTransactions")
        suspend fun getRoomTransactions(
            @Header("Authorization") token: String,
            @Query("startDate") startDate: String = "2024-09-09", //ini default value
            @Query("endDate") endDate: String = "2024-09-09", //ini default value
            @Query("includeUnapproved") includeUnapproved: Boolean = true //ini default value
        ): RoomTransaction

        @GET("Correction/GetCorrectionSchedules")
        suspend fun getCorrectionSchedules(
            @Header("Authorization") token: String,
            @Query("semesterId") semesterId: String = "a7ff28f1-bd85-410b-b222-a29c619068fa" //EVEN 2023/2024
        ): List<Correction>

        @GET("Schedule/GetJobsAssistant")
        suspend fun getJobsAssistant(
            @Header("Authorization") token: String,
            @Query("mode") mode: String = "history", //future atau history atau current
            @Query("semesterId") semesterId: String = "f6e478f5-428c-4eb2-9aa5-6036488f24b3" //EVEN 2023/2024
            ): List<Casemaking>

        @GET("Assistant/GetUserWithRoles")
        suspend fun getAssistantByRole(
            @Header("Authorization") token: String,
            @Query("role") role: String = "Software Teaching Assistant" //ini default value
        ): List<User>

        @GET("Assistant")
        suspend fun getAssistantByGeneration(
            @Header("Authorization") token: String,
            @Query("initial") initial: String = "", //ini default value
            @Query("generation") generation: String ="24-1", //ini default value
        ): List<User>

        @GET("Course/GetCourseOutlineDetail")
        suspend fun getCourseOutlineDetail(
            @Header("Authorization") token: String,
            @Query("courseOutlineId") courseOutlineId: String = "965b6b1a-e431-e611-903a-d8d385fce79e"
        ): TeachingDetailResponse

        @GET("Assistant/GetAssistantRoles")
        suspend fun getAssistantRoles(
            @Query("username") username: String
        ): List<String>
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

}