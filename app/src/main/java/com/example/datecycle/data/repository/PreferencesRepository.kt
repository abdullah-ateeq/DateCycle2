package com.example.datecycle.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.DateCycleHistory
import com.example.datecycle.model.EndMode
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.LeapDayMode
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import com.example.datecycle.model.ThemeMode
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "datecycle_preferences"
        private const val KEY_THEME = "pref_theme"
        private const val KEY_DYNAMIC_COLOR = "pref_dynamic_color"
        private const val KEY_RECURRENCE_TYPE = "pref_recurrence_type"
        private const val KEY_INTERVAL = "pref_interval"
        private const val KEY_INTERVAL_UNIT = "pref_interval_unit"
        private const val KEY_WEEKDAYS = "pref_weekdays"
        private const val KEY_MONTHLY_MODE = "pref_monthly_mode"
        private const val KEY_MONTHLY_DAY = "pref_monthly_day"
        private const val KEY_LEAP_DAY_MODE = "pref_leap_day_mode"
        private const val KEY_END_MODE = "pref_end_mode"
        private const val KEY_OCCURRENCE_COUNT = "pref_occurrence_count"
        private const val KEY_END_DATE = "pref_end_date"
        private const val KEY_START_DATE = "pref_start_date"
        private const val KEY_SHOW_CALENDAR = "pref_show_calendar"
        private const val KEY_SHOW_MONTHLY_SUMMARY = "pref_show_monthly_summary"
        private const val KEY_SHOW_YEARLY_SUMMARY = "pref_show_yearly_summary"
        private const val KEY_SHOW_DAY_OF_WEEK = "pref_show_day_of_week"
        private const val KEY_SHOW_OCCURRENCE_NUMBERS = "pref_show_occurrence_numbers"
        private const val KEY_SHOW_NEXT_DATE = "pref_show_next_date"
        private const val KEY_DATE_FORMAT = "pref_date_format"
        private const val KEY_LIVE_PREVIEW = "pref_live_preview"
        private const val KEY_HAPTIC_FEEDBACK = "pref_haptic_feedback"
        private const val KEY_ANIMATIONS = "pref_animations"
        private const val KEY_CONFIRM_DELETE = "pref_confirm_delete"
        private const val KEY_RESTORE_LAST_CONFIG = "pref_restore_last_config"
        private const val KEY_HISTORY = "pref_history_json"
        const val MAX_HISTORY_ENTRIES = 10
    }

    // Settings getters and setters
    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name) }.getOrDefault(ThemeMode.SYSTEM)
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var dynamicColor: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    var showCalendarPreview: Boolean
        get() = prefs.getBoolean(KEY_SHOW_CALENDAR, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_CALENDAR, value).apply()

    var showMonthlySummary: Boolean
        get() = prefs.getBoolean(KEY_SHOW_MONTHLY_SUMMARY, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_MONTHLY_SUMMARY, value).apply()

    var showYearlySummary: Boolean
        get() = prefs.getBoolean(KEY_SHOW_YEARLY_SUMMARY, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_YEARLY_SUMMARY, value).apply()

    var showDayOfWeek: Boolean
        get() = prefs.getBoolean(KEY_SHOW_DAY_OF_WEEK, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_DAY_OF_WEEK, value).apply()

    var showOccurrenceNumbers: Boolean
        get() = prefs.getBoolean(KEY_SHOW_OCCURRENCE_NUMBERS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_OCCURRENCE_NUMBERS, value).apply()

    var showNextDate: Boolean
        get() = prefs.getBoolean(KEY_SHOW_NEXT_DATE, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_NEXT_DATE, value).apply()

    var dateFormat: DateFormatOption
        get() = runCatching { DateFormatOption.valueOf(prefs.getString(KEY_DATE_FORMAT, DateFormatOption.DAY_MONTH_YEAR.name) ?: DateFormatOption.DAY_MONTH_YEAR.name) }.getOrDefault(DateFormatOption.DAY_MONTH_YEAR)
        set(value) = prefs.edit().putString(KEY_DATE_FORMAT, value.name).apply()

    var livePreview: Boolean
        get() = prefs.getBoolean(KEY_LIVE_PREVIEW, true)
        set(value) = prefs.edit().putBoolean(KEY_LIVE_PREVIEW, value).apply()

    var hapticFeedback: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, value).apply()

    var animations: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS, value).apply()

    var confirmDelete: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        set(value) = prefs.edit().putBoolean(KEY_CONFIRM_DELETE, value).apply()

    var restoreLastConfiguration: Boolean
        get() = prefs.getBoolean(KEY_RESTORE_LAST_CONFIG, true)
        set(value) = prefs.edit().putBoolean(KEY_RESTORE_LAST_CONFIG, value).apply()

    // Configuration persistence
    fun saveLastConfiguration(
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
    ) {
        val editor = prefs.edit()
        editor.putString(KEY_START_DATE, startDate.toString())
        editor.putString(KEY_RECURRENCE_TYPE, recurrenceType.name)
        editor.putInt(KEY_INTERVAL, interval)
        editor.putString(KEY_INTERVAL_UNIT, intervalUnit.name)
        editor.putString(KEY_WEEKDAYS, selectedWeekdays.map { it.value }.joinToString(","))
        editor.putString(KEY_MONTHLY_MODE, monthlyMode.name)
        editor.putInt(KEY_MONTHLY_DAY, monthlyDay)
        editor.putString(KEY_LEAP_DAY_MODE, leapDayMode.name)
        editor.putString(KEY_END_MODE, endMode.name)
        editor.putInt(KEY_OCCURRENCE_COUNT, occurrenceCount)
        editor.putString(KEY_END_DATE, endDate?.toString())
        editor.apply()
    }

    fun hasSavedConfiguration(): Boolean = prefs.contains(KEY_START_DATE)

    fun loadLastStartDate(): LocalDate? {
        val str = prefs.getString(KEY_START_DATE, null) ?: return null
        return runCatching { LocalDate.parse(str) }.getOrNull()
    }

    fun loadLastRecurrenceType(): RecurrenceType {
        val str = prefs.getString(KEY_RECURRENCE_TYPE, RecurrenceType.WEEKLY.name)
        return runCatching { RecurrenceType.valueOf(str ?: RecurrenceType.WEEKLY.name) }.getOrDefault(RecurrenceType.WEEKLY)
    }

    fun loadLastInterval(): Int = prefs.getInt(KEY_INTERVAL, 1).coerceAtLeast(1)

    fun loadLastIntervalUnit(): IntervalUnit {
        val str = prefs.getString(KEY_INTERVAL_UNIT, IntervalUnit.WEEKS.name)
        return runCatching { IntervalUnit.valueOf(str ?: IntervalUnit.WEEKS.name) }.getOrDefault(IntervalUnit.WEEKS)
    }

    fun loadLastSelectedWeekdays(): Set<DayOfWeek>? {
        val str = prefs.getString(KEY_WEEKDAYS, null) ?: return null
        if (str.isBlank()) return emptySet()
        return runCatching {
            str.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .map { DayOfWeek.of(it) }
                .toSet()
        }.getOrNull()
    }

    fun loadLastMonthlyMode(): MonthlyMode {
        val str = prefs.getString(KEY_MONTHLY_MODE, MonthlyMode.SAME_DAY.name)
        return runCatching { MonthlyMode.valueOf(str ?: MonthlyMode.SAME_DAY.name) }.getOrDefault(MonthlyMode.SAME_DAY)
    }

    fun loadLastMonthlyDay(): Int = prefs.getInt(KEY_MONTHLY_DAY, LocalDate.now().dayOfMonth).coerceIn(1, 31)

    fun loadLastLeapDayMode(): LeapDayMode {
        val str = prefs.getString(KEY_LEAP_DAY_MODE, LeapDayMode.FEBRUARY_28.name)
        return runCatching { LeapDayMode.valueOf(str ?: LeapDayMode.FEBRUARY_28.name) }.getOrDefault(LeapDayMode.FEBRUARY_28)
    }

    fun loadLastEndMode(): EndMode {
        val str = prefs.getString(KEY_END_MODE, EndMode.OCCURRENCE_COUNT.name)
        return runCatching { EndMode.valueOf(str ?: EndMode.OCCURRENCE_COUNT.name) }.getOrDefault(EndMode.OCCURRENCE_COUNT)
    }

    fun loadLastOccurrenceCount(): Int = prefs.getInt(KEY_OCCURRENCE_COUNT, 10).coerceIn(1, 1000)

    fun loadLastEndDate(): LocalDate? {
        val str = prefs.getString(KEY_END_DATE, null) ?: return null
        return runCatching { LocalDate.parse(str) }.getOrNull()
    }

    // History management
    fun getHistory(): List<DateCycleHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<DateCycleHistory>()
        runCatching {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val weekdaysArray = obj.optJSONArray("selectedWeekdays") ?: JSONArray()
                val weekdays = mutableListOf<Int>()
                for (j in 0 until weekdaysArray.length()) {
                    weekdays.add(weekdaysArray.getInt(j))
                }
                list.add(
                    DateCycleHistory(
                        id = obj.getString("id"),
                        startDate = obj.getString("startDate"),
                        recurrenceType = obj.getString("recurrenceType"),
                        interval = obj.getInt("interval"),
                        intervalUnit = obj.getString("intervalUnit"),
                        selectedWeekdays = weekdays,
                        monthlyMode = if (obj.has("monthlyMode") && !obj.isNull("monthlyMode")) obj.getString("monthlyMode") else null,
                        monthlyDay = if (obj.has("monthlyDay") && !obj.isNull("monthlyDay")) obj.getInt("monthlyDay") else null,
                        leapDayMode = if (obj.has("leapDayMode") && !obj.isNull("leapDayMode")) obj.getString("leapDayMode") else null,
                        endMode = obj.getString("endMode"),
                        occurrenceCount = if (obj.has("occurrenceCount") && !obj.isNull("occurrenceCount")) obj.getInt("occurrenceCount") else null,
                        endDate = if (obj.has("endDate") && !obj.isNull("endDate")) obj.getString("endDate") else null,
                        savedAt = obj.getLong("savedAt")
                    )
                )
            }
        }
        return list
    }

    fun saveHistoryEntry(newEntry: DateCycleHistory): Boolean {
        val currentList = getHistory().toMutableList()

        // Check for consecutive duplicate:
        if (currentList.isNotEmpty()) {
            val latest = currentList.first()
            val isDuplicate = latest.startDate == newEntry.startDate &&
                    latest.recurrenceType == newEntry.recurrenceType &&
                    latest.interval == newEntry.interval &&
                    latest.intervalUnit == newEntry.intervalUnit &&
                    latest.selectedWeekdays.sorted() == newEntry.selectedWeekdays.sorted() &&
                    latest.monthlyMode == newEntry.monthlyMode &&
                    latest.monthlyDay == newEntry.monthlyDay &&
                    latest.leapDayMode == newEntry.leapDayMode &&
                    latest.endMode == newEntry.endMode &&
                    latest.occurrenceCount == newEntry.occurrenceCount &&
                    latest.endDate == newEntry.endDate

            if (isDuplicate) {
                return false // Did not save duplicate
            }
        }

        currentList.add(0, newEntry)

        // Enforce max 10 entries limit
        while (currentList.size > MAX_HISTORY_ENTRIES) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveHistoryList(currentList)
        return true
    }

    fun deleteHistoryEntry(id: String) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveHistoryList(currentList)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    fun resetSettingsToDefaults() {
        prefs.edit()
            .remove(KEY_THEME)
            .remove(KEY_DYNAMIC_COLOR)
            .remove(KEY_SHOW_CALENDAR)
            .remove(KEY_SHOW_MONTHLY_SUMMARY)
            .remove(KEY_SHOW_YEARLY_SUMMARY)
            .remove(KEY_SHOW_DAY_OF_WEEK)
            .remove(KEY_SHOW_OCCURRENCE_NUMBERS)
            .remove(KEY_SHOW_NEXT_DATE)
            .remove(KEY_DATE_FORMAT)
            .remove(KEY_LIVE_PREVIEW)
            .remove(KEY_HAPTIC_FEEDBACK)
            .remove(KEY_ANIMATIONS)
            .remove(KEY_CONFIRM_DELETE)
            .remove(KEY_RESTORE_LAST_CONFIG)
            .apply()
    }

    private fun saveHistoryList(list: List<DateCycleHistory>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("startDate", item.startDate)
            obj.put("recurrenceType", item.recurrenceType)
            obj.put("interval", item.interval)
            obj.put("intervalUnit", item.intervalUnit)
            val weekdaysArray = JSONArray()
            item.selectedWeekdays.forEach { weekdaysArray.put(it) }
            obj.put("selectedWeekdays", weekdaysArray)
            obj.put("monthlyMode", item.monthlyMode)
            obj.put("monthlyDay", item.monthlyDay)
            obj.put("leapDayMode", item.leapDayMode)
            obj.put("endMode", item.endMode)
            obj.put("occurrenceCount", item.occurrenceCount)
            obj.put("endDate", item.endDate)
            obj.put("savedAt", item.savedAt)
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}
