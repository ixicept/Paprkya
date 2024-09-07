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
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.adapters.RoomAdapter
import edu.bluejack24_1.papryka.databinding.FragmentRoomBinding
import edu.bluejack24_1.papryka.models.StatusDetail
import edu.bluejack24_1.papryka.utils.ProgressBarUtils
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager
import edu.bluejack24_1.papryka.utils.showDateDialog
import edu.bluejack24_1.papryka.viewmodels.RoomViewModel
import java.util.Locale

class RoomFragment : Fragment() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var etDate: TextView
    private lateinit var vBinding: FragmentRoomBinding

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
        etDate.setOnClickListener { showDateDialog(requireActivity(), etDate, dateFormatter) }

        val btnView = vBinding.btnView

        btnView.setOnClickListener {
            val date = etDate.text.toString()
            val shift = shiftSpinner.selectedItem.toString().toInt()
            val campus = campusSpinner.selectedItem.toString()
            val unapproved = vBinding.cbUnapproved.isChecked
            val onsite = vBinding.cbOnsite.isChecked

            val accessToken = TokenManager.getAccessToken(requireActivity())
            if (accessToken != null) {
                roomViewModel.fetchRoomTransactions(
                    accessToken,
                    date,
                    shift,
                    campus,
                    unapproved
                )
            } else {
                SnackBarUtils.showSnackBar(vBinding.root, "Please login first")
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
                SnackBarUtils.showSnackBar(vBinding.root, it)
            }
        }

        roomViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as MainActivity).showProgressBar()
            } else {
                (activity as MainActivity).hideProgressBar()
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
}