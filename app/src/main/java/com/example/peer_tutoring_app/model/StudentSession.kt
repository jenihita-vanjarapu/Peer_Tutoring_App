package com.example.peer_tutoring_app.model


data class StudentSession(
    val studentName: String,
    val course: String,
    val time: String,
    val imageResId: Int? = null
)

