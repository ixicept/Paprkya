package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardCasemakingBinding
import edu.bluejack24_1.papryka.models.Casemaking

class CasemakingAdapter(private val casemakingList: List<Casemaking>) : RecyclerView.Adapter<CasemakingAdapter.CasemakingViewHolder>() {
    interface IOnItemClickCallback {
        fun onItemClicked(casemaking: Casemaking)
    }

    private lateinit var onItemClickCallback: IOnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: IOnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private lateinit var binding: CardCasemakingBinding

    class CasemakingViewHolder(private val binding: CardCasemakingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(casemaking: Casemaking) {
            binding.casemaking = casemaking
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasemakingViewHolder {
        binding = CardCasemakingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CasemakingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return casemakingList.size
    }

    override fun onBindViewHolder(holder: CasemakingViewHolder, position: Int) {
        val casemakings = casemakingList[position]
        holder.bind(casemakings)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(casemakingList[position])
        }
    }
}