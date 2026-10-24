package com.example.datecycle.model

import java.time.LocalDate

data class DateCycleResult(
    val startDate: LocalDate,
    val generatedDates: List<LocalDate>,
    val recurrenceType: RecurrenceType,
    val interval: Int,
    val intervalUnit: IntervalUnit,
    val firstDate: LocalDate,
    val lastDate: LocalDate,
    val totalOccurrences: Int,
    val spanDays: Long
)

data class GeneratedDateMarker(
    val date: LocalDate,
    val occurrenceNumber: Int
)
