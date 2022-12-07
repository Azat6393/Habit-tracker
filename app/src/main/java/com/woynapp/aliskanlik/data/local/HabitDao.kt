package com.woynapp.aliskanlik.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habit: Habit)

    @Query("SELECT * FROM habit WHERE started = 1")
    fun getStartedHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habit WHERE started = 0")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habit WHERE category =:category AND started = 0")
    fun getHabitByCategory(category: String): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getAllCategory(): Flow<List<Category>>
}