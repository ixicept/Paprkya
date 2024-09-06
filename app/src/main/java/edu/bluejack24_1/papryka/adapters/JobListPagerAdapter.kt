package edu.bluejack24_1.papryka.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.bluejack24_1.papryka.fragments.CasemakingFragment
import edu.bluejack24_1.papryka.fragments.CorrectionFragment
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Correction

class JobListPagerAdapter(
    activity: FragmentActivity,
    private var corrections: List<Correction>,
    private var casemakings: List<Casemaking>
) : FragmentStateAdapter(activity) {

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

    fun updateCasemakingData(casemakings: List<Casemaking>) {
        this.casemakings = casemakings
        notifyItemChanged(1)
    }

    fun updateCorrectionData(corrections: List<Correction>) {
        println(corrections)
        this.corrections = corrections
        notifyItemChanged(0)
    }
}
