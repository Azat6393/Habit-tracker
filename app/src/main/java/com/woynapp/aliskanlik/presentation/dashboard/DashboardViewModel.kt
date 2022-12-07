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
        viewModelScope.launch {
            repo.insertHabit(
                Habit(
                    name = "Alkol icmeyi birakmak",
                    description = "Alkol icme ayol",
                    started = true,
                    started_date = System.currentTimeMillis(),
                    days = BooleanArray(30).toList(),
                    category = "sport"
                )
            )
            repo.insertHabit(
                Habit(
                    name = "Alkol icmeyi birakmak1",
                    description = "Alkol icme ayol1",
                    started = true,
                    started_date = System.currentTimeMillis(),
                    days = BooleanArray(50).toList(),
                    category = "sport"
                )
            )
            repo.insertHabit(
                Habit(
                    name = "Alkol icmeyi birakmak2",
                    description = "Alkol icme ayol2",
                    started = false,
                    started_date = System.currentTimeMillis(),
                    days = BooleanArray(21).toList(),
                    category = "sport"
                )
            )
            repo.insertHabit(
                Habit(
                    name = "Alkol icmeyi birakmak3",
                    description = "Alkol icme ayol3",
                    started = false,
                    started_date = System.currentTimeMillis(),
                    days = BooleanArray(21).toList(),
                    category = "sport"
                )
            )
            repo.insertHabit(
                Habit(
                    name = "Alkol icmeyi birakmak4",
                    description = "Alkol icme ayol4",
                    started = false,
                    started_date = System.currentTimeMillis(),
                    days = BooleanArray(21).toList(),
                    category = "health"
                )
            )
        }
    }

    private fun getStartedHabits() = viewModelScope.launch {
        repo.getStartedHabits().onEach {
            _habits.value = it
        }.launchIn(viewModelScope)
    }
}