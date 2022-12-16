package com.woynapp.wontto.core.utils

import android.content.Context
import com.woynapp.wontto.R
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.Habit
import java.util.*

object DefaultCategoryAndHabit {

    fun getCategories(context: Context): List<Category> {
        return listOf<Category>(
            Category(name = context.getString(R.string.education_category)),
            Category(name = context.getString(R.string.health_category)),
            Category(name = context.getString(R.string.sport_category)),
            Category(name = context.getString(R.string.system_category)),
        )
    }

    fun getHabits(context: Context): List<Habit> {
        return listOf<Habit>(
            Habit(
                name = context.getString(R.string.learn_new_language),
                description = context.getString(R.string.learn_new_language_description),
                started = false,
                category = context.getString(R.string.education_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🎓",
            ),
            Habit(
                name = context.getString(R.string.regular_book_reading),
                description = context.getString(R.string.regular_book_reading_description),
                started = false,
                category = context.getString(R.string.education_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "📖",
            ),
            Habit(
                name = context.getString(R.string.studying),
                description = context.getString(R.string.studying_description),
                started = false,
                category = context.getString(R.string.education_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "✏️",
            ),


            Habit(
                name = context.getString(R.string.quit_smoking),
                description = context.getString(R.string.quit_smoking_description),
                started = false,
                category = context.getString(R.string.health_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🚬",
            ),
            Habit(
                name = context.getString(R.string.meditation),
                description = context.getString(R.string.meditation_description),
                started = false,
                category = context.getString(R.string.health_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🧘",
            ),
            Habit(
                name = context.getString(R.string.drop_sugar),
                description = context.getString(R.string.drop_sugar_description),
                started = false,
                category = context.getString(R.string.health_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🍥",
            ),
            Habit(
                name = context.getString(R.string.drinking_water),
                description = context.getString(R.string.drinking_water_description),
                started = false,
                category = context.getString(R.string.health_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "💧",
            ),
            Habit(
                name = context.getString(R.string.stop_drinking_coffee),
                description = context.getString(R.string.srop_dringking_coffee_descriotion),
                started = false,
                category = context.getString(R.string.health_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "☕",
            ),


            Habit(
                name = context.getString(R.string.work_out),
                description = context.getString(R.string.work_out_description),
                started = false,
                category = context.getString(R.string.sport_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🏃",
            ),
            Habit(
                name = context.getString(R.string.work_out_at_home),
                description = context.getString(R.string.work_out_at_home_description),
                started = false,
                category = context.getString(R.string.sport_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🏘️",
            ),


            Habit(
                name = context.getString(R.string.time_management),
                description = context.getString(R.string.time_management_description),
                started = false,
                category = context.getString(R.string.system_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "⌛",
            ),
            Habit(
                name = context.getString(R.string.wake_up_early),
                description = context.getString(R.string.wake_up_early_description),
                started = false,
                category = context.getString(R.string.system_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "⏰",
            ),
            Habit(
                name = context.getString(R.string.regular_sleep),
                description = context.getString(R.string.regular_sleep_description),
                started = false,
                category = context.getString(R.string.system_category),
                day_size = 21,
                uuid = UUID.randomUUID().toString(),
                emoji = "🛌",
            )
        )
    }
}