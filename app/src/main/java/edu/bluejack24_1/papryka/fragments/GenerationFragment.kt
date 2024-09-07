package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentGenerationBinding
import edu.bluejack24_1.papryka.databinding.FragmentInitialBinding
import edu.bluejack24_1.papryka.models.User
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.viewmodels.ScheduleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class GenerationFragment : Fragment() {

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private lateinit var vBinding: FragmentGenerationBinding
    private lateinit var adapter: AssistantScheduleAdapter
    private val initials = mutableListOf<String>()

    private var date: String? = null
    private var day: String? = null
    private var shift: String? = null
    private var midCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE)
            day = it.getString(ARG_DAY)
            shift = it.getString(ARG_SHIFT)
            midCode = it.getString(ARG_MID_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentGenerationBinding.inflate(inflater, container, false)

        val generationSpinner = vBinding.spGeneration

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.generations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            generationSpinner.adapter = adapter
        }

        generationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGeneration = generationSpinner.selectedItem.toString()
                scheduleViewModel.fetchInitials(selectedGeneration)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val accessToken = getAccessToken()

        adapter = AssistantScheduleAdapter(listOf())
        vBinding.rvSchedule.adapter = adapter
        vBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        vBinding.btnView.setOnClickListener {
            scheduleViewModel.loadSchedules(initials, date!!, shift!!, accessToken)
        }

        scheduleViewModel.assistantSchedules.observe(viewLifecycleOwner) { schedules ->
            println(schedules)
            adapter = AssistantScheduleAdapter(schedules)
            vBinding.rvSchedule.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        scheduleViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        scheduleViewModel.initials.observe(viewLifecycleOwner) { initials ->
            initials?.let {
                this.initials.clear()
                this.initials.addAll(it)
            }
            println("Initials: $this.initials")
        }


        return vBinding.root
    }

    private fun getAccessToken(): String {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("ACCESS_TOKEN", null) ?: ""
    }

    fun updateDate(newDate: String) {
        date = newDate
    }

    fun updateShift(newShift: String) {
        shift = newShift
    }

    companion object {
        private const val ARG_DATE = "date"
        private const val ARG_DAY = "day"
        private const val ARG_SHIFT = "shift"
        private const val ARG_MID_CODE = "midCode"

        fun newInstance(date: String, day: String, shift: String, midCode: String) =
            GenerationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                    putString(ARG_DAY, day)
                    putString(ARG_SHIFT, shift)
                    putString(ARG_MID_CODE, midCode)
                }
            }
    }


}