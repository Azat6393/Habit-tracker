package com.woynapp.aliskanlik.presentation.dashboard

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
class DashboardViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    init {
        getStartedHabits()
    }

    private fun getStartedHabits() = viewModelScope.launch {
        repo.getStartedHabits().onEach {
            _habits.value = it
        }.launchIn(viewModelScope)
    }
}