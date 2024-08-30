package edu.bluejack24_1.papryka.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.bluejack24_1.papryka.fragments.ThisWeekFragment
import edu.bluejack24_1.papryka.fragments.TodayFragment
import edu.bluejack24_1.papryka.models.Schedule

class HomePagerAdapter(fa: FragmentActivity, private val schedules: List<Schedule>): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFragment.newInstance(schedules)
            1 -> ThisWeekFragment()

            else -> TodayFragment()

        }
    }

}