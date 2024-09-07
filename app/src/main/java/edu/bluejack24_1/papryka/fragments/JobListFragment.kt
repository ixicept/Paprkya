package edu.bluejack24_1.papryka.fragments

import edu.bluejack24_1.papryka.viewmodels.JobListViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.adapters.JobListPagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentJobListBinding
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction
import edu.bluejack24_1.papryka.utils.ProgressBarUtils
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager.getAccessToken

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
        observeViewModel()
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
        val accessToken = getAccessToken(requireActivity())
        if (accessToken != null) {
            jobListViewModel.fetchCasemaking(accessToken)
            jobListViewModel.fetchCorrection(accessToken)

        } else {
            SnackBarUtils.showSnackBar(vBinding.root, "Please login first")
        }
    }

    private fun observeViewModel() {

        jobListViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as MainActivity).showProgressBar()
            } else {
                (activity as MainActivity).hideProgressBar()
            }
        }

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

        jobListViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                SnackBarUtils.showSnackBarWithAction(vBinding.root, it, "Retry") {
                    fetchJobData()
                }
            }
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

}
