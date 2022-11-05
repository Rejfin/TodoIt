package dev.rejfin.todoit.models

import java.io.Serializable

data class TaskPartModel(
    val status: Boolean = false,
    val desc: String = ""
): Serializable
