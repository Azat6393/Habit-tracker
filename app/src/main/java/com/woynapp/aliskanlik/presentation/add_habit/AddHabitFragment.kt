package com.woynapp.aliskanlik.presentation.add_habit

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghajari.emojiview.view.AXSingleEmojiView
import com.google.android.material.snackbar.Snackbar
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.FragmentAddHabitBinding
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.presentation.adapter.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID


@AndroidEntryPoint
class AddHabitFragment : Fragment(R.layout.fragment_add_habit),
    CategoryAdapter.CategoryItemListener {

    private lateinit var _binding: FragmentAddHabitBinding
    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter(this) }
    private val viewModel: AddHabitViewModel by viewModels()

    private var selectedCategory: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddHabitBinding.bind(view)

        _binding.daysSeekBar.setOnSeekBarChangeListener(onSeekbarChangeListener)

        _binding.saveBtn.setOnClickListener {
            if (checkForValidHabit()) {
                val name = _binding.habitNameInputLayout.editText?.text.toString()
                val description = _binding.habitDescriptionInputLayout.editText?.text.toString()
                val daySize = _binding.daysSeekBar.progress
                val habit = Habit(
                    name = name,
                    description = description,
                    started = false,
                    category = selectedCategory!!,
                    day_size = daySize,
                    uuid = UUID.randomUUID().toString(),
                    emoji = ""
                )
                viewModel.addHabit(habit)
                clearViews()
                Snackbar.make(_binding.root, getString(R.string.habit_saved_successfully), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        initRecyclerViews()
        observe()
    }

    private fun clearViews() {
        _binding.apply {
            _binding.habitNameInputLayout.editText?.setText("")
            _binding.habitDescriptionInputLayout.editText?.setText("")
        }
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
    }

    private fun initRecyclerViews() {
        _binding.categoryRv.apply {
            adapter = categoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }


    private val onSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.daySize.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    override fun addCategory() {
        AddHabitBottomSheet {
            viewModel.addCategory(Category(name = it))
        }.show(childFragmentManager, "Add Habit bottom sheet")
    }

    override fun onClick(item: Category) {
        selectedCategory = if (selectedCategory == item.name)
            null
        else item.name
    }

    private fun checkForValidHabit(): Boolean {
        when {
            _binding.habitNameInputLayout.editText?.text?.toString().isNullOrBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_write_habit_name),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            selectedCategory.isNullOrBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_category),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }
}