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
import com.woynapp.wontto.databinding.ItemCategoryAddHabitBinding
import com.woynapp.wontto.databinding.ItemCategoryBinding
import com.woynapp.wontto.domain.model.Category

class CategoryAddHabitAdapter(private val listener: CategoryItemListener) :
    ListAdapter<Category, CategoryAddHabitAdapter.CategoryViewHolder>(DiffCallBack) {

    private var selectedItemId: Int? = null
    private lateinit var mContext: Context

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedCategory(name: String){
        currentList.forEachIndexed { index, category ->
            println("$name -> ${category.name}")
            if (category.name == name){
                selectedItemId = category.id
                notifyDataSetChanged()
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        mContext = parent.context
        return CategoryViewHolder(
            ItemCategoryAddHabitBinding.inflate(
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
    inner class CategoryViewHolder(private val _binding: ItemCategoryAddHabitBinding) :
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
            _binding.btn.apply {
                setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            if (selectedItemId == item.id) {
                                selectedItemId = null
                                listener.onClick(item)
                                notifyDataSetChanged()
                            } else {
                                selectedItemId = item.id
                                listener.onClick(item)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
                setOnLongClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onLongClick(item)
                        }
                    }
                    true
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
                        if (selectedItemId == item.id)
                            mContext.getColor(R.color.light_primary)
                        else mContext.getColor(R.color.dark_surface)
                    )
                    strokeColor = if (selectedItemId == item.id)
                        ColorStateList.valueOf(mContext.getColor(R.color.light_primary))
                    else ColorStateList.valueOf(mContext.getColor(R.color.dark_surface))
                } else {
                    setTextColor(
                        if (selectedItemId == item.id)
                            ContextCompat.getColor(mContext, R.color.light_primary)
                        else ContextCompat.getColor(mContext, R.color.dark_surface)
                    )
                    strokeColor = if (selectedItemId == item.id)
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
        fun onLongClick(item: Category)
    }
}