package com.woynapp.aliskanlik.presentation.habit_dateils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.notification.RemindersManager
import com.woynapp.aliskanlik.core.utils.showAlertDialog
import com.woynapp.aliskanlik.core.utils.showToastMessage
import com.woynapp.aliskanlik.core.utils.toDays
import com.woynapp.aliskanlik.databinding.FragmentHabitDetailsBinding
import com.woynapp.aliskanlik.domain.model.DayInfo
import com.woynapp.aliskanlik.domain.model.HabitWithDays
import com.woynapp.aliskanlik.presentation.adapter.DaysAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HabitDetailsFragment : Fragment(R.layout.fragment_habit_details) {

    private lateinit var _binding: FragmentHabitDetailsBinding
    private val viewModel: HabitDetailViewModel by viewModels()
    private val mAdapter: DaysAdapter by lazy { DaysAdapter() }
    private val args: HabitDetailsFragmentArgs by navArgs()

    private lateinit var powerMenu: PowerMenu


    private var currentHabit: HabitWithDays? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHabitDetailsBinding.bind(view)

        viewModel.getHabitById(args.habitId)

        _binding.doneBtn.setOnClickListener { markDay() }
        _binding.moreBtn.setOnClickListener {
            powerMenu.showAsDropDown(_binding.moreBtn)
        }

        initRecyclerView()
        initPowerMenu()
        observe()
    }


    private fun initPowerMenu() {
        powerMenu = PowerMenu.Builder(requireContext())
            .addItem(PowerMenuItem(getString(R.string.alertOn), false))
            .addItem(PowerMenuItem(getString(R.string.alertOff), false))
            .addItem(PowerMenuItem(getString(R.string.restart), false))
            .addItem(PowerMenuItem(getString(R.string.delete), false))
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            .setTextGravity(Gravity.START)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.light_primary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()
    }

    private val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem> =
        OnMenuItemClickListener<PowerMenuItem> { position, item ->
            when (position) {
                0 -> {
                    // alert on
                    showTimePicker()
                    powerMenu.dismiss()
                }
                1 -> {
                    // alert off
                    currentHabit?.habit?.id?.let {
                        RemindersManager.stopReminder(
                            requireContext(),
                            it
                        )
                    }
                    powerMenu.dismiss()
                }
                2 -> {
                    // restart
                    powerMenu.dismiss()
                    showAlertDialog(
                        requireContext(),
                        getString(R.string.restart),
                        getString(R.string.restart_habit_message)
                    ) {
                        currentHabit?.let { viewModel.restartHabit(habit = it) }
                    }
                }
                3 -> {
                    // delete
                    powerMenu.dismiss()
                    showAlertDialog(
                        requireContext(),
                        getString(R.string.delete_habit),
                        getString(R.string.delete_habit_message)
                    ) {
                        currentHabit?.let {
                            RemindersManager.stopReminder(requireContext(), it.habit.id!!)
                            viewModel.deleteHabit(habit = it.habit)
                        }
                        findNavController().popBackStack()
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
                currentHabit!!.habit.id?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = ContextCompat.getSystemService(
                            requireContext(),
                            AlarmManager::class.java
                        )
                        if (alarmManager?.canScheduleExactAlarms() == false) {
                            Intent().also { intent ->
                                intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                requireContext().startActivity(intent)
                            }
                        } else {
                            createNotificationsChannels()
                            RemindersManager.startReminder(
                                requireContext(),
                                reminderTime = "$hourOfDay:$minute",
                                reminderId = currentHabit?.habit?.id!!
                            )
                        }
                    } else {
                        createNotificationsChannels()
                        RemindersManager.startReminder(
                            requireContext(),
                            reminderTime = "$hourOfDay:$minute",
                            reminderId = it
                        )
                    }
                }
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun createNotificationsChannels() {

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                getString(R.string.reminders_notification_channel_id),
                getString(R.string.reminders_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        ContextCompat.getSystemService(requireContext(), NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    private fun markDay() {
        currentHabit?.let { habit ->
            val currentMl = System.currentTimeMillis()
            val currentDayFromMl = currentMl.toDays()
            val startedDayFromMl = habit.habit.started_date!!.toDays()
            val currentDay = currentDayFromMl - startedDayFromMl
            println(currentDay + 1)
            val updateDay = habit.days.find { it.day.toLong() == currentDay + 1 }
            updateDay?.let {
                if (it.type == 1) {
                    requireContext().showToastMessage(getString(R.string.already_done_text))
                } else {
                    viewModel.updateDay(it.copy(type = 1))
                    updateDays(habit)
                }
            }
        }
    }

    private fun updateDays(habit: HabitWithDays): HabitWithDays {
        val newDays = arrayListOf<DayInfo>()
        val currentMl = System.currentTimeMillis()
        val currentDayFromMl = currentMl.toDays()
        val startedDayFromMl = habit.habit.started_date!!.toDays()
        habit.days.forEach {
            when (it.type) {
                1 -> {
                    newDays.add(it)
                }
                2 -> {
                    newDays.add(it)
                }
                0 -> {
                    val currentDay = currentDayFromMl - startedDayFromMl
                    if (it.day < currentDay + 1) {
                        val newDay = it.copy(type = 2)
                        newDays.add(newDay).also {
                            viewModel.updateDay(newDay)
                        }
                    } else {
                        newDays.add(it)
                    }
                }
            }
        }
        return habit.copy(days = newDays)
    }

    private fun initRecyclerView() {
        _binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
            setHasFixedSize(true)
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habit.collect { result ->
                    result?.let {
                        _binding.icon.text = result.habit.emoji
                        _binding.title.text = it.habit.name
                        _binding.descriptionTv.text = it.habit.description
                        mAdapter.submitList(result.days)
                        currentHabit = it
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .visibility = View.VISIBLE
    }
}