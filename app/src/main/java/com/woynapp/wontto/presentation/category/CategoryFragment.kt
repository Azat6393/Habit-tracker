package com.woynapp.wontto.presentation.category

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.woynapp.wontto.R
import com.woynapp.wontto.core.utils.showAlertDialog
import com.woynapp.wontto.databinding.FragmentCategoryBinding
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.presentation.adapter.AdapterItemListener
import com.woynapp.wontto.presentation.adapter.CategoryAdapter
import com.woynapp.wontto.presentation.adapter.HabitsAdapter
import com.woynapp.wontto.presentation.add_habit.AddHabitBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.fragment_category), AdapterItemListener<Habit>,
    CategoryAdapter.CategoryItemListener {

    private lateinit var _binding: FragmentCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val habitAdapter: HabitsAdapter by lazy { HabitsAdapter(this) }
    //private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryBinding.bind(view)

        initRecyclerViews()
        observe()
        viewModel.getAllCategory()
        viewModel.getAllHabits()
    }

    private fun observe() {
        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { result ->
                    val categoryList = arrayListOf<Category>(Category(name = ""))
                    categoryList.addAll(result.reversed())
                    categoryAdapter.submitList(categoryList)
                }
            }
        }*/
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { result ->
                    habitAdapter.submitList(result)
                }
            }
        }
    }

    private fun initRecyclerViews() {
        /*_binding.categoryRv.apply {
            adapter = categoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }*/
        _binding.habitsRv.apply {
            adapter = habitAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    override fun onClick(item: Habit) {
        if (item.started) {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToHabitDetailsFragment(item.id!!)
            findNavController().navigate(action)
        } else {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(item.name)
            alertDialog.setMessage(getString(R.string.start_challenge_message))
            alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                val action =
                    CategoryFragmentDirections.actionCategoryFragmentToHabitFragment(item)
                findNavController().navigate(action)
            }
            alertDialog.setNegativeButton(getString(R.string.no)) { _, _ -> }
            alertDialog.show()
        }
    }

    override fun addCategory() {
        AddHabitBottomSheet {
            viewModel.addCategory(Category(name = it))
        }.show(childFragmentManager, "Add Habit bottom sheet")
    }

    private var selectedCategory: String? = null

    override fun onClick(item: Category) {
        if (selectedCategory == item.name) {
            viewModel.getAllHabits()
            selectedCategory = null
        } else {
            selectedCategory = item.name
            viewModel.getHabitByCategory(item.name)
        }
    }

    override fun onLongClick(item: Category) {
        showAlertDialog(
            requireContext(),
            getString(R.string.delete_category),
            getString(R.string.delete_category_message)
        ) {
            viewModel.deleteCategory(item)
        }
    }
}
