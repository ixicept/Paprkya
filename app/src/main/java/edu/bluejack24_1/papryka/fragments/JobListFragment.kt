package edu.bluejack24_1.papryka.fragments

import edu.bluejack24_1.papryka.viewmodels.JobListViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.adapters.JobListPagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentJobListBinding
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction

class JobListFragment : Fragment() {

    private lateinit var vBinding: FragmentJobListBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var jobListPagerAdapter: JobListPagerAdapter
    private val corrections = mutableListOf<Correction>()
    private val casemakings = mutableListOf<Casemaking>()
    private val jobListViewModel: JobListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentJobListBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

        jobListPagerAdapter = JobListPagerAdapter(requireActivity(), corrections, casemakings)
        viewPager.adapter = jobListPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) {
                tab, position ->
            when (position) {
                0 -> tab.text = "Correction"
                1 -> tab.text = "Casemaking"
            }
        }.attach()

        fetchJobData()

        return vBinding.root
    }

    private fun fetchJobData() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            jobListViewModel.fetchCasemaking(accessToken)
            jobListViewModel.fetchCorrection(accessToken)

            jobListViewModel.casemakings.observe(viewLifecycleOwner) { casemakings ->
                casemakings?.let {
                    updateCasemakingData(it)
                }
            }

            jobListViewModel.corrections.observe(viewLifecycleOwner) { corrections ->
                corrections?.let {
                    updateCorrectionData(it)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCasemakingData(jobs: List<Casemaking>) {
        casemakings.clear()
        for (job in jobs) {
            if (job.isCaseMaking) {
                job.Type = getDescription(job.Description)
                job.Variation = getVariation(job.Description)
                casemakings.add(job)
            }
        }
        jobListPagerAdapter = JobListPagerAdapter(requireActivity(), corrections, casemakings)
        viewPager.adapter = jobListPagerAdapter
        jobListPagerAdapter.notifyDataSetChanged()
    }

    private fun updateCorrectionData(corrections: List<Correction>) {
        this.corrections.clear()
        this.corrections.addAll(corrections)
        jobListPagerAdapter = JobListPagerAdapter(requireActivity(), this.corrections, casemakings)
        viewPager.adapter = jobListPagerAdapter
        jobListPagerAdapter.notifyDataSetChanged()
    }

    private fun getDescription(description: String): String {
        val words = description.split(" ")
        return if (words.size >= 2) words[words.size - 2] else "Unknown"
    }

    private fun getVariation(description: String): String {
        val words = description.split(" ")
        return if (words.isNotEmpty()) words.last() else "Unknown"
    }

}