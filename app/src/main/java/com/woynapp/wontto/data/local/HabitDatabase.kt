package com.woynapp.wontto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.woynapp.wontto.data.local.dto.AlarmItemDto
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit

@Database(
    entities = [Category::class, Habit::class, DayInfo::class, AlarmItemDto::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao

    companion object {
        val migration1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // update old habit table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS habit_temporary (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "started BLOB NOT NULL, " +
                            "started_date INTEGER, " +
                            "category TEXT NOT NULL, " +
                            "day_size NOT NULL, " +
                            "uuid TEXT NOT NULL, " +
                            "emoji TEXT NOT NULL)"
                )
                database.execSQL(
                    "INSERT INTO habit_temporary (id, name, description, started, started_date, category, day_size, uuid, emoji) " +
                            "SELECT id, name, description, started, started_date, category, day_size, uuid, emoji FROM habit"
                )
                database.execSQL("DROP TABLE habit")
                database.execSQL("ALTER TABLE habit_temporary RENAME TO habit")

                // insert alarm_item table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS alarm_item (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "uuid INTEGER NOT NULL, " +
                            "time INTEGER NOT NULL, " +
                            "message TEXT NOT NULL, " +
                            "is_mute BLOB NOT NULL, " +
                            "habit_id TEXT NOT NULL)"
                )
            }
        }
    }
}