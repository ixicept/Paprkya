package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.bluejack24_1.papryka.adapters.CourseOutlineListAdapter
import edu.bluejack24_1.papryka.databinding.FragmentTeachingDetailBinding
import edu.bluejack24_1.papryka.models.Schedule
import edu.bluejack24_1.papryka.models.TeachingDetailResponse
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class TeachingDetailFragment : Fragment() {

    private lateinit var vBinding: FragmentTeachingDetailBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableListDetail: HashMap<String, List<String>>
    private lateinit var expandableListTitle: List<String>
    private lateinit var expandableListAdapter: CourseOutlineListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentTeachingDetailBinding.inflate(inflater, container, false)

        expandableListView = vBinding.expandableListView
        expandableListDetail = getData()
        expandableListTitle = ArrayList(expandableListDetail.keys)
        expandableListAdapter = CourseOutlineListAdapter(requireActivity(), expandableListTitle, expandableListDetail)
        expandableListView.setAdapter(expandableListAdapter)

        fetchDetail()
        return vBinding.root
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

    private fun getData(): HashMap<String, List<String>> {
        val expandableListDetail = HashMap<String, List<String>>()

        // Session 1 - No child items based on image
        val session1 = listOf<String>() // Empty list as there's no detail shown in the image

        // Session 2 - List of topics related to DNA Composition Analysis
        val session2 = listOf(
            "GC and AT Composition on DNA",
            "Melting Point of DNA",
            "Nucleotide Molecular Weight"
        )

        // Session 3 - No child items based on image
        val session3 = listOf<String>() // Empty list as there's no detail shown in the image

        // Session 4 - No child items based on image
        val session4 = listOf<String>() // Empty list as there's no detail shown in the image

        // Session 5 - No child items based on image
        val session5 = listOf<String>() // Empty list as there's no detail shown in the image

        // Session 6 - No child items based on image
        val session6 = listOf<String>() // Empty list as there's no detail shown in the image

        // Adding the sessions and their corresponding topics to the HashMap
        expandableListDetail["Session 1"] = session1
        expandableListDetail["Session 2"] = session2
        expandableListDetail["Session 3"] = session3
        expandableListDetail["Session 4"] = session4
        expandableListDetail["Session 5"] = session5
        expandableListDetail["Session 6"] = session6

        return expandableListDetail
    }

    private fun fetchDetail(){
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreference", AppCompatActivity.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        CoroutineScope(Dispatchers.IO).launch {
            val timeoutDuration = 10_000L
            try {
                val response: TeachingDetailResponse? = withTimeoutOrNull(timeoutDuration) {
                    NetworkUtils.apiService.getCourseOutlineDetail(
                        "Bearer $accessToken",
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response == null) {
                        Toast.makeText(
                            requireContext(),
                            "Request timed out or failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }  else {
//                        schedules.clear()
//                        schedules.addAll(response)
//                        homePagerAdapter = HomePagerAdapter(requireActivity(), schedules)
//                        viewPager.adapter = homePagerAdapter
//                        homePagerAdapter.notifyDataSetChanged()
                        println("Teaching Detail: $response")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println(e)
                    Toast.makeText(requireContext(), "Failed to get Teaching Detail transactions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}