package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.ActivityTeachingDetailBinding
import edu.bluejack24_1.papryka.models.Schedule

class TeachingDetailFragment : Fragment() {

    private lateinit var binding: ActivityTeachingDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityTeachingDetailBinding.inflate(layoutInflater)

        return inflater.inflate(R.layout.fragment_teaching_detail, container, false)
    }

    companion object {
        private const val ARG_SCHEDULE = "schedule"

        fun newInstance(schedule: Schedule): TeachingDetailFragment {
            val fragment = TeachingDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_SCHEDULE, schedule)
            fragment.arguments = args
            return fragment
        }
    }

}