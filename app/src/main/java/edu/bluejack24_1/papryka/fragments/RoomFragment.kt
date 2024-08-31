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
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.FragmentRoomBinding
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

        shiftSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedNumber = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedNumber", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when nothing is selected, if necessary
            }
        }

        dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        etDate = vBinding.etDate
        etDate.setOnClickListener { showDateDialog() }
        fetchRoomTransactions()

        return vBinding.root
    }

    private fun fetchRoomTransactions() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        // note: di room transaction bagian status detail, [] itu berarti kosong, terus kalo room terus [] terus room lagi artinya [] itu kek spasinya gitu
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