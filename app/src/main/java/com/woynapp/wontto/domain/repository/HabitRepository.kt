package com.woynapp.wontto.domain.repository

import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.domain.model.HabitWithDays
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun insertHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    fun getStartedHabits(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getHabitById(id: Int): Flow<Habit>
    fun getHabitByCategory(category: String): Flow<List<Habit>>
    suspend fun insertCategory(category: Category)
    fun getAllCategory(): Flow<List<Category>>
    suspend fun insertDayInfo(dayInfo: DayInfo)
    suspend fun updateDayInfo(dayInfo: DayInfo)
    suspend fun deleteAllDaysInfo(uuid: String)
    fun getHabitWithDays(id: Int): Flow<HabitWithDays>
    fun getAllHabitWithDays(): Flow<List<HabitWithDays>>
}