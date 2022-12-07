package com.woynapp.aliskanlik.data.local

import androidx.lifecycle.Lifecycle
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.woynapp.aliskanlik.domain.model.Category
import com.woynapp.aliskanlik.domain.model.Habit
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [Category::class, Habit::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
}