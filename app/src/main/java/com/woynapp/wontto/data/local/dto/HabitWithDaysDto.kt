package com.woynapp.wontto.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.Habit

data class HabitWithDaysDto(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "habit_uuid"
    ) val days: List<DayInfo>,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "habit_id"
    ) val alarmsDto: List<AlarmItemDto>
)
