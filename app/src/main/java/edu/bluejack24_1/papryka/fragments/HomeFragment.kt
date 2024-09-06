package edu.bluejack24_1.papryka.fragments

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
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.processCollegeSchedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.utils.getDayOfWeek
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import edu.bluejack24_1.papryka.utils.getDateRange
import edu.bluejack24_1.papryka.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var vBinding: FragmentHomeBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var homePagerAdapter: HomePagerAdapter
    private val schedules = mutableListOf<Schedule>()

    private val homeViewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentHomeBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

        homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
        viewPager.adapter = homePagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.today)
                1 -> tab.text = getString(R.string.this_week)
            }
        }.attach()

        observeViewModel()

        fetchUserData()

        return vBinding.root
    }

    private fun observeViewModel() {
        homeViewModel.userInitial.observe(viewLifecycleOwner) { initial ->
            vBinding.tvInitial.text = initial
        }

        homeViewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
            viewPager.adapter = homePagerAdapter
            homePagerAdapter.notifyDataSetChanged()
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchUserData() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let {
            homeViewModel.fetchUserInformation(it)
            homeViewModel.userInitial.observe(viewLifecycleOwner) { initial ->
                homeViewModel.fetchClassTransaction(initial, it)
            }
            homeViewModel.nim.observe(viewLifecycleOwner) { nim ->
                val (startDate, endDate) = getDateRange("weekly")
                homeViewModel.fetchCollegeSchedule(nim, it, startDate, endDate)
            }
        } ?: run {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

}