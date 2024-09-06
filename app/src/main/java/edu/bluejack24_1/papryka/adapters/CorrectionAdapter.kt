package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardCorrectionBinding
import edu.bluejack24_1.papryka.models.Correction

class CorrectionAdapter(private val correctionList: List<Correction>) : RecyclerView.Adapter<CorrectionAdapter.CorrectionViewHolder>() {
    private lateinit var binding: CardCorrectionBinding

    class CorrectionViewHolder(private val binding: CardCorrectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(correction: Correction) {
            binding.correction = correction
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorrectionViewHolder {
        binding = CardCorrectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CorrectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return correctionList.size
    }

    override fun onBindViewHolder(holder: CorrectionViewHolder, position: Int) {
        val corrections = correctionList[position]
        holder.bind(corrections)
    }
}