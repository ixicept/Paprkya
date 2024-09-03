package edu.bluejack24_1.papryka.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.RoomAdapter
import edu.bluejack24_1.papryka.databinding.FragmentRoomBinding
import edu.bluejack24_1.papryka.models.StatusDetail
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class RoomFragment : Fragment() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var etDate: TextView

    private lateinit var vBinding: FragmentRoomBinding

    private val roomList: MutableList<StatusDetail> = mutableListOf()
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentRoomBinding.inflate(inflater, container, false)

        val shiftSpinner = vBinding.spShift

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.shifts,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shiftSpinner.adapter = adapter
        }

        val campusSpinner = vBinding.spCampus

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.campuses,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            campusSpinner.adapter = adapter
        }

        dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        etDate = vBinding.etDate
        etDate.setOnClickListener { showDateDialog() }

        val btnView = vBinding.btnView

        btnView.setOnClickListener {
            val date = etDate.text.toString()
            val shift = shiftSpinner.selectedItem.toString()
            val campus = campusSpinner.selectedItem.toString()
            val unapproved = vBinding.cbUnapproved.isChecked
            val onsite = vBinding.cbOnsite.isChecked

            fetchRoomTransactions(date, shift.toInt(), campus, unapproved, onsite)
        }

        return vBinding.root
    }

    private fun fetchRoomTransactions(date: String, shift: Int, campus: String, unapproved: Boolean, onsite: Boolean) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        // note: di room transaction bagian status detail, [] itu berarti kosong, terus kalo room terus [] terus room lagi artinya [] itu kek spasinya gitu
        // [] [] [] -> cuma yang tengah doang kosong yang lain cuman spasi

        val shiftCol = when (shift) {
            1 -> 1
            2 -> 3
            3 -> 5
            4 -> 7
            5 -> 9
            6 -> 11
            7 -> 12
            else -> 0
        }

        val campusCode = when (campus) {
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

        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val roomTransactions = NetworkUtils.apiService.getRoomTransactions(
                        "Bearer $accessToken",
                        date,
                        date,
                        unapproved
                    )
                    withContext(Dispatchers.Main) {
                        roomList.clear()
                        roomTransactions.Details.forEach { details ->
                            if (details.Campus == campusCode) {
                                if (details.StatusDetails[shiftCol].isNotEmpty()) {
                                    details.StatusDetails[shiftCol].forEach {
                                        it.RoomName = details.RoomName
                                        roomList.add(it)
                                    }
                                } else {
                                    val emptyStatusDetail = StatusDetail(Description = "Available", RoomName = details.RoomName)
                                    roomList.add(emptyStatusDetail)
                                }
                            }
                        }

                        roomAdapter = RoomAdapter(roomList)
                        vBinding.rvRoom.adapter = roomAdapter
                        vBinding.rvRoom.layoutManager = LinearLayoutManager(context)
                        vBinding.rvRoom.setHasFixedSize(true)
                        roomAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get room transactions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDateDialog() {
        val newCalendar: Calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newDate: Calendar = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                etDate.text = dateFormatter.format(newDate.time)
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}