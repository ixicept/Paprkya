package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.FragmentSettingsBinding
import edu.bluejack24_1.papryka.utils.getLanguageCode
import edu.bluejack24_1.papryka.utils.setLanguageForApp
import edu.bluejack24_1.papryka.viewmodels.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var vBinding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupSpinner()
        observeViewModel()

        vBinding.btnLogout.setOnClickListener {
            settingsViewModel.logout(requireContext())
        }

        return vBinding.root
    }

    private fun setupSpinner() {
        val languageSpinner = vBinding.spLanguage
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        val defaultLanguage = settingsViewModel.getCurrentLanguage(requireContext())
        val position = when (defaultLanguage) {
            "en" -> 0
            "in" -> 1
            else -> 0
        }
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                val languageCode = getLanguageCode(selectedLanguage)
                settingsViewModel.setSelectedLanguage(languageCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        settingsViewModel.selectedLanguage.observe(viewLifecycleOwner) { languageCode ->
            setLanguageForApp(requireActivity(), languageCode)
            settingsViewModel.saveLanguage(requireContext(), languageCode)
            refreshUI()
        }
    }

    private fun refreshUI() {
        vBinding.tvSettings.text = getString(R.string.settings)
        vBinding.tvLanguage.text = getString(R.string.language)
        vBinding.btnLogout.text = getString(R.string.logout)
    }
}
