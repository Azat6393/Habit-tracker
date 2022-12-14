package com.woynapp.aliskanlik.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.ItemCategoryBinding
import com.woynapp.aliskanlik.databinding.ItemDayBinding
import com.woynapp.aliskanlik.databinding.ItemHabitBinding
import com.woynapp.aliskanlik.databinding.ItemStartedHabitBinding
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.DayInfo
import com.woynapp.aliskanlik.domain.model.Habit

class DaysAdapter :
    ListAdapter<DayInfo, DaysAdapter.DaysViewHolder>(DiffCallBack) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        mContext = parent.context
        return DaysViewHolder(
            ItemDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class DaysViewHolder(private val _binding: ItemDayBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        fun bind(dayInfo: DayInfo) {
            _binding.day.text = dayInfo.day.toString()
            when (dayInfo.type) {
                1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        _binding.cardview.setCardBackgroundColor(mContext.getColor(R.color.green))
                        _binding.day.setTextColor(mContext.getColor(R.color.white))
                    } else {
                        _binding.cardview.setCardBackgroundColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.green
                            )
                        )
                        _binding.day.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    }
                }
                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        _binding.cardview.setCardBackgroundColor(mContext.getColor(R.color.red))
                        _binding.day.setTextColor(mContext.getColor(R.color.white))
                    } else {
                        _binding.cardview.setCardBackgroundColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.red
                            )
                        )
                        _binding.day.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    }
                }
                0 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        _binding.cardview.setCardBackgroundColor(mContext.getColor(R.color.white))
                        _binding.day.setTextColor(mContext.getColor(R.color.black))
                    } else {
                        _binding.cardview.setCardBackgroundColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.white
                            )
                        )
                        _binding.day.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                    }
                }
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<DayInfo>() {
            override fun areItemsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}