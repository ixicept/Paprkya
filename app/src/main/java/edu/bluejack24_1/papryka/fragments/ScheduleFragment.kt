package edu.bluejack24_1.papryka.fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.SchedulePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentScheduleBinding
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.showDateDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class ScheduleFragment : Fragment() {

    private lateinit var vBinding: FragmentScheduleBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var jobListPagerAdapter: SchedulePagerAdapter

    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var etDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentScheduleBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

//        val daySpinner = vBinding.spDay
//
//        ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.days,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            daySpinner.adapter = adapter
//        }
//
//        val midCodeSpinner = vBinding.spMidCode
//
//        ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.mid_codes,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            midCodeSpinner.adapter = adapter
//        }

        val shiftSpinner = vBinding.spShift

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.shifts,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shiftSpinner.adapter = adapter
        }

        val selectedDay = ""
        var selectedShift = shiftSpinner.selectedItem.toString()
        val selectedMidCode = ""

        dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        etDate = vBinding.etDate
        etDate.setOnClickListener { showDateDialog(requireContext(), etDate, dateFormatter) }

        jobListPagerAdapter = SchedulePagerAdapter(
            requireActivity(),
            etDate.text.toString(), selectedDay, selectedShift, selectedMidCode
        )
        viewPager.adapter = jobListPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.by_initial)
                1 -> tab.text = getString(R.string.by_generation)
                2 -> tab.text = getString(R.string.by_position)
            }
        }.attach()

        etDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println(s.toString())
                jobListPagerAdapter.updateDate(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        shiftSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedShift = parent.getItemAtPosition(position).toString()
                jobListPagerAdapter.updateShift(selectedShift)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                jobListPagerAdapter.updateShift("1")
            }
        }

        return vBinding.root
    }
}