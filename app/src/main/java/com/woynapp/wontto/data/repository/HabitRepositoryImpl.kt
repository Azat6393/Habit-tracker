package com.woynapp.wontto.data.repository

import com.woynapp.wontto.data.local.HabitDao
import com.woynapp.wontto.data.local.dto.HabitWithDaysDto
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.domain.repository.HabitRepository
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

    override fun getHabitById(id: Int): Flow<Habit> {
        return dao.getHabitById(id)
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

    override suspend fun insertDayInfo(dayInfo: DayInfo) {
        dao.insertDayInfo(dayInfo)
    }

    override suspend fun updateDayInfo(dayInfo: DayInfo) {
        dao.updateDayInfo(dayInfo)
    }

    override suspend fun deleteAllDaysInfo(uuid: String) {
        dao.deleteAllDaysInfo(uuid)
    }

    override fun getHabitWithDays(id: Int): Flow<HabitWithDaysDto> {
        return dao.getHabitWithDays(id)
    }

    override fun getAllHabitWithDays(): Flow<List<HabitWithDaysDto>>{
        return dao.getAllHabitWithDays()
    }

    override suspend fun deleteCategory(category: Category){
        dao.deleteCategory(category)
    }

    override fun getHabitByUUID(uuid: String): Flow<Habit> {
        return dao.getHabitByUUID(uuid)
    }
}