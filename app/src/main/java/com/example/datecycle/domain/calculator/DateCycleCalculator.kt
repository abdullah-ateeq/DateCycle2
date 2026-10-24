package com.example.datecycle.domain.calculator

import com.example.datecycle.model.DateCycleResult
import com.example.datecycle.model.GeneratedDateMarker
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.RecurrenceType
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class NextOccurrenceInfo(
    val nextDate: LocalDate?,
    val isToday: Boolean,
    val daysAway: Long,
    val hasPastOnly: Boolean
)

object DateCycleCalculator {

    fun calculateResult(
        startDate: LocalDate,
        generatedDates: List<LocalDate>,
        recurrenceType: RecurrenceType,
        interval: Int,
        intervalUnit: IntervalUnit
    ): DateCycleResult? {
        if (generatedDates.isEmpty()) return null
        val firstDate = generatedDates.first()
        val lastDate = generatedDates.last()
        val spanDays = ChronoUnit.DAYS.between(firstDate, lastDate)

        return DateCycleResult(
            startDate = startDate,
            generatedDates = generatedDates,
            recurrenceType = recurrenceType,
            interval = interval,
            intervalUnit = intervalUnit,
            firstDate = firstDate,
            lastDate = lastDate,
            totalOccurrences = generatedDates.size,
            spanDays = spanDays
        )
    }

    fun calculateMonthlySummary(generatedDates: List<LocalDate>): List<Pair<YearMonth, Int>> {
        val map = linkedMapOf<YearMonth, Int>()
        for (date in generatedDates) {
            val ym = YearMonth.from(date)
            map[ym] = (map[ym] ?: 0) + 1
        }
        return map.toList()
    }

    fun calculateYearlySummary(generatedDates: List<LocalDate>): List<Pair<Int, Int>> {
        val map = linkedMapOf<Int, Int>()
        for (date in generatedDates) {
            val year = date.year
            map[year] = (map[year] ?: 0) + 1
        }
        return map.toList()
    }

    fun calculateNextOccurrence(
        generatedDates: List<LocalDate>,
        today: LocalDate = LocalDate.now()
    ): NextOccurrenceInfo {
        if (generatedDates.isEmpty()) {
            return NextOccurrenceInfo(
                nextDate = null,
                isToday = false,
                daysAway = 0,
                hasPastOnly = false
            )
        }

        // Check if today is in generated dates
        if (today in generatedDates) {
            return NextOccurrenceInfo(
                nextDate = today,
                isToday = true,
                daysAway = 0,
                hasPastOnly = false
            )
        }

        // Find first date strictly after today
        val upcoming = generatedDates.firstOrNull { it.isAfter(today) }
        return if (upcoming != null) {
            val daysAway = ChronoUnit.DAYS.between(today, upcoming)
            NextOccurrenceInfo(
                nextDate = upcoming,
                isToday = false,
                daysAway = daysAway,
                hasPastOnly = false
            )
        } else {
            // All dates in the past: display the final generated date as requested
            NextOccurrenceInfo(
                nextDate = generatedDates.last(),
                isToday = false,
                daysAway = ChronoUnit.DAYS.between(today, generatedDates.last()),
                hasPastOnly = true
            )
        }
    }

    fun generateCalendarMarkers(generatedDates: List<LocalDate>): List<GeneratedDateMarker> {
        return generatedDates.mapIndexed { index, date ->
            GeneratedDateMarker(
                date = date,
                occurrenceNumber = index + 1
            )
        }
    }
}
