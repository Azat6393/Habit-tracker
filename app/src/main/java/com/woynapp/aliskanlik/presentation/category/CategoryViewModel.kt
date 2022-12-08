package com.woynapp.aliskanlik.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    fun addCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }

    fun getAllCategory() = viewModelScope.launch {
        repo.getAllCategory().onEach {
            _categories.value = it
        }.launchIn(viewModelScope)
    }

    fun getHabitByCategory(category: String) = viewModelScope.launch {
        repo.getHabitByCategory(category).onEach {
            _habits.value = it
        }.launchIn(viewModelScope)
    }

    fun getAllHabits() = viewModelScope.launch {
        repo.getAllHabits().onEach {
            _habits.value = it
        }.launchIn(viewModelScope)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repo.updateHabit(habit)
    }
}