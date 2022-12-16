package com.woynapp.wontto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit

@Database(
    entities = [Category::class, Habit::class, DayInfo::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
}