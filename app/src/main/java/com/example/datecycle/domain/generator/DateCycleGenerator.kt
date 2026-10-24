package com.example.datecycle.domain.generator

import com.example.datecycle.model.EndMode
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.LeapDayMode
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth

class DateCycleGenerator {

    companion object {
        const val MAX_OCCURRENCES = 1000
    }

    fun generate(
        startDate: LocalDate,
        recurrenceType: RecurrenceType,
        interval: Int,
        intervalUnit: IntervalUnit,
        selectedWeekdays: Set<DayOfWeek>,
        monthlyMode: MonthlyMode,
        monthlyDay: Int,
        leapDayMode: LeapDayMode,
        endMode: EndMode,
        occurrenceCount: Int,
        endDate: LocalDate?
    ): List<LocalDate> {
        require(interval >= 1) { "Interval must be at least 1." }

        when (endMode) {
            EndMode.OCCURRENCE_COUNT -> {
                require(occurrenceCount >= 1) { "Occurrence count must be at least 1." }
                require(occurrenceCount <= MAX_OCCURRENCES) { "Maximum is 1000 occurrences." }
                if (occurrenceCount == 1) {
                    return listOf(startDate)
                }
            }
            EndMode.END_DATE -> {
                requireNotNull(endDate) { "End date must be provided in end date mode." }
                require(!endDate.isBefore(startDate)) { "End date must be on or after the start date." }
                if (endDate == startDate) {
                    return listOf(startDate)
                }
            }
        }

        val targetCount = if (endMode == EndMode.OCCURRENCE_COUNT) {
            occurrenceCount.coerceAtMost(MAX_OCCURRENCES)
        } else {
            MAX_OCCURRENCES
        }

        val rawDates: List<LocalDate> = when (recurrenceType) {
            RecurrenceType.DAILY -> generateDaily(startDate, interval, endMode, targetCount, endDate)
            RecurrenceType.WEEKLY -> generateWeekly(startDate, interval, selectedWeekdays, endMode, targetCount, endDate)
            RecurrenceType.MONTHLY -> generateMonthly(startDate, interval, monthlyMode, monthlyDay, endMode, targetCount, endDate)
            RecurrenceType.YEARLY -> generateYearly(startDate, interval, leapDayMode, endMode, targetCount, endDate)
            RecurrenceType.CUSTOM -> {
                when (intervalUnit) {
                    IntervalUnit.DAYS -> generateDaily(startDate, interval, endMode, targetCount, endDate)
                    IntervalUnit.WEEKS -> generateWeekly(startDate, interval, selectedWeekdays, endMode, targetCount, endDate)
                    IntervalUnit.MONTHS -> generateMonthly(startDate, interval, monthlyMode, monthlyDay, endMode, targetCount, endDate)
                    IntervalUnit.YEARS -> generateYearly(startDate, interval, leapDayMode, endMode, targetCount, endDate)
                }
            }
        }

        // Post-processing: ensure startDate is occurrence #1, remove duplicates, ensure sorted, and limit
        val resultList = mutableListOf<LocalDate>()
        if (rawDates.isEmpty() || rawDates.first() != startDate) {
            resultList.add(startDate)
        }
        for (date in rawDates) {
            if (date !in resultList) {
                if (endMode == EndMode.END_DATE && endDate != null && date.isAfter(endDate)) {
                    continue
                }
                resultList.add(date)
            }
            if (resultList.size >= targetCount) {
                break
            }
        }

        val sorted = resultList.distinct().sorted()
        return if (sorted.size > targetCount) sorted.subList(0, targetCount) else sorted
    }

    private fun generateDaily(
        startDate: LocalDate,
        interval: Int,
        endMode: EndMode,
        targetCount: Int,
        endDate: LocalDate?
    ): List<LocalDate> {
        val list = mutableListOf<LocalDate>()
        var i = 0L
        while (list.size < targetCount) {
            val candidate = startDate.plusDays(i * interval.toLong())
            if (endMode == EndMode.END_DATE && endDate != null && candidate.isAfter(endDate)) {
                break
            }
            list.add(candidate)
            i++
        }
        return list
    }

