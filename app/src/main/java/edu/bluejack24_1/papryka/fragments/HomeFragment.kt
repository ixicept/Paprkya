package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.HomePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentHomeBinding
import edu.bluejack24_1.papryka.models.Schedule

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

}