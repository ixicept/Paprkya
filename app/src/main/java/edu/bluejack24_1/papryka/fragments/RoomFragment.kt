package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchRoomTransactions()
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    private fun fetchRoomTransactions() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        //note: di room transaction bagian status detail, [] itu berarti kosong, terus kalo room terus [] terus room lagi artinya [] itu kek spasinya gitu
        // [] [] [] -> cuma yang tengah doang kosong yang lain cuman spasi
        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val rooms = NetworkUtils.apiService.getRooms("Bearer $accessToken")
                    val roomTransactions = NetworkUtils.apiService.getRoomTransactions("Bearer $accessToken")
                    withContext(Dispatchers.Main) {
                        println("ini rooms $rooms")
                        println("ini room transactions $roomTransactions")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to get room transactions", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

}