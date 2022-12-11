package com.woynapp.aliskanlik.presentation.dashboard

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
import com.woynapp.aliskanlik.databinding.FragmentDashboardBinding
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.presentation.adapter.AdapterItemListener
import com.woynapp.aliskanlik.presentation.adapter.StartedHabitsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard), AdapterItemListener<Habit> {

    private lateinit var _binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val mAdapter: StartedHabitsAdapter by lazy { StartedHabitsAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        _binding.newHabit.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .selectedItemId = R.id.habitDetailsFragment
        }

        initRecyclerView()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { result ->
                    mAdapter.submitList(result)
                }
            }
        }
    }

    private fun initRecyclerView() {
        _binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onClick(item: Habit) {
        val action = DashboardFragmentDirections.actionDashboardFragmentToHabitDetailsFragment(item.id!!)
        findNavController().navigate(action)
    }
}