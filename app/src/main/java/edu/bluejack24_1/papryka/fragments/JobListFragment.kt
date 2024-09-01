package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.adapters.JobListPagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.databinding.FragmentJobListBinding
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobListFragment : Fragment() {

    private lateinit var vBinding: FragmentJobListBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var jobListPagerAdapter: JobListPagerAdapter
    private val corrections = mutableListOf<Correction>()
    private val casemakings = mutableListOf<Casemaking>()

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

        fetchCasemaking()
        fetchCorrection()

        return vBinding.root
    }

    private fun fetchCasemaking() {
        //casemaking
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val casemaking = NetworkUtils.apiService.getJobsAssistant("Bearer $accessToken")
                    casemakings.addAll(casemaking)
                    withContext(Dispatchers.Main) {
                        proccesCasemaking(casemaking)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to get jobs", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

    //agak bruteforce
    private fun proccesCasemaking(jobs: List<Casemaking>) {
        for (job in jobs) {
            if (job.isCaseMaking){
                val variation= getVariation(job.Description)
                val type = getDescription(job.Description)
                println("Job Type: $type, Variation: $variation")
            }
        }
    }

    private fun getDescription(description: String): String {
        println(description)
        val words = description.split(" ")
        println(words)
        return if (words.size >= 2) {
            words[words.size - 2]
        } else {
            "Unknown"
        }
    }

    private fun getVariation(description: String): String {
        val words = description.split(" ")
        return if (words.isNotEmpty()) {
            words.last()
        } else {
            "Unknown"
        }
    }


    private fun fetchCorrection() {
        //correction
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val correction = NetworkUtils.apiService.getCorrectionSchedules("Bearer $accessToken")
                    corrections.addAll(correction)
                    withContext(Dispatchers.Main) {
                        print("Correction: $correction")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to get jobs", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

}