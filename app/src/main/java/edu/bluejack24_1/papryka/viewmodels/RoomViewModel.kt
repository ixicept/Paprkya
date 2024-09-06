package edu.bluejack24_1.papryka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.papryka.models.StatusDetail
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RoomViewModel : ViewModel() {
    private val _roomList = MutableLiveData<List<StatusDetail>>()
    val roomList: LiveData<List<StatusDetail>> get() = _roomList

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchRoomTransactions(
        accessToken: String,
        date: String,
        shift: Int,
        campus: String,
        unapproved: Boolean
    ) {
        val shiftCol = mapShiftToColumn(shift)
        val campusCode = mapCampusToCode(campus)

        CoroutineScope(Dispatchers.IO).launch() {
            try {
                val roomTransactions = NetworkUtils.apiService.getRoomTransactions(
                    "Bearer $accessToken", date, date, unapproved
                )

                val rooms = mutableListOf<StatusDetail>()

                roomTransactions.Details.forEach { details ->
                    if (details.Campus == campusCode) {
                        if (details.StatusDetails[shiftCol].isNotEmpty()) {
                            details.StatusDetails[shiftCol].forEach {
                                it.RoomName = details.RoomName
                                rooms.add(it)
                            }
                        } else {
                            val emptyStatusDetail = StatusDetail(
                                Description = "Available", RoomName = details.RoomName
                            )
                            rooms.add(emptyStatusDetail)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    _roomList.value = rooms
                }
            } catch (e: Exception) {
                println("Error fetching room transactions: $e")
                withContext(Dispatchers.Main) {
                    _error.value = "Failed to get room transactions"
                }
            }
        }
    }

    private fun mapShiftToColumn(shift: Int): Int {
        return when (shift) {
            1 -> 1
            2 -> 3
            3 -> 5
            4 -> 7
            5 -> 9
            6 -> 11
            7 -> 12
            else -> 0
        }
    }

    private fun mapCampusToCode(campus: String): String {
        return when (campus) {
            "Anggrek" -> "ANGGREK"
            "Syahdan" -> "SYAHDAN"
            "Kijang" -> "KIJANG"
            "Alam Sutera" -> "ASM"
            "Bandung" -> "BANDUNG"
            "Bekasi" -> "BKSM"
            "Malang" -> "MALANG"
            "Semarang" -> "SEMARANG"
            else -> "ANGGREK"
        }
    }
}