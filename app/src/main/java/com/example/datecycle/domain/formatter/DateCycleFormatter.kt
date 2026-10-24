package com.example.datecycle.domain.formatter

import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.DateCycleUiState
import com.example.datecycle.model.EndMode
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateCycleFormatter {

    fun formatDate(date: LocalDate, formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(formatOption.pattern, locale)
        return date.format(formatter)
    }

    fun formatDayOfWeek(date: LocalDate, locale: Locale = Locale.getDefault(), short: Boolean = false): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return date.dayOfWeek.getDisplayName(style, locale)
    }

    fun formatYearMonth(yearMonth: YearMonth, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return yearMonth.format(formatter)
    }

    fun formatRecurrenceRule(
        recurrenceType: RecurrenceType,
        interval: Int,
        intervalUnit: IntervalUnit,
        selectedWeekdays: Set<DayOfWeek>,
        monthlyMode: MonthlyMode,
        monthlyDay: Int,
        locale: Locale = Locale.getDefault()
    ): String {
        return when (recurrenceType) {
            RecurrenceType.DAILY -> {
                if (interval == 1) "Every day" else "Every $interval days"
            }
            RecurrenceType.WEEKLY -> {
                val weekPart = if (interval == 1) "Every week" else "Every $interval weeks"
                if (selectedWeekdays.isNotEmpty()) {
                    val daysStr = selectedWeekdays.sortedBy { it.value }
                        .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, locale) }
                    "$weekPart on $daysStr"
                } else {
                    weekPart
                }
            }
            RecurrenceType.MONTHLY -> {
                val monthPart = if (interval == 1) "Every month" else "Every $interval months"
                val dayPart = when (monthlyMode) {
                    MonthlyMode.LAST_DAY -> "on the last day of month"
                    MonthlyMode.SKIP_INVALID -> "on day $monthlyDay (skip invalid)"
                    MonthlyMode.CLAMP_INVALID, MonthlyMode.SAME_DAY -> "on day $monthlyDay"
                }
                "$monthPart $dayPart"
            }
            RecurrenceType.YEARLY -> {
                if (interval == 1) "Every year" else "Every $interval years"
            }
            RecurrenceType.CUSTOM -> {
                val unitStr = when (intervalUnit) {
                    IntervalUnit.DAYS -> if (interval == 1) "day" else "days"
                    IntervalUnit.WEEKS -> if (interval == 1) "week" else "weeks"
                    IntervalUnit.MONTHS -> if (interval == 1) "month" else "months"
                    IntervalUnit.YEARS -> if (interval == 1) "year" else "years"
                }
                "Every $interval $unitStr"
            }
        }
    }

    fun formatDatesOnly(
        dates: List<LocalDate>,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern(formatOption.pattern, locale)
        return dates.joinToString("\n") { it.format(formatter) }
    }

    fun formatFullSummary(
        state: DateCycleUiState,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val rule = formatRecurrenceRule(
            state.recurrenceType,
            state.interval,
            state.intervalUnit,
            state.selectedWeekdays,
            state.monthlyMode,
            state.monthlyDay,
            locale
        )
        val startDateFormatted = formatDate(state.startDate, formatOption, locale)
        val datesFormatted = formatDatesOnly(state.generatedDates, formatOption, locale)

        val endConditionStr = if (state.endMode == EndMode.OCCURRENCE_COUNT) {
            "Occurrences:\n${state.generatedDates.size}"
        } else {
            val endFormatted = state.endDate?.let { formatDate(it, formatOption, locale) } ?: "N/A"
            "Until:\n$endFormatted\nTotal occurrences:\n${state.generatedDates.size}"
        }

        return buildString {
            appendLine("DateCycle")
            appendLine("Generate recurring dates from a starting date.")
            appendLine()
            appendLine("Start:")
            appendLine(startDateFormatted)
            appendLine()
            appendLine("Repeat:")
            appendLine(rule)
            appendLine()
            appendLine(endConditionStr)
            appendLine()
            appendLine("Generated dates:")
            appendLine()
            appendLine(datesFormatted)
        }.trimEnd()
    }
}
