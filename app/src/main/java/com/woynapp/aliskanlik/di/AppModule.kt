package com.woynapp.aliskanlik.di

import android.content.Context
import androidx.room.Room
import com.woynapp.aliskanlik.data.local.HabitDao
import com.woynapp.aliskanlik.data.local.HabitDatabase
import com.woynapp.aliskanlik.data.repository.HabitRepositoryImpl
import com.woynapp.aliskanlik.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHabitDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        HabitDatabase::class.java,
        "habit_database",
    ).build()

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao): HabitRepository{
        return HabitRepositoryImpl(dao)
    }
}