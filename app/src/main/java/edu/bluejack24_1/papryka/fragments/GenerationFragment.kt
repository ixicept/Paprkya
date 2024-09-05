package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.FragmentGenerationBinding

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

        return generationBinding.root
    }

    override fun getInitials() {
        // Custom logic to get initials for GenerationFragment
        val selectedGeneration = generationBinding.spGeneration.selectedItem.toString()
        initials.add(selectedGeneration)
    }

    companion object {
        fun newInstance(date: String, day: String, shift: String, midCode: String): GenerationFragment {
            val fragment = GenerationFragment()
            fragment.arguments = createArguments(date, day, shift, midCode)
            return fragment
        }
    }
}