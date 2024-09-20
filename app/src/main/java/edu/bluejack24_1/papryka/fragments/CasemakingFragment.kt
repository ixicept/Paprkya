package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.CasemakingAdapter
import edu.bluejack24_1.papryka.databinding.FragmentCasemakingBinding
import edu.bluejack24_1.papryka.fragments.TodayFragment.Companion
import edu.bluejack24_1.papryka.models.Casemaking
import edu.bluejack24_1.papryka.models.Schedule

class CasemakingFragment : Fragment() {

    private lateinit var casemakingList: List<Casemaking>
    private lateinit var vBinding: FragmentCasemakingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            casemakingList = it.getParcelableArrayList(ARG_CASEMAKING) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentCasemakingBinding.inflate(inflater, container, false)

        val casemakingAdapter = CasemakingAdapter(casemakingList)

        vBinding.rvCasemaking.adapter = casemakingAdapter
        vBinding.rvCasemaking.layoutManager = LinearLayoutManager(context)
        vBinding.rvCasemaking.setHasFixedSize(true)

        if (casemakingList.isEmpty()) {
            vBinding.tvNoData.visibility = View.VISIBLE
        } else {
            vBinding.tvNoData.visibility = View.GONE
        }

        return vBinding.root
    }

    companion object {
        private const val ARG_CASEMAKING = "casemaking"

        fun newInstance(casemakingList: List<Casemaking>): CasemakingFragment {
            val fragment = CasemakingFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_CASEMAKING, ArrayList(casemakingList))
            fragment.arguments = args
            return fragment
        }
    }

}