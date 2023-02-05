package com.woynapp.wontto.presentation.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woynapp.wontto.R
import com.woynapp.wontto.core.utils.parseHourAndMinute
import com.woynapp.wontto.databinding.ItemAlarmBinding
import com.woynapp.wontto.domain.model.AlarmItem
import java.util.*

class AlarmAdapter constructor(
    private val listener: OnItemClickListener
) : ListAdapter<AlarmItem, AlarmAdapter.AlarmViewHolder>(DiffCallBack) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        context = parent.context
        return AlarmViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class AlarmViewHolder(private val _binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        init {
            _binding.clockBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        val c = Calendar.getInstance()
                        val hour = c.get(Calendar.HOUR_OF_DAY)
                        val minute = c.get(Calendar.MINUTE)
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val calendar: Calendar =
                                    Calendar.getInstance(Locale.getDefault()).apply {
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                    }
                                listener.update(
                                    item = item.copy(
                                        time = calendar.timeInMillis
                                    )
                                )
                            },
                            hour,
                            minute,
                            false
                        )
                        timePickerDialog.show()
                    }
                }
            }
            _binding.alertBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.update(
                            item = item.copy(
                                is_mute = !item.is_mute
                            )
                        )
                    }
                }
            }
            _binding.deleteBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.delete(item)
                    }
                }
            }
        }

        fun bind(item: AlarmItem) {
            _binding.apply {
                timeTv.text =
                    if (!item.is_mute) "${context.getString(R.string.open)} - ${
                        parseHourAndMinute(
                            item.time
                        )
                    }"
                    else "${context.getString(R.string.close)} - ${parseHourAndMinute(item.time)}"
                alertBtn.text =
                    if (!item.is_mute) context.getString(R.string.off)
                    else context.getString(R.string.on)
                alertBtn.setTextColor(
                    if (!item.is_mute) Color.parseColor("#FF5151")
                    else Color.parseColor("#08D500")
                )
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<AlarmItem>() {
            override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun update(item: AlarmItem)
        fun delete(item: AlarmItem)
    }
}