package com.woynapp.wontto.presentation.habit_dateils

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woynapp.wontto.R
import com.woynapp.wontto.databinding.DialogAlertBinding
import com.woynapp.wontto.domain.model.Habit
import java.util.*


class AlertDetailsDialog(
    private val habit: Habit,
    private val alertOn: (String) -> Unit,
    private val alertOff: () -> Unit,
    private val changeAlertTime: (String) -> Unit
) : DialogFragment() {

    private lateinit var _binding: DialogAlertBinding
    private var selectedTime: String? = null
    private var alertState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_alert, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogAlertBinding.bind(view)
        alertState = habit.alert_on
        _binding.apply {
            selectedTime = habit.alert_time
            setTimeText()
            closeBtn.setOnClickListener { this@AlertDetailsDialog.dismiss() }
            alertBtn.text =
                if (habit.alert_on) getString(R.string.off)
                else getString(R.string.on)
            alertBtn.setTextColor(
                if (habit.alert_on) Color.parseColor("#FF5151")
                else Color.parseColor("#08D500")
            )
            alertBtn.setOnClickListener {
                if (alertState) {
                    alertOff()
                    alertBtn.text = getString(R.string.on)
                    alertBtn.setTextColor(Color.parseColor("#08D500"))
                    _binding.timeTv.text = "${getString(R.string.close)} - $selectedTime"
                    alertState = false
                } else {
                    alertOn(selectedTime!!)
                    alertBtn.text = getString(R.string.off)
                    alertBtn.setTextColor(Color.parseColor("#FF5151"))
                    _binding.timeTv.text = "${getString(R.string.open)} - $selectedTime"
                    alertState = true
                }
            }
            timeCtn.setOnClickListener { showTimePicker() }
        }
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = "$hourOfDay:$minute"
                setTimeText()
                changeAlertTime(selectedTime!!)
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun setTimeText() {
        _binding.timeTv.text =
            if (habit.alert_on) "${getString(R.string.open)} - $selectedTime"
            else "${getString(R.string.close)} - $selectedTime"
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

}