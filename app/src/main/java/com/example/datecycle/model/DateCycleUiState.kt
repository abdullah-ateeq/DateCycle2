package com.example.datecycle.model

import java.time.DayOfWeek
import java.time.LocalDate

data class DateCycleUiState(
    val startDate: LocalDate = LocalDate.now(),
    val recurrenceType: RecurrenceType = RecurrenceType.WEEKLY,
    val interval: Int = 1,
    val intervalUnit: IntervalUnit = IntervalUnit.WEEKS,
    val selectedWeekdays: Set<DayOfWeek> = setOf(
        LocalDate.now().dayOfWeek
    ),
    val monthlyMode: MonthlyMode = MonthlyMode.SAME_DAY,
    val monthlyDay: Int = LocalDate.now().dayOfMonth,
    val leapDayMode: LeapDayMode = LeapDayMode.FEBRUARY_28,
    val endMode: EndMode = EndMode.OCCURRENCE_COUNT,
    val occurrenceCount: Int = 10,
    val endDate: LocalDate? = null,
    val generatedDates: List<LocalDate> = emptyList(),
    val errorMessage: String? = null,
    val history: List<DateCycleHistory> = emptyList(),
    val isLivePreview: Boolean = true,
    val showCalendarPreview: Boolean = true,
    val showMonthlySummary: Boolean = true,
    val showYearlySummary: Boolean = false,
    val showDayOfWeek: Boolean = true,
    val showOccurrenceNumbers: Boolean = true,
    val showNextOccurrence: Boolean = true,
    val dateFormat: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastConfiguration: Boolean = true,
    val infoMessage: String? = null,
    val warningMessage: String? = null
)
