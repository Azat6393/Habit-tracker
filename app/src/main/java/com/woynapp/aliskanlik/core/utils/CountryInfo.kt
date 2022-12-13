package com.woynapp.aliskanlik.core.utils

import kotlinx.serialization.Serializable

@Serializable
data class CountryInfo(
    val flag: String,
    val name: String,
    val number: String
)