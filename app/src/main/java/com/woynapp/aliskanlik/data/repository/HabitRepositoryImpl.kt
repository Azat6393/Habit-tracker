package com.woynapp.aliskanlik.data.repository

import com.woynapp.aliskanlik.data.local.HabitDao
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import com.woynapp.aliskanlik.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao
) : HabitRepository {

    override suspend fun insertHabit(habit: Habit) {
        dao.insertHabit(habit)
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit)
    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit)
    }

    override fun getStartedHabits(): Flow<List<Habit>> {
        return dao.getStartedHabits()
    }

    override fun getAllHabits(): Flow<List<Habit>> {
        return dao.getAllHabits()
    }

    override fun getHabitByCategory(category: String): Flow<List<Habit>> {
        return dao.getHabitByCategory(category)
    }

    override suspend fun insertCategory(category: Category) {
        return dao.insertCategory(category)
    }

    override fun getAllCategory(): Flow<List<Category>> {
        return dao.getAllCategory()
    }
}