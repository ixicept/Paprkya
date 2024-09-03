package edu.bluejack24_1.papryka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.adapters.CorrectionAdapter
import edu.bluejack24_1.papryka.databinding.FragmentCorrectionBinding
import edu.bluejack24_1.papryka.models.Correction

class CorrectionFragment : Fragment() {

    private var corrections: List<Correction> = emptyList()
    private lateinit var vBinding: FragmentCorrectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            corrections = it.getParcelableArrayList(ARG_CORRECTIONS) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = FragmentCorrectionBinding.inflate(inflater, container, false)

        val correctionAdapter = CorrectionAdapter(corrections)

        vBinding.rvCorrection.adapter = correctionAdapter
        vBinding.rvCorrection.layoutManager = LinearLayoutManager(context)
        vBinding.rvCorrection.setHasFixedSize(true)

        return vBinding.root
    }

    companion object {
        private const val ARG_CORRECTIONS = "corrections"

        fun newInstance(corrections: List<Correction>): CorrectionFragment {
            val fragment = CorrectionFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_CORRECTIONS, ArrayList(corrections))
            fragment.arguments = args
            return fragment
        }
    }

}