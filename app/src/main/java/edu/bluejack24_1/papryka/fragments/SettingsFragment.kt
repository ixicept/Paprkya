package edu.bluejack24_1.papryka.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.FragmentSettingsBinding
import edu.bluejack24_1.papryka.utils.getCurrentLanguage
import edu.bluejack24_1.papryka.utils.setLanguageForApp
import java.util.Locale


class SettingsFragment : Fragment() {

    private lateinit var vBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        val languageSpinner = vBinding.spLanguage

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        val defaultLanguage = getCurrentLanguage(requireContext())

        val position = when (defaultLanguage) {
            "en" -> 0
            "in" -> 1
            else -> 0
        }
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                val languageCode = when (selectedLanguage) {
                    "English" -> "en"
                    "Bahasa Indonesia" -> "in"
                    else -> "not-set"
                }

                setLanguageForApp(requireActivity(), languageCode)

                val sharedPref = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("selected_language", languageCode)
                    apply()
                }
                refreshUI()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                setLanguageForApp("not-set")
            }
        }

        return vBinding.root
    }

    private fun refreshUI() {
        vBinding.tvSettings.text = getString(R.string.settings)
        vBinding.tvLanguage.text = getString(R.string.language)
    }


}