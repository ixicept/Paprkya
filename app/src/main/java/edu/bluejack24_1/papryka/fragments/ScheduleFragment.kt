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
import edu.bluejack24_1.papryka.adapters.JobListPagerAdapter
import edu.bluejack24_1.papryka.adapters.SchedulePagerAdapter
import edu.bluejack24_1.papryka.databinding.FragmentJobListBinding
import edu.bluejack24_1.papryka.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private lateinit var vBinding: FragmentScheduleBinding
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager2;
    private lateinit var jobListPagerAdapter: SchedulePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentScheduleBinding.inflate(inflater, container, false)

        tabLayout = vBinding.tabLayout
        viewPager = vBinding.viewPager

        jobListPagerAdapter = SchedulePagerAdapter(requireActivity())
        viewPager.adapter = jobListPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) {
                tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.by_initial)
                1 -> tab.text = getString(R.string.by_generation)
                2 -> tab.text = getString(R.string.by_position)
            }
        }.attach()

        return vBinding.root
    }

}