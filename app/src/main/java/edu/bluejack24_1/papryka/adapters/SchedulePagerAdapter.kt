package edu.bluejack24_1.papryka.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.bluejack24_1.papryka.fragments.CasemakingFragment
import edu.bluejack24_1.papryka.fragments.CorrectionFragment
import edu.bluejack24_1.papryka.fragments.GenerationFragment
import edu.bluejack24_1.papryka.fragments.InitialFragment
import edu.bluejack24_1.papryka.fragments.PositionFragment
import edu.bluejack24_1.papryka.models.Schedule

class SchedulePagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InitialFragment()
            1 -> GenerationFragment()
            2 -> PositionFragment()

            else -> InitialFragment()

        }
    }

}