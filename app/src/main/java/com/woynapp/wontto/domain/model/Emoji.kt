package com.woynapp.wontto.domain.model

data class Emoji(
    val aliases: List<String>,
    val category: String,
    val description: String,
    val emoji: String,
    val ios_version: String,
    val tags: List<String>,
    val unicode_version: String
)