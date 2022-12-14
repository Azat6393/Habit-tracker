package com.woynapp.aliskanlik.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.aliskanlik.databinding.ItemStartedHabitBinding
import com.woynapp.aliskanlik.domain.model.HabitWithDays

class StartedHabitsAdapter(private val listener: AdapterItemListener<HabitWithDays>) :
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
            holder.bind(item)
    }

    inner class HabitViewHolder(private val _binding: ItemStartedHabitBinding) :
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

        fun bind(item: HabitWithDays) {
            _binding.apply {
                nameTv.text = item.habit.name
                totalTv.text = item.days.size.toString()
                val doneList = item.days.filter { it.type == 1 }
                val mistakeList = item.days.filter { it.type == 2}
                doneTv.text = doneList.size.toString()
                mistakeTv.text = mistakeList.size.toString()
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<HabitWithDays>() {
            override fun areItemsTheSame(oldItem: HabitWithDays, newItem: HabitWithDays): Boolean {
                return oldItem.habit.id == newItem.habit.id
            }

            override fun areContentsTheSame(oldItem: HabitWithDays, newItem: HabitWithDays): Boolean {
                return oldItem == newItem
            }
        }
    }
}