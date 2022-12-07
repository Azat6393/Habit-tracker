package com.woynapp.aliskanlik.domain.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun insertHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    fun getStartedHabits(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getHabitByCategory(category: String): Flow<List<Habit>>
    suspend fun insertCategory(category: Category)
    fun getAllCategory(): Flow<List<Category>>
}