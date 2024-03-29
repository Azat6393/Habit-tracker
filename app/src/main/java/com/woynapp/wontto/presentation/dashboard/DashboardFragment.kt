package com.woynapp.wontto.presentation.dashboard

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
import com.woynapp.wontto.R
import com.woynapp.wontto.core.utils.Constants
import com.woynapp.wontto.core.utils.toDays
import com.woynapp.wontto.databinding.FragmentDashboardBinding
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.HabitWithDays
import com.woynapp.wontto.presentation.adapter.StartedHabitsAdapter
import com.woynapp.wontto.presentation.adapter.StartedHabitsItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard),
    StartedHabitsItemListener {

    private lateinit var _binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val mAdapter: StartedHabitsAdapter by lazy { StartedHabitsAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        observeUser()

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

    override fun onClickAd() {
        val action =
            DashboardFragmentDirections.actionDashboardFragmentToWebViewFragment(
                Constants.KARGO_BUL_URL
            )
        findNavController().navigate(action)
    }
}