package com.woynapp.aliskanlik.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.aliskanlik.databinding.ItemEmojiBinding
import com.woynapp.aliskanlik.domain.model.Emoji

class EmojiAdapter(private val listener: AdapterItemListener<Emoji>) :
    ListAdapter<Emoji, EmojiAdapter.EmojiViewHolder>(
        DiffCallBack
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        return EmojiViewHolder(
            ItemEmojiBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class EmojiViewHolder(private val _binding: ItemEmojiBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        init {
            _binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClick(item)
                    }
                }
            }
        }

        fun bind(item: Emoji) {
            _binding.emojiTv.text = item.emoji
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Emoji>() {
            override fun areItemsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
                return oldItem.emoji == newItem.emoji
            }

            override fun areContentsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
                return oldItem == newItem
            }
        }
    }
}