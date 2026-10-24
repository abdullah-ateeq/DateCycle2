package com.example.datecycle.model

data class DateCycleHistory(
    val id: String,
    val startDate: String,
    val recurrenceType: String,
    val interval: Int,
    val intervalUnit: String,
    val selectedWeekdays: List<Int>,
    val monthlyMode: String?,
    val monthlyDay: Int?,
    val leapDayMode: String?,
    val endMode: String,
    val occurrenceCount: Int?,
    val endDate: String?,
    val savedAt: Long
)
