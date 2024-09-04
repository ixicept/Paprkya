package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.FragmentGenerationBinding

class GenerationFragment : Fragment() {

    private lateinit var vBinding: FragmentGenerationBinding

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

        return vBinding.root
    }

}