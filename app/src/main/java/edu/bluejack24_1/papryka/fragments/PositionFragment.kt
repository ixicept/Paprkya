package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentGenerationBinding
import edu.bluejack24_1.papryka.databinding.FragmentPositionBinding
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager.getAccessToken
import edu.bluejack24_1.papryka.viewmodels.ScheduleViewModel

class PositionFragment : Fragment() {

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private lateinit var vBinding: FragmentPositionBinding
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
        vBinding = FragmentPositionBinding.inflate(inflater, container, false)

        val positionSpinner = vBinding.spPosition

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.positions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            positionSpinner.adapter = adapter
        }

        positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPosition = positionSpinner.selectedItem.toString()
                scheduleViewModel.fetchInitialsByPosition(selectedPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val accessToken = getAccessToken(requireActivity()) ?: ""

        adapter = AssistantScheduleAdapter(listOf())
        vBinding.rvSchedule.adapter = adapter
        vBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        vBinding.btnView.setOnClickListener {
            scheduleViewModel.loadSchedules(initials, date!!, shift!!, accessToken)
        }

        scheduleViewModel.assistantSchedules.observe(viewLifecycleOwner) { schedules ->
            adapter = AssistantScheduleAdapter(schedules)
            vBinding.rvSchedule.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        scheduleViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as MainActivity).showProgressBar()
            } else {
                (activity as MainActivity).hideProgressBar()
            }
        }

        scheduleViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            SnackBarUtils.showSnackBar(vBinding.root, errorMessage)
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
            PositionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                    putString(ARG_DAY, day)
                    putString(ARG_SHIFT, shift)
                    putString(ARG_MID_CODE, midCode)
                }
            }
    }


}