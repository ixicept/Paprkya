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

class SchedulePagerAdapter(
    fa: FragmentActivity,
    private var date: String,
    private var day: String,
    private var shift: String,
    private var midCode: String
) : FragmentStateAdapter(fa) {

    private lateinit var initialFragment: InitialFragment
    private lateinit var generationFragment: GenerationFragment
    private lateinit var positionFragment: PositionFragment

    fun initFragments() {
        initialFragment = InitialFragment.newInstance(date, day, shift, midCode)
        generationFragment = GenerationFragment.newInstance(date, day, shift, midCode)
        positionFragment = PositionFragment.newInstance(date, day, shift, midCode)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> initialFragment
            1 -> generationFragment
            2 -> positionFragment

            else -> initialFragment

        }
    }

    fun updateDate(newDate: String) {
        initialFragment.updateDate(newDate)
        generationFragment.updateDate(newDate)
        positionFragment.updateDate(newDate)
    }

    fun updateShift(newShift: String) {
        initialFragment.updateShift(newShift)
        generationFragment.updateShift(newShift)
        positionFragment.updateShift(newShift)
    }

}