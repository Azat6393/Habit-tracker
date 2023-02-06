package com.woynapp.wontto.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.woynapp.wontto.data.local.dto.AlarmItemDto
import com.woynapp.wontto.data.local.dto.HabitWithDaysDto
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
    fun getHabitWithDays(id: Int): Flow<HabitWithDaysDto>
    fun getAllHabitWithDays(): Flow<List<HabitWithDaysDto>>
    suspend fun deleteCategory(category: Category)
    fun getHabitByUUID(uuid: String): Flow<Habit>
    suspend fun insertAlarmItem(item: AlarmItemDto)
    suspend fun deleteAlarmItem(item: AlarmItemDto)
    suspend fun updateAlarmItem(item: AlarmItemDto)
}