package com.woynapp.wontto.domain.model

data class AlarmItem(
    val id: Int? = null,
    val uuid: Int,
    val time: Long,
    val message: String,
    val is_mute: Boolean,
    val habit_id: String
)
