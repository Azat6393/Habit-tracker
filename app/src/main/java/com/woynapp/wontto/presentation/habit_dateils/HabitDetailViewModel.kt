package com.woynapp.wontto.presentation.habit_dateils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.wontto.core.utils.toHabitWithDays
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.domain.model.HabitWithDays
import com.woynapp.wontto.domain.repository.HabitRepository
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

    private val _habit = MutableStateFlow<HabitWithDays?>(null)
    val habit = _habit.asStateFlow()

    fun getHabitById(id: Int) {
        repo.getHabitWithDays(id).onEach { result ->
            _habit.value = result.toHabitWithDays()
        }.launchIn(viewModelScope)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repo.updateHabit(habit)
    }

    fun updateDay(dayInfo: DayInfo) = viewModelScope.launch {
        repo.updateDayInfo(dayInfo)
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repo.deleteHabit(habit).also { repo.deleteAllDaysInfo(habit.uuid) }
    }

    fun restartHabit(habit: HabitWithDays) = viewModelScope.launch {
        habit.days.forEach {
            repo.updateDayInfo(it.copy(type = 0))
        }
        repo.updateHabit(
            habit.habit.copy(started_date = System.currentTimeMillis())
        )
    }
}
