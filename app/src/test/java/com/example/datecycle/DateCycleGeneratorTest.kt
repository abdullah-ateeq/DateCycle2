package com.example.datecycle

import com.example.datecycle.domain.generator.DateCycleGenerator
import com.example.datecycle.model.EndMode
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.LeapDayMode
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class DateCycleGeneratorTest {

    private val generator = DateCycleGenerator()

    @Test
    fun testDailyRecurrence() {
        val start = LocalDate.of(2026, 10, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.DAILY,
            interval = 1,
            intervalUnit = IntervalUnit.DAYS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 10, 25),
            LocalDate.of(2026, 10, 26),
            LocalDate.of(2026, 10, 27),
            LocalDate.of(2026, 10, 28)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testEvery3Days() {
        val start = LocalDate.of(2026, 10, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.DAILY,
            interval = 3,
            intervalUnit = IntervalUnit.DAYS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 10, 27),
            LocalDate.of(2026, 10, 30),
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 5)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testWeekly() {
        val start = LocalDate.of(2026, 10, 24) // Saturday
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 1,
            intervalUnit = IntervalUnit.WEEKS,
            selectedWeekdays = setOf(DayOfWeek.SATURDAY),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 10, 31),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 14),
            LocalDate.of(2026, 11, 21)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testBiweekly() {
        val start = LocalDate.of(2026, 10, 24) // Saturday
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 2,
            intervalUnit = IntervalUnit.WEEKS,
            selectedWeekdays = setOf(DayOfWeek.SATURDAY),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 21),
            LocalDate.of(2026, 12, 5),
            LocalDate.of(2026, 12, 19)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testMultipleWeekdays() {
        val start = LocalDate.of(2026, 10, 19) // Monday
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 1,
            intervalUnit = IntervalUnit.WEEKS,
            selectedWeekdays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 6,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 19), // Mon
            LocalDate.of(2026, 10, 21), // Wed
            LocalDate.of(2026, 10, 23), // Fri
            LocalDate.of(2026, 10, 26), // Mon
            LocalDate.of(2026, 10, 28), // Wed
            LocalDate.of(2026, 10, 30)  // Fri
        )
        assertEquals(expected, result)
    }

    @Test
    fun testMonthlySameDay() {
        val start = LocalDate.of(2026, 10, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 1,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = 24,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 11, 24),
            LocalDate.of(2026, 12, 24),
            LocalDate.of(2027, 1, 24),
            LocalDate.of(2027, 2, 24)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testJanuary31Clamp() {
        val start = LocalDate.of(2026, 1, 31)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 1,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.CLAMP_INVALID,
            monthlyDay = 31,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2026, 3, 31),
            LocalDate.of(2026, 4, 30),
            LocalDate.of(2026, 5, 31)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testJanuary31Skip() {
        val start = LocalDate.of(2026, 1, 31)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 1,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SKIP_INVALID,
            monthlyDay = 31,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 3, 31),
            LocalDate.of(2026, 5, 31),
            LocalDate.of(2026, 7, 31),
            LocalDate.of(2026, 8, 31)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testLastDayOfMonth() {
        val start = LocalDate.of(2026, 1, 31)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 1,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.LAST_DAY,
            monthlyDay = 31,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2026, 3, 31),
            LocalDate.of(2026, 4, 30),
            LocalDate.of(2026, 5, 31)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testEvery3MonthsAnchorPreservation() {
        val start = LocalDate.of(2026, 1, 31)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 3,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.CLAMP_INVALID,
            monthlyDay = 31,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 4, 30),
            LocalDate.of(2026, 7, 31),
            LocalDate.of(2026, 10, 31),
            LocalDate.of(2027, 1, 31)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testYearly() {
        val start = LocalDate.of(2026, 10, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.YEARLY,
            interval = 1,
            intervalUnit = IntervalUnit.YEARS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2027, 10, 24),
            LocalDate.of(2028, 10, 24),
            LocalDate.of(2029, 10, 24),
            LocalDate.of(2030, 10, 24)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testLeapDayFeb28Mode() {
        val start = LocalDate.of(2024, 2, 29)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.YEARLY,
            interval = 1,
            intervalUnit = IntervalUnit.YEARS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2024, 2, 29),
            LocalDate.of(2025, 2, 28),
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2027, 2, 28),
            LocalDate.of(2028, 2, 29)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testLeapDayMarch1Mode() {
        val start = LocalDate.of(2024, 2, 29)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.YEARLY,
            interval = 1,
            intervalUnit = IntervalUnit.YEARS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.MARCH_1,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 5,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2024, 2, 29),
            LocalDate.of(2025, 3, 1),
            LocalDate.of(2026, 3, 1),
            LocalDate.of(2027, 3, 1),
            LocalDate.of(2028, 2, 29)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testLeapDaySkipMode() {
        val start = LocalDate.of(2024, 2, 29)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.YEARLY,
            interval = 1,
            intervalUnit = IntervalUnit.YEARS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.SKIP,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 3,
            endDate = null
        )

        val expected = listOf(
            LocalDate.of(2024, 2, 29),
            LocalDate.of(2028, 2, 29),
            LocalDate.of(2032, 2, 29)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testOccurrenceCount1() {
        val start = LocalDate.of(2026, 10, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.DAILY,
            interval = 1,
            intervalUnit = IntervalUnit.DAYS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 1,
            endDate = null
        )

        assertEquals(listOf(start), result)
    }

    @Test
    fun testEndDate() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 1,
            intervalUnit = IntervalUnit.WEEKS,
            selectedWeekdays = setOf(DayOfWeek.THURSDAY),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.END_DATE,
            occurrenceCount = 10,
            endDate = end
        )

        val expected = listOf(
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 8),
            LocalDate.of(2026, 10, 15),
            LocalDate.of(2026, 10, 22),
            LocalDate.of(2026, 10, 29)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testEndDateExactMatch() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 29)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.DAILY,
            interval = 7,
            intervalUnit = IntervalUnit.DAYS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.END_DATE,
            occurrenceCount = 10,
            endDate = end
        )

        assertTrue(result.contains(end))
        assertEquals(LocalDate.of(2026, 10, 29), result.last())
    }

    @Test
    fun testEndDateBeforeStart() {
        val start = LocalDate.of(2026, 10, 31)
        val end = LocalDate.of(2026, 10, 1)
        try {
            generator.generate(
                startDate = start,
                recurrenceType = RecurrenceType.DAILY,
                interval = 1,
                intervalUnit = IntervalUnit.DAYS,
                selectedWeekdays = setOf(start.dayOfWeek),
                monthlyMode = MonthlyMode.SAME_DAY,
                monthlyDay = start.dayOfMonth,
                leapDayMode = LeapDayMode.FEBRUARY_28,
                endMode = EndMode.END_DATE,
                occurrenceCount = 10,
                endDate = end
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("End date must be on or after the start date.", e.message)
        }
    }

    @Test
    fun testZeroInterval() {
        val start = LocalDate.of(2026, 10, 24)
        try {
            generator.generate(
                startDate = start,
                recurrenceType = RecurrenceType.DAILY,
                interval = 0,
                intervalUnit = IntervalUnit.DAYS,
                selectedWeekdays = setOf(start.dayOfWeek),
                monthlyMode = MonthlyMode.SAME_DAY,
                monthlyDay = start.dayOfMonth,
                leapDayMode = LeapDayMode.FEBRUARY_28,
                endMode = EndMode.OCCURRENCE_COUNT,
                occurrenceCount = 10,
                endDate = null
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Interval must be at least 1.", e.message)
        }
    }

    @Test
    fun testMaximumOccurrences() {
        val start = LocalDate.of(2026, 10, 24)
        try {
            generator.generate(
                startDate = start,
                recurrenceType = RecurrenceType.DAILY,
                interval = 1,
                intervalUnit = IntervalUnit.DAYS,
                selectedWeekdays = setOf(start.dayOfWeek),
                monthlyMode = MonthlyMode.SAME_DAY,
                monthlyDay = start.dayOfMonth,
                leapDayMode = LeapDayMode.FEBRUARY_28,
                endMode = EndMode.OCCURRENCE_COUNT,
                occurrenceCount = 1001,
                endDate = null
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Maximum is 1000 occurrences.", e.message)
        }
    }

    @Test
    fun testNoDuplicatesAndChronologicalOrder() {
        val start = LocalDate.of(2026, 1, 1)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.WEEKLY,
            interval = 2,
            intervalUnit = IntervalUnit.WEEKS,
            selectedWeekdays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = 1,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 50,
            endDate = null
        )

        assertEquals(50, result.size)
        // Check uniqueness
        assertEquals(result.distinct().size, result.size)
        // Check chronological order
        for (i in 0 until result.size - 1) {
            assertTrue("Dates must be strictly chronological", result[i].isBefore(result[i + 1]))
        }
    }

    @Test
    fun testLargeRangePerformance() {
        val start = LocalDate.of(2000, 1, 1)
        val startTime = System.currentTimeMillis()
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.DAILY,
            interval = 1,
            intervalUnit = IntervalUnit.DAYS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = start.dayOfMonth,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 1000,
            endDate = null
        )
        val duration = System.currentTimeMillis() - startTime
        assertEquals(1000, result.size)
        assertEquals(LocalDate.of(2000, 1, 1), result.first())
        assertTrue("Generation of 1000 dates should take under 50ms, took ${duration}ms", duration < 50)
    }

    @Test
    fun testMonthYearBoundary() {
        val start = LocalDate.of(2026, 12, 24)
        val result = generator.generate(
            startDate = start,
            recurrenceType = RecurrenceType.MONTHLY,
            interval = 1,
            intervalUnit = IntervalUnit.MONTHS,
            selectedWeekdays = setOf(start.dayOfWeek),
            monthlyMode = MonthlyMode.SAME_DAY,
            monthlyDay = 24,
            leapDayMode = LeapDayMode.FEBRUARY_28,
            endMode = EndMode.OCCURRENCE_COUNT,
            occurrenceCount = 2,
            endDate = null
        )
        assertEquals(listOf(LocalDate.of(2026, 12, 24), LocalDate.of(2027, 1, 24)), result)
    }
}
