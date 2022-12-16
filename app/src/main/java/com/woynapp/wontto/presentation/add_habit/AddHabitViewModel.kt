package com.woynapp.wontto.presentation.add_habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        getAllCategory()
    }

    fun addHabit(habit: Habit) = viewModelScope.launch {
        repo.insertHabit(habit)
    }

    private fun getAllCategory() = viewModelScope.launch {
        repo.getAllCategory().onEach {
            _categories.value = it
        }.launchIn(viewModelScope)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }
}