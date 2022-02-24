package com.kenetic.blockchainvs.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.blockchainvs.appviewmodel.MainViewModel
import com.kenetic.blockchainvs.databinding.PartyListItemBinding

class PartyListAdapter(private val viewModel: MainViewModel) :
    ListAdapter<Int, PartyListAdapter.PartyViewHolder>(diffCallback) {

    class PartyViewHolder(private val itemBinding: PartyListItemBinding, private val viewModel: MainViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(id: Int) {
            val currentPartyData = viewModel.getById(id)
            itemBinding.apply {
                // TODO: apply changes to ui elements, search for image saving methods
                descriptionTextview
                partyImage
                partyName
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        return PartyViewHolder(
            PartyListItemBinding.inflate(LayoutInflater.from(parent.context)),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

