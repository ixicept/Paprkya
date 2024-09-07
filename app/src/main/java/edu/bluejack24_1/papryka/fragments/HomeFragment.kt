package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.viewmodels.HomeViewModel
import edu.bluejack24_1.papryka.viewmodels.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var vBinding: FragmentHomeBinding
    private lateinit var homePagerAdapter: HomePagerAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentHomeBinding.inflate(inflater, container, false)

        homePagerAdapter = HomePagerAdapter(requireActivity(), mutableListOf())
        vBinding.viewPager.adapter = homePagerAdapter

        setupTabLayout()
        observeViewModel()
        homeViewModel.fetchUserInformation(getAccessToken())

        return vBinding.root
    }

    private fun setupTabLayout() {
        TabLayoutMediator(vBinding.tabLayout, vBinding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.today)
                1 -> tab.text = getString(R.string.this_week)
            }
        }.attach()
    }

    private fun observeViewModel() {
        homeViewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            homePagerAdapter = HomePagerAdapter(requireActivity(), schedules.toMutableList())
            vBinding.viewPager.adapter = homePagerAdapter
            homePagerAdapter.notifyDataSetChanged()
        }

        homeViewModel.userInitial.observe(viewLifecycleOwner) { initial ->
            vBinding.tvInitial.text = initial
            homeViewModel.fetchClassTransaction(initial, getAccessToken())
        }

        homeViewModel.nim.observe(viewLifecycleOwner) { nim ->
            homeViewModel.fetchCollegeSchedule(nim, getAccessToken(), "weekly")
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserInformation() {
        val accessToken = getAccessToken()

        if (accessToken != null) {
            userViewModel.fetchUserInformation(accessToken)
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAccessToken(): String {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("ACCESS_TOKEN", null) ?: ""
    }
}
