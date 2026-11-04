package com.example.peer_tutoring_app.model


data class Tutor(
    val name: String = "",
    val email: String = "",
    val subjects: List<String> = emptyList(),
    val availableDates: List<String> = emptyList(),
    val recommendedTeacherName: String = "",
    val recommendedTeacherEmail: String = "",
    val imageResId: Int? = null
) {
    val subject: String
        get() = subjects.joinToString(", ")
}

