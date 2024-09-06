package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.CourseOutlineListAdapter
import edu.bluejack24_1.papryka.databinding.FragmentTeachingDetailBinding
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.viewmodels.CourseOutlineViewModel

class TeachingDetailFragment : Fragment() {

    private lateinit var vBinding: FragmentTeachingDetailBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableListAdapter: CourseOutlineListAdapter

    private val courseOutlineViewModel: CourseOutlineViewModel by viewModels()


    private lateinit var schedule: Schedule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentTeachingDetailBinding.inflate(inflater, container, false)

        setupUI()
        observeViewModel()
        fetchDetail()

        return vBinding.root
    }

    private fun setupUI() {
        vBinding.tvSubject.text = String.format("%s\t: %s", getString(R.string.subject), schedule.Subject)
        vBinding.tvClass.text = String.format("%s\t: %s", getString(R.string.classroom), schedule.Class)
        vBinding.tvRoom.text = String.format("%s\t: %s", getString(R.string.teaching_room), schedule.Room)

        expandableListView = vBinding.expandableListView
        expandableListView.setGroupIndicator(null)

        vBinding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        courseOutlineViewModel.courseOutlineDetails.observe(viewLifecycleOwner, Observer { details ->
            if (details != null) {
                val expandableListTitle = ArrayList(details.keys)
                expandableListAdapter = CourseOutlineListAdapter(requireActivity(), expandableListTitle, details)
                expandableListView.setAdapter(expandableListAdapter)
            }
        })

        courseOutlineViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDetail() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            courseOutlineViewModel.fetchCourseOutlineDetails(accessToken, schedule.CourseOutlineId)
        } else {
            Toast.makeText(requireContext(), "Access token not found", Toast.LENGTH_SHORT).show()
        }
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