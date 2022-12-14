package com.woynapp.aliskanlik.presentation.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.toDays
import com.woynapp.aliskanlik.databinding.FragmentDashboardBinding
import com.woynapp.aliskanlik.domain.model.DayInfo
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.domain.model.HabitWithDays
import com.woynapp.aliskanlik.presentation.adapter.AdapterItemListener
import com.woynapp.aliskanlik.presentation.adapter.StartedHabitsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard),
    AdapterItemListener<HabitWithDays> {

    private lateinit var _binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val mAdapter: StartedHabitsAdapter by lazy { StartedHabitsAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        observeUser()
        _binding.newHabit.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .selectedItemId = R.id.habitFragment
        }

        initRecyclerView()
        observe()
    }


    @SuppressLint("SetTextI18n")
    private fun observeUser() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentUser.collect {
                    _binding.title.text = "${getString(R.string.hello)} ${it.first_name}"
                }
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { result ->
                    mAdapter.submitList(result.map { updateDays(it) })
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
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onClick(item: HabitWithDays) {
        val action =
            DashboardFragmentDirections.actionDashboardFragmentToHabitDetailsFragment(item.habit.id!!)
        findNavController().navigate(action)
    }
}