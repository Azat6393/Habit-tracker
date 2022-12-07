package com.woynapp.aliskanlik.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.aliskanlik.databinding.ItemStartedHabitBinding
import com.woynapp.aliskanlik.domain.model.Habit

class StartedHabitsAdapter(private val listener: AdapterItemListener<Habit>) :
    ListAdapter<Habit, StartedHabitsAdapter.HabitViewHolder>(DiffCallBack) {

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
                if (position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if (item != null){
                        listener.onClick(item)
                    }
                }
            }
        }

        fun bind(item: Habit){
            _binding.apply {
                nameTv.text = item.name
                totalTv.text = item.days.size.toString()
                val doneList = item.days.filter { it }

                doneTv.text = doneList.size.toString()
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
                return oldItem == newItem
            }
        }
    }
}