    private fun generateWeekly(
        startDate: LocalDate,
        interval: Int,
        selectedWeekdays: Set<DayOfWeek>,
        endMode: EndMode,
        targetCount: Int,
        endDate: LocalDate?
    ): List<LocalDate> {
        val list = mutableListOf<LocalDate>()
        list.add(startDate)

        val effectiveDays = if (selectedWeekdays.isEmpty()) {
            setOf(startDate.dayOfWeek)
        } else {
            selectedWeekdays
        }.sortedBy { it.value }

        // Determine base Monday of start date's week
        val baseMonday = startDate.minusDays((startDate.dayOfWeek.value - 1).toLong())
        var cycleIndex = 0L

        while (list.size < targetCount) {
            val cycleMonday = baseMonday.plusWeeks(cycleIndex * interval.toLong())
            var allPastEndDate = false

            for (dow in effectiveDays) {
                val candidate = cycleMonday.plusDays((dow.value - 1).toLong())
                if (candidate.isBefore(startDate)) {
                    continue
                }
                if (candidate == startDate) {
                    continue // Already added as occurrence #1
                }
                if (endMode == EndMode.END_DATE && endDate != null && candidate.isAfter(endDate)) {
                    allPastEndDate = true
                    break
                }
                if (candidate !in list) {
                    list.add(candidate)
                }
                if (list.size >= targetCount) {
                    break
                }
            }

            if (allPastEndDate) {
                break
            }
            cycleIndex++
        }

        return list
    }

    private fun generateMonthly(
        startDate: LocalDate,
        interval: Int,
        monthlyMode: MonthlyMode,
        monthlyDay: Int,
        endMode: EndMode,
        targetCount: Int,
        endDate: LocalDate?
    ): List<LocalDate> {
        val list = mutableListOf<LocalDate>()
        val startYearMonth = YearMonth.from(startDate)
        var step = 0L

        while (list.size < targetCount) {
            val targetYm = startYearMonth.plusMonths(step * interval.toLong())

            val candidate: LocalDate? = when (monthlyMode) {
                MonthlyMode.LAST_DAY -> {
                    if (step == 0L && startDate != targetYm.atEndOfMonth()) {
                        startDate
                    } else {
                        targetYm.atEndOfMonth()
                    }
                }
                MonthlyMode.SKIP_INVALID -> {
                    if (step == 0L) {
                        startDate
                    } else if (monthlyDay <= targetYm.lengthOfMonth()) {
                        targetYm.atDay(monthlyDay)
                    } else {
                        null // Skip this invalid month
                    }
                }
                MonthlyMode.CLAMP_INVALID, MonthlyMode.SAME_DAY -> {
                    if (step == 0L) {
                        startDate
                    } else {
                        val validDay = monthlyDay.coerceAtMost(targetYm.lengthOfMonth())
                        targetYm.atDay(validDay)
                    }
                }
            }

            step++

            if (candidate == null) {
                continue
            }

            if (candidate.isBefore(startDate)) {
                continue
            }

            if (endMode == EndMode.END_DATE && endDate != null && candidate.isAfter(endDate)) {
                break
            }

            if (candidate !in list) {
                list.add(candidate)
            }
        }

        return list
    }

    private fun generateYearly(
        startDate: LocalDate,
        interval: Int,
        leapDayMode: LeapDayMode,
        endMode: EndMode,
        targetCount: Int,
        endDate: LocalDate?
    ): List<LocalDate> {
        val list = mutableListOf<LocalDate>()
        val isAnchorLeapDay = startDate.month == Month.FEBRUARY && startDate.dayOfMonth == 29
        var step = 0L

        while (list.size < targetCount) {
            val targetYear = startDate.year + (step * interval.toLong()).toInt()
            val candidate: LocalDate?

            if (step == 0L) {
                candidate = startDate
            } else if (isAnchorLeapDay) {
                val isLeap = Year.isLeap(targetYear.toLong())
                candidate = when {
                    isLeap -> LocalDate.of(targetYear, Month.FEBRUARY, 29)
                    leapDayMode == LeapDayMode.SKIP -> null
                    leapDayMode == LeapDayMode.MARCH_1 -> LocalDate.of(targetYear, Month.MARCH, 1)
                    else -> LocalDate.of(targetYear, Month.FEBRUARY, 28) // FEBRUARY_28 and FEBRUARY_29_OR_28
                }
            } else {
                candidate = LocalDate.of(targetYear, startDate.month, startDate.dayOfMonth)
            }

            step++

            if (candidate == null) {
                continue
            }

            if (candidate.isBefore(startDate)) {
                continue
            }

            if (endMode == EndMode.END_DATE && endDate != null && candidate.isAfter(endDate)) {
                break
            }

            if (candidate !in list) {
                list.add(candidate)
            }
        }

        return list
    }
}
