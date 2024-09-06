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
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
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

        setupViewPagerAndTabs()
        fetchJobData()

        return vBinding.root
    }

    private fun setupViewPagerAndTabs() {
        jobListPagerAdapter = JobListPagerAdapter(requireActivity(), mutableListOf(), mutableListOf())
        viewPager.adapter = jobListPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Correction" else "Casemaking"
        }.attach()
    }

    private fun fetchJobData() {
        val accessToken = getAccessToken()
        if (accessToken != null) {
            jobListViewModel.fetchCasemaking(accessToken)
            jobListViewModel.fetchCorrection(accessToken)

            jobListViewModel.corrections.observe(viewLifecycleOwner) { corrections ->
                corrections?.let {
                    updateCorrectionData(it)
                }
            }

            jobListViewModel.casemakings.observe(viewLifecycleOwner) { casemakings ->
                casemakings?.let {
                    updateCasemakingData(it)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCasemakingData(casemakings: List<Casemaking>) {
        this.casemakings.clear()
        this.casemakings.addAll(casemakings)
        reinitPagerAdapter()
    }

    private fun updateCorrectionData(corrections: List<Correction>) {
        this.corrections.clear()
        this.corrections.addAll(corrections)
        reinitPagerAdapter()
    }

    private fun reinitPagerAdapter() {
        jobListPagerAdapter = JobListPagerAdapter(requireActivity(), corrections, casemakings)
        viewPager.adapter = jobListPagerAdapter
        jobListPagerAdapter.notifyDataSetChanged()
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }
}
