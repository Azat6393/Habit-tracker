package com.woynapp.wontto.presentation.habit_dateils

import android.Manifest
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.woynapp.wontto.R
import com.woynapp.wontto.core.utils.randomId
import com.woynapp.wontto.core.utils.showToastMessage
import com.woynapp.wontto.databinding.DialogAlertBinding
import com.woynapp.wontto.domain.model.AlarmItem
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.presentation.adapter.AlarmAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class AlertDetailsDialog(
    private val habit: Habit,
    private val viewModel: HabitDetailViewModel
) : DialogFragment(), AlarmAdapter.OnItemClickListener {

    private lateinit var _binding: DialogAlertBinding
    private val mAdapter: AlarmAdapter by lazy { AlarmAdapter(this) }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.post_notification_message),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                showTimePicker()
            }
        }

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
        _binding.apply {
            closeBtn.setOnClickListener { this@AlertDetailsDialog.dismiss() }
            addBtn.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showTimePicker()
                    } else {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    showTimePicker()
                }
            }
            recyclerView.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.getHabitById(habit.id!!)
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habit.collect { result ->
                    mAdapter.submitList(result?.alarmsDto)
                }
            }
        }
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val calendar: Calendar =
                    Calendar.getInstance(Locale.getDefault()).apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }

                viewModel.insertAlertItem(
                    AlarmItem(
                        uuid = randomId(),
                        time = calendar.timeInMillis,
                        message = habit.name,
                        is_mute = false,
                        habit_id = habit.uuid
                    )
                )
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    override fun update(item: AlarmItem) {
        viewModel.updateAlertItem(item)
    }

    override fun delete(item: AlarmItem) {
        viewModel.deleteAlertItem(item)
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