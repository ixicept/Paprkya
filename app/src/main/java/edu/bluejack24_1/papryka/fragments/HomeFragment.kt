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
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var vBinding: FragmentHomeBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentHomeBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

        val schedules = seedData()

        viewPager.adapter = HomePagerAdapter(requireActivity(), schedules)

        TabLayoutMediator(tabLayout, viewPager) {
            tab, position ->
            when (position) {
                0 -> tab.text = "Today"
                1 -> tab.text = "This week"
            }
        }.attach()

        fetchUserInformation()


        return vBinding.root
    }

    private fun seedData(): List<Schedule> {
        val schedules = arrayListOf<Schedule>()

        val dummyData = listOf(
            Schedule("Algorithm & Programming", 1, 1.0F, "628", "Teaching"),
            Schedule("Deep Learning", 1, 4.0F, "710", "Teaching"),
            Schedule("Object Oriented Programming", 1, 6.0F, "614", "Teaching"),
        )

        schedules.addAll(dummyData)

        return schedules
    }

    private fun fetchUserInformation() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                    withContext(Dispatchers.Main) {
                        val initial = response.Username
                        println("User initial: $initial")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to get user information", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
    }

}