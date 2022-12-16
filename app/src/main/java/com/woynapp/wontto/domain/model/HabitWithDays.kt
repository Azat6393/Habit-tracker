package com.woynapp.wontto.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithDays(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "habit_uuid"
    ) val days: List<DayInfo>
)
