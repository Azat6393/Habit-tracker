package com.woynapp.wontto.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.woynapp.wontto.data.local.dto.AlarmItemDto

data class HabitWithDays(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "habit_uuid"
    ) val days: List<DayInfo>,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "habit_id"
    ) val alarmsDto: List<AlarmItem>
)
