package com.example.datecycle

import com.example.datecycle.domain.calculator.DateCycleCalculator
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class DateCycleCalculatorAndFormatterTest {

    @Test
    fun testCalculatorResultSpan() {
        val start = LocalDate.of(2026, 10, 24)
        val dates = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 21),
            LocalDate.of(2026, 12, 5),
            LocalDate.of(2026, 12, 19),
            LocalDate.of(2027, 1, 2),
            LocalDate.of(2027, 1, 16),
            LocalDate.of(2027, 1, 30),
            LocalDate.of(2027, 2, 13),
            LocalDate.of(2027, 2, 27)
        )

        val result = DateCycleCalculator.calculateResult(
            startDate = start,
            generatedDates = dates,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 2,
            intervalUnit = IntervalUnit.WEEKS
        )

        assertNotNull(result)
        assertEquals(10, result!!.totalOccurrences)
        assertEquals(LocalDate.of(2026, 10, 24), result.firstDate)
        assertEquals(LocalDate.of(2027, 2, 27), result.lastDate)
        // 24 Oct 2026 to 27 Feb 2027:
        // Oct: 31 - 24 = 7 days
        // Nov: 30 days
        // Dec: 31 days
        // Jan: 31 days
        // Feb: 27 days
        // Total = 7 + 30 + 31 + 31 + 27 = 126 days!
        assertEquals(126L, result.spanDays)
    }

    @Test
    fun testMonthlySummaryCalculation() {
        val dates = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 10, 31),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 14),
            LocalDate.of(2026, 12, 5)
        )

        val summary = DateCycleCalculator.calculateMonthlySummary(dates)
        assertEquals(3, summary.size)
        assertEquals(YearMonth.of(2026, 10) to 2, summary[0])
        assertEquals(YearMonth.of(2026, 11) to 2, summary[1])
        assertEquals(YearMonth.of(2026, 12) to 1, summary[2])
    }

    @Test
    fun testYearlySummaryCalculation() {
        val dates = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 11, 24),
            LocalDate.of(2027, 1, 24),
            LocalDate.of(2027, 2, 24)
        )

        val summary = DateCycleCalculator.calculateYearlySummary(dates)
        assertEquals(2, summary.size)
        assertEquals(2026 to 2, summary[0])
        assertEquals(2027 to 2, summary[1])
    }

    @Test
    fun testNextOccurrence() {
        val today = LocalDate.of(2026, 10, 24)
        val dates = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 21)
        )

        val info = DateCycleCalculator.calculateNextOccurrence(dates, today = today)
        assertTrue(info.isToday)
        assertEquals(today, info.nextDate)

        // Case when today is between occurrences
        val midDay = LocalDate.of(2026, 10, 25)
        val infoAfterMid = DateCycleCalculator.calculateNextOccurrence(dates, today = midDay)
        assertFalse(infoAfterMid.isToday)
        assertEquals(LocalDate.of(2026, 11, 7), infoAfterMid.nextDate)
        assertEquals(13L, infoAfterMid.daysAway)
    }

    @Test
    fun testDateFormatting() {
        val date = LocalDate.of(2026, 10, 24)
        val usLocale = Locale.US

        assertEquals("24 October 2026", DateCycleFormatter.formatDate(date, DateFormatOption.DAY_MONTH_YEAR, usLocale))
        assertEquals("Oct 24, 2026", DateCycleFormatter.formatDate(date, DateFormatOption.MONTH_DAY_YEAR, usLocale))
        assertEquals("24/10/2026", DateCycleFormatter.formatDate(date, DateFormatOption.SLASH_DD_MM_YYYY, usLocale))
        assertEquals("2026-10-24", DateCycleFormatter.formatDate(date, DateFormatOption.ISO_YYYY_MM_DD, usLocale))
        assertEquals("Saturday", DateCycleFormatter.formatDayOfWeek(date, usLocale))
    }

    @Test
    fun testRecurrenceRuleFormatting() {
        val usLocale = Locale.US

        val dailyStr = DateCycleFormatter.formatRecurrenceRule(
            RecurrenceType.DAILY,
            3,
            IntervalUnit.DAYS,
            emptySet(),
            MonthlyMode.SAME_DAY,
            1,
            usLocale
        )
        assertEquals("Every 3 days", dailyStr)

        val weeklyStr = DateCycleFormatter.formatRecurrenceRule(
            RecurrenceType.WEEKLY,
            2,
            IntervalUnit.WEEKS,
            setOf(DayOfWeek.SATURDAY),
            MonthlyMode.SAME_DAY,
            1,
            usLocale
        )
        assertEquals("Every 2 weeks on Sat", weeklyStr)

        val monthlyLastDayStr = DateCycleFormatter.formatRecurrenceRule(
            RecurrenceType.MONTHLY,
            1,
            IntervalUnit.MONTHS,
            emptySet(),
            MonthlyMode.LAST_DAY,
            31,
            usLocale
        )
        assertEquals("Every month on the last day of month", monthlyLastDayStr)
    }
}
