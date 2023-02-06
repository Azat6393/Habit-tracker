package com.woynapp.wontto.presentation.add_habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.wontto.core.notification.RemindersManager
import com.woynapp.wontto.core.utils.toAlarmItemDto
import com.woynapp.wontto.domain.model.AlarmItem
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repo: HabitRepository,
    private val remindersManager: RemindersManager
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _addedHabit = MutableStateFlow<Habit?>(null)
    val addedHabit = _addedHabit.asStateFlow()

    init {
        getAllCategory()
    }

    fun addHabit(habit: Habit, alarmList: List<AlarmItem>) = viewModelScope.launch {
        repo.insertHabit(habit).also {
            for (i in 1..habit.day_size) {
                repo.insertDayInfo(
                    DayInfo(
                        habit_uuid = habit.uuid,
                        day = i,
                        type = 0
                    )
                )
            }
            alarmList.forEach { item ->
                if (item.is_mute)
                    remindersManager.stopReminder(item)
                else remindersManager.startReminder(item)
                repo.insertAlarmItem(item.toAlarmItemDto())
            }
        }.also {
            getHabitByUUID(habit.uuid)
        }
    }

    fun updateHabit(habit: Habit, alarmList: List<AlarmItem>) = viewModelScope.launch {
        repo.updateHabit(habit).also {
            for (i in 1..habit.day_size) {
                repo.insertDayInfo(
                    DayInfo(
                        habit_uuid = habit.uuid,
                        day = i,
                        type = 0
                    )
                )
            }
            alarmList.forEach { item ->
                if (item.is_mute)
                    remindersManager.stopReminder(item)
                else remindersManager.startReminder(item)
                repo.insertAlarmItem(item.toAlarmItemDto())
            }
        }.also {
            getHabitByUUID(habit.uuid)
        }
    }

    private fun getHabitByUUID(uuid: String) = viewModelScope.launch{
        repo.getHabitByUUID(uuid).onEach {
            _addedHabit.value = it
        }.launchIn(viewModelScope)
    }

    fun clearHabitFlow(){
        _addedHabit.value = null
    }

    private fun getAllCategory() = viewModelScope.launch {
        repo.getAllCategory().onEach {
            _categories.value = it
        }.launchIn(viewModelScope)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repo.deleteCategory(category)
    }
}