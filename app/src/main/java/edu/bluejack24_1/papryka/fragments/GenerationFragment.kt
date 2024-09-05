package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.AssistantScheduleAdapter
import edu.bluejack24_1.papryka.databinding.FragmentGenerationBinding
import edu.bluejack24_1.papryka.models.User
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class GenerationFragment : ScheduleBaseFragment() {

    private lateinit var generationBinding: FragmentGenerationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        generationBinding = FragmentGenerationBinding.inflate(inflater, container, false)

        val generationSpinner = generationBinding.spGeneration

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
                getInitials()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        assistantScheduleAdapter = AssistantScheduleAdapter(astSchedules)
        generationBinding.rvSchedule.adapter = assistantScheduleAdapter
        generationBinding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())

        generationBinding.btnView.setOnClickListener {
            astSchedules.clear()
            schedulesLiveData.value = emptyList()
            initSchedules()
            initials.forEach {
                fetchAssistantClassTransaction(it, accessToken)
//                fetchCollegeSchedule(it, accessToken, date!!)
            }
        }

        schedulesLiveData.observe(viewLifecycleOwner, Observer { schedules ->
            assistantScheduleAdapter.notifyDataSetChanged()
            println("All Assistant Schedules: $schedules")
        })


        return generationBinding.root
    }

    override fun getInitials() {
        initials.clear()
        val selectedGeneration = generationBinding.spGeneration.selectedItem.toString()
        fetchAssistant(selectedGeneration)
    }

    companion object {
        fun newInstance(date: String, day: String, shift: String, midCode: String): GenerationFragment {
            val fragment = GenerationFragment()
            fragment.arguments = createArguments(date, day, shift, midCode)
            return fragment
        }
    }

    private fun fetchAssistant(generation: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<User>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService
                        .getAssistantByGeneration(
                            generation = generation
                        )
                }
                if (response != null && response.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        response.forEach {
                            initials.add(it.Username)
                        }
                            println(initials)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}