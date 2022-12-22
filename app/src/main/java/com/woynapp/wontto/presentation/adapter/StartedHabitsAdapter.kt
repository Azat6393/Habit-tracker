package com.woynapp.wontto.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.wontto.R
import com.woynapp.wontto.databinding.ItemStartedHabitBinding
import com.woynapp.wontto.domain.model.HabitWithDays

class StartedHabitsAdapter(private val listener: StartedHabitsItemListener) :
    ListAdapter<HabitWithDays, StartedHabitsAdapter.HabitViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        return HabitViewHolder(
            ItemStartedHabitBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item, position == 0)
    }

    inner class HabitViewHolder(private val _binding: ItemStartedHabitBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        init {
            _binding.bannerAdsCv.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClickAd()
                    }
                }
            }
            _binding.content.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClick(item)
                    }
                }
            }
        }

        fun bind(item: HabitWithDays, isFirstItem: Boolean) {
            _binding.apply {
                icon.text = item.habit.emoji
                nameTv.text = item.habit.name
                totalTv.text = item.days.size.toString()
                val doneList = item.days.filter { it.type == 1 }
                val mistakeList = item.days.filter { it.type == 2 }
                doneTv.text = doneList.size.toString()
                mistakeTv.text = mistakeList.size.toString()
                if (isFirstItem) {
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                        _binding.kargoBulBanner.setImageResource(R.drawable.banner_dark_theme)
                    } else {
                        _binding.kargoBulBanner.setImageResource(R.drawable.banner_light_theme)
                    }
                    _binding.bannerAdsCv.isVisible = true
                } else _binding.kargoBulBanner.isVisible = false
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<HabitWithDays>() {
            override fun areItemsTheSame(oldItem: HabitWithDays, newItem: HabitWithDays): Boolean {
                return oldItem.habit.id == newItem.habit.id
            }

            override fun areContentsTheSame(
                oldItem: HabitWithDays,
                newItem: HabitWithDays
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}