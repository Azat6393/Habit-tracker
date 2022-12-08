package com.woynapp.aliskanlik.presentation.habit_dateils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _habit = MutableStateFlow<Habit?>(null)
    val habit = _habit.asStateFlow()

    fun getHabitById(id: Int) {
        repo.getHabitById(id).onEach {
            _habit.value = it
        }.launchIn(viewModelScope)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repo.updateHabit(habit)
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repo.deleteHabit(habit)
    }

    fun restartHabit(habit: Habit) = viewModelScope.launch {
        val newList = BooleanArray(habit.days.size)
        repo.updateHabit(
            habit.copy(days = newList.toList(), started_date = System.currentTimeMillis())
        )
    }
}
