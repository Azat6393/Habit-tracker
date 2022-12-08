package com.woynapp.aliskanlik.presentation.category

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.FragmentCategoryBinding
import com.woynapp.aliskanlik.databinding.FragmentDashboardBinding
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.presentation.adapter.AdapterItemListener
import com.woynapp.aliskanlik.presentation.adapter.CategoryAdapter
import com.woynapp.aliskanlik.presentation.adapter.HabitsAdapter
import com.woynapp.aliskanlik.presentation.adapter.StartedHabitsAdapter
import com.woynapp.aliskanlik.presentation.add_habit.AddHabitBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_habit.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.fragment_category), AdapterItemListener<Habit>,
    CategoryAdapter.CategoryItemListener {

    private lateinit var _binding: FragmentCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val habitAdapter: HabitsAdapter by lazy { HabitsAdapter(this) }
    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryBinding.bind(view)

        initRecyclerViews()
        observe()
        viewModel.getAllCategory()
        viewModel.getAllHabits()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { result ->
                    val categoryList = arrayListOf<Category>(Category(name = ""))
                    categoryList.addAll(result)
                    println(result)
                    println(categoryList)
                    categoryAdapter.submitList(categoryList)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { result ->
                    habitAdapter.submitList(result)
                }
            }
        }
    }

    private fun initRecyclerViews() {
        _binding.categoryRv.apply {
            adapter = categoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        _binding.habitsRv.apply {
            adapter = habitAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    override fun onClick(item: Habit) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(item.name)
        alertDialog.setMessage("Do you want to start this challenge?")
        alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
            viewModel.updateHabit(item.copy(started = true, started_date = System.currentTimeMillis()))
        }
        alertDialog.setNegativeButton(getString(R.string.no)) { _, _ -> }
        alertDialog.show()
    }

    override fun addCategory() {
        AddHabitBottomSheet {
            viewModel.addCategory(Category(name = it))
        }.show(childFragmentManager, "Add Habit bottom sheet")
    }

    private var selectedCategory: String? = null

    override fun onClick(item: Category) {
        if (selectedCategory == item.name){
            viewModel.getAllHabits()
            selectedCategory = null
        }else {
            selectedCategory = item.name
            viewModel.getHabitByCategory(item.name)
        }
    }
}
