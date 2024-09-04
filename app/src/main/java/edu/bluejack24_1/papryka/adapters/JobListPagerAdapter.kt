package edu.bluejack24_1.papryka.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.bluejack24_1.papryka.fragments.CasemakingFragment
import edu.bluejack24_1.papryka.fragments.CorrectionFragment
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction

class JobListPagerAdapter(fa: FragmentActivity, private val corrections: MutableList<Correction>, private val casemakings: MutableList<Casemaking>): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CorrectionFragment.newInstance(corrections)
            1 -> CasemakingFragment.newInstance(casemakings)

            else -> CorrectionFragment.newInstance(corrections)

        }
    }

}