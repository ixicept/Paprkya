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
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.models.CollegeSchedule
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.getDateRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class HomeFragment : Fragment() {

    private lateinit var vBinding: FragmentHomeBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var homePagerAdapter: HomePagerAdapter
    private val schedules = mutableListOf<Schedule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentHomeBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

        homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
        viewPager.adapter = homePagerAdapter

        TabLayoutMediator(tabLayout, viewPager) {
            tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.today)
                1 -> tab.text = getString(R.string.this_week)
            }
        }.attach()

        fetchUserInformation()

        return vBinding.root
    }

    private fun fetchUserInformation() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            val initial = response.Username
                            val nim = response.BinusianNumber
                            vBinding.tvInitial.text = initial
                            println("User initial: $initial")
                            println("User nim: $nim")
                            fetchCollegeSchedule(nim, accessToken, "weekly")
                            fetchClassTransaction(initial, accessToken)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Failed to get user information", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchClassTransaction(username: String, accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: List<Schedule>? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getClassTransactionByAssistantUsername(
                        "Bearer $accessToken",
                        username
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response == null) {
                        Toast.makeText(
                            requireContext(),
                            "Request timed out or failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (response.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No college transactions found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        schedules.clear()
                        schedules.addAll(response)
                        homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
                        viewPager.adapter = homePagerAdapter
                        homePagerAdapter.notifyDataSetChanged()

                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to get class transactions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchCollegeSchedule(nim: String, accessToken: String,timeframe: String) {
        val (startDate, endDate) = getDateRange(timeframe)

        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: CollegeSchedule? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getStudentSchedule(
                        "Bearer $accessToken",
                        nim,
                        startDate,
                        endDate
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response == null) {
                        Toast.makeText(
                            requireContext(),
                            "Request timed out or failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (response.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No college transactions found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
//                        schedules.clear()
//                        schedules.addAll(response)
//                        homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
//                        viewPager.adapter = homePagerAdapter
//                        homePagerAdapter.notifyDataSetChanged()
                        println("College Schedule: $response")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to get college transactions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}