package edu.bluejack24_1.papryka.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.RoomAdapter
import edu.bluejack24_1.papryka.databinding.FragmentRoomBinding
import edu.bluejack24_1.papryka.models.StatusDetail
import edu.bluejack24_1.papryka.viewmodels.RoomViewModel
import java.util.Locale

class RoomFragment : Fragment() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var etDate: TextView

    private lateinit var vBinding: FragmentRoomBinding

    private val roomList: MutableList<StatusDetail> = mutableListOf()
    private lateinit var roomAdapter: RoomAdapter
    private val roomViewModel: RoomViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentRoomBinding.inflate(inflater, container, false)

        val shiftSpinner = vBinding.spShift
        val campusSpinner = vBinding.spCampus

        setupSpinners(shiftSpinner, campusSpinner)

        dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        etDate = vBinding.etDate
        etDate.setOnClickListener { showDateDialog() }

        val btnView = vBinding.btnView

        btnView.setOnClickListener {
            val date = etDate.text.toString()
            val shift = shiftSpinner.selectedItem.toString().toInt()
            val campus = campusSpinner.selectedItem.toString()
            val unapproved = vBinding.cbUnapproved.isChecked
            val onsite = vBinding.cbOnsite.isChecked

            val shiftCol = mapShiftToColumn(shift)
            val campusCode = mapCampusToCode(campus)

            val accessToken = getAccessToken()
            if (accessToken != null) {
                roomViewModel.fetchRoomTransactions(
                    accessToken,
                    date,
                    shiftCol,
                    campusCode,
                    unapproved
                )
            } else {
                Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        roomViewModel.roomList.observe(viewLifecycleOwner) { rooms ->
            roomAdapter = RoomAdapter(rooms)
            vBinding.rvRoom.adapter = roomAdapter
            vBinding.rvRoom.layoutManager = LinearLayoutManager(context)
            vBinding.rvRoom.setHasFixedSize(true)
            roomAdapter.notifyDataSetChanged()
        }

        roomViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        return vBinding.root
    }

    private fun setupSpinners(shiftSpinner: Spinner, campusSpinner: Spinner) {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.shifts,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shiftSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.campuses,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            campusSpinner.adapter = adapter
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

    private fun getAccessToken(): String? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("ACCESS_TOKEN", null)
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