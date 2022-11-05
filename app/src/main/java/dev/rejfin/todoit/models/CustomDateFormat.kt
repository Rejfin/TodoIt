package dev.rejfin.todoit.models

import java.io.Serializable

data class CustomDateFormat(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minutes: Int = 0
): Serializable
