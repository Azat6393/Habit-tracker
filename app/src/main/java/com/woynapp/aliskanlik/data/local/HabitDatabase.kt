package com.woynapp.aliskanlik.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.DayInfo
import com.woynapp.aliskanlik.domain.model.Habit

@Database(
    entities = [Category::class, Habit::class, DayInfo::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
}