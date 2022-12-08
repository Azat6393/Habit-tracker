package com.woynapp.aliskanlik.presentation.habit_dateils

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.getDaysDetail
import com.woynapp.aliskanlik.core.utils.showAlertDialog
import com.woynapp.aliskanlik.core.utils.toDays
import com.woynapp.aliskanlik.databinding.FragmentHabitDetailsBinding
import com.woynapp.aliskanlik.domain.model.Habit
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


    private var currentHabit: Habit? = null

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
                        currentHabit?.let { viewModel.deleteHabit(habit = it) }
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
                Toast.makeText(requireContext(), "$hourOfDay:$minute", Toast.LENGTH_SHORT).show()
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }


    private fun markDay() {
        currentHabit?.let { habit ->
            val intList = arrayListOf<Boolean>()
            intList.addAll(habit.days)
            val currentMl = System.currentTimeMillis()
            val currentDayFromMl = currentMl.toDays()
            val startedDayFromMl = habit.started_date!!.toDays()
            val currentDay = currentDayFromMl - startedDayFromMl
            val isAlreadyDone = habit.days[currentDay]
            if (isAlreadyDone) {
                Toast.makeText(requireContext(), "You already done for today", Toast.LENGTH_LONG)
                    .show()
            } else {
                intList[currentDay] = true
                println(intList)
                viewModel.updateHabit(habit.copy(days = intList))
            }
        }
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
                        _binding.title.text = it.name
                        _binding.descriptionTv.text = it.description
                        mAdapter.submitList(getDaysDetail(it.days, it.started_date!!))
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