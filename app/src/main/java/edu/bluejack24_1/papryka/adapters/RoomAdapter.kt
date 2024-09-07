package edu.bluejack24_1.papryka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.papryka.databinding.CardRoomBinding
import edu.bluejack24_1.papryka.models.StatusDetail

class RoomAdapter(private val roomList: List<StatusDetail>): RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private lateinit var binding: CardRoomBinding

    class RoomViewHolder(private val binding: CardRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(room: StatusDetail) {
            binding.room = room
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        binding = CardRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val rooms = roomList[position]
        holder.bind(rooms)
    }

}