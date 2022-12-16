package com.woynapp.wontto.presentation.adapter

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
import com.woynapp.wontto.R
import com.woynapp.wontto.databinding.ItemCategoryBinding
import com.woynapp.wontto.domain.model.Category

class CategoryAdapter(private val listener: CategoryItemListener) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallBack) {

    private var selectedItemPosition: Int? = null
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        mContext = parent.context
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, position == 0, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class CategoryViewHolder(private val _binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        init {
            _binding.addButton.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.addCategory()
                    }
                }
            }
            _binding.btn.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        if (selectedItemPosition == position){
                            selectedItemPosition = null
                            listener.onClick(item)
                            notifyDataSetChanged()
                        }else {
                            selectedItemPosition = position
                            listener.onClick(item)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        fun bind(item: Category, isFirst: Boolean, position: Int) {
            _binding.addButton.isVisible = isFirst
            _binding.btn.isVisible = !isFirst
            _binding.btn.apply {
                text = item.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextColor(
                        if (selectedItemPosition == position)
                            mContext.getColor(R.color.light_primary)
                        else mContext.getColor(R.color.dark_surface)
                    )
                    strokeColor = if (selectedItemPosition == position)
                        ColorStateList.valueOf(mContext.getColor(R.color.light_primary))
                    else ColorStateList.valueOf(mContext.getColor(R.color.dark_surface))
                } else {
                    setTextColor(
                        if (selectedItemPosition == position)
                            ContextCompat.getColor(mContext, R.color.light_primary)
                        else ContextCompat.getColor(mContext, R.color.dark_surface)
                    )
                    strokeColor = if (selectedItemPosition == position)
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.light_primary
                            )
                        )
                    else ColorStateList.valueOf(
                        ContextCompat.getColor(
                            mContext,
                            R.color.dark_surface
                        )
                    )
                }
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface CategoryItemListener {
        fun addCategory()
        fun onClick(item: Category)
    }
}