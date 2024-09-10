package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager
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

        val accessToken = TokenManager.getAccessToken(requireActivity()) ?: ""

        setupTabLayout()
        observeViewModel()
        homeViewModel.fetchUserInformation(accessToken)

        val refreshLayout = vBinding.pullToRefresh
        refreshLayout.setOnRefreshListener {
            homeViewModel.userInitial.value?.let { initial ->
                homeViewModel.nim.value?.let { nim ->
                    homeViewModel.fetchAllSchedules(initial, nim, accessToken)
                }
            }
            refreshLayout.isRefreshing = false
        }

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
        val accessToken = TokenManager.getAccessToken(requireActivity()) ?: ""

        homeViewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            homePagerAdapter = HomePagerAdapter(requireActivity(), schedules.toMutableList())
            vBinding.viewPager.adapter = homePagerAdapter
            homePagerAdapter.notifyDataSetChanged()
        }

        homeViewModel.userInitial.observe(viewLifecycleOwner) { initial ->
            vBinding.tvInitial.text = initial
            homeViewModel.nim.value?.let { nim ->
                homeViewModel.fetchAllSchedules(initial, nim, accessToken)
            }
        }

        homeViewModel.nim.observe(viewLifecycleOwner) { nim ->
            homeViewModel.userInitial.value?.let { initial ->
                homeViewModel.fetchAllSchedules(initial, nim, accessToken)
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            SnackBarUtils.showSnackBarWithAction(vBinding.root, message, "Retry") {
                homeViewModel.userInitial.value?.let { initial ->
                    homeViewModel.nim.value?.let { nim ->
                        homeViewModel.fetchAllSchedules(initial, nim, accessToken)
                    }
                }
            }
        }

        homeViewModel.successMessage.observe(viewLifecycleOwner) { message ->
            SnackBarUtils.showSnackBar(vBinding.root, message)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as MainActivity).showProgressBar()
            } else {
                (activity as MainActivity).hideProgressBar()
            }
        }
    }

}
