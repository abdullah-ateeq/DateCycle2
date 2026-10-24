package com.example.datecycle.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.datecycle.data.repository.PreferencesRepository
import com.example.datecycle.domain.generator.DateCycleGenerator
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.DateCycleHistory
import com.example.datecycle.model.DateCycleUiState
import com.example.datecycle.model.EndMode
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.LeapDayMode
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import com.example.datecycle.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class DateCycleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application.applicationContext)
    private val generator = DateCycleGenerator()

    private val _uiState = MutableStateFlow(DateCycleUiState())
    val uiState: StateFlow<DateCycleUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val today = LocalDate.now()
        val shouldRestore = repository.restoreLastConfiguration && repository.hasSavedConfiguration()

        val startDate = if (shouldRestore) repository.loadLastStartDate() ?: today else today
        val recurrenceType = if (shouldRestore) repository.loadLastRecurrenceType() else RecurrenceType.WEEKLY
        val interval = if (shouldRestore) repository.loadLastInterval() else 1
        val intervalUnit = if (shouldRestore) repository.loadLastIntervalUnit() else IntervalUnit.WEEKS
        val weekdays = if (shouldRestore) {
            repository.loadLastSelectedWeekdays() ?: setOf(startDate.dayOfWeek)
        } else {
            setOf(startDate.dayOfWeek)
        }
        val monthlyMode = if (shouldRestore) repository.loadLastMonthlyMode() else MonthlyMode.SAME_DAY
        val monthlyDay = if (shouldRestore) repository.loadLastMonthlyDay() else startDate.dayOfMonth
        val leapDayMode = if (shouldRestore) repository.loadLastLeapDayMode() else LeapDayMode.FEBRUARY_28
        val endMode = if (shouldRestore) repository.loadLastEndMode() else EndMode.OCCURRENCE_COUNT
        val occurrenceCount = if (shouldRestore) repository.loadLastOccurrenceCount() else 10
        val endDate = if (shouldRestore) repository.loadLastEndDate() else null

        val historyList = repository.getHistory()

        _uiState.update {
            it.copy(
                startDate = startDate,
                recurrenceType = recurrenceType,
                interval = interval,
                intervalUnit = intervalUnit,
                selectedWeekdays = if (weekdays.isEmpty()) setOf(startDate.dayOfWeek) else weekdays,
                monthlyMode = monthlyMode,
                monthlyDay = monthlyDay,
                leapDayMode = leapDayMode,
                endMode = endMode,
                occurrenceCount = occurrenceCount,
                endDate = endDate,
                history = historyList,
                isLivePreview = repository.livePreview,
                showCalendarPreview = repository.showCalendarPreview,
                showMonthlySummary = repository.showMonthlySummary,
                showYearlySummary = repository.showYearlySummary,
                showDayOfWeek = repository.showDayOfWeek,
                showOccurrenceNumbers = repository.showOccurrenceNumbers,
                showNextOccurrence = repository.showNextDate,
                dateFormat = repository.dateFormat,
                themeMode = repository.themeMode,
                dynamicColor = repository.dynamicColor,
                hapticFeedback = repository.hapticFeedback,
                animationsEnabled = repository.animations,
                confirmDelete = repository.confirmDelete,
                restoreLastConfiguration = repository.restoreLastConfiguration
            )
        }

        // Generate initial recurrence
        generateRecurrence()
    }

    fun setStartDate(date: LocalDate) {
        _uiState.update { current ->
            // If the user hasn't explicitly selected custom weekdays, update the selected weekday to match the new start date
            val updatedWeekdays = if (current.selectedWeekdays.size <= 1) {
                setOf(date.dayOfWeek)
            } else {
                current.selectedWeekdays
            }
            current.copy(
                startDate = date,
                selectedWeekdays = updatedWeekdays,
                monthlyDay = date.dayOfMonth,
                errorMessage = null,
                infoMessage = null
            )
        }
        onConfigurationChanged()
    }

    fun setToday() {
        setStartDate(LocalDate.now())
    }

    fun setRecurrenceType(type: RecurrenceType) {
        _uiState.update { current ->
            val updatedUnit = when (type) {
                RecurrenceType.DAILY -> IntervalUnit.DAYS
                RecurrenceType.WEEKLY -> IntervalUnit.WEEKS
                RecurrenceType.MONTHLY -> IntervalUnit.MONTHS
                RecurrenceType.YEARLY -> IntervalUnit.YEARS
                RecurrenceType.CUSTOM -> current.intervalUnit
            }
            current.copy(
                recurrenceType = type,
                intervalUnit = updatedUnit,
                errorMessage = null,
                infoMessage = null
            )
        }
        onConfigurationChanged()
    }

    fun setInterval(value: Int) {
        if (value < 1) {
            _uiState.update { it.copy(errorMessage = "Interval must be at least 1.") }
            return
        }
        _uiState.update { it.copy(interval = value, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun setIntervalUnit(unit: IntervalUnit) {
        _uiState.update { it.copy(intervalUnit = unit, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun toggleWeekday(day: DayOfWeek) {
        _uiState.update { current ->
            val set = current.selectedWeekdays.toMutableSet()
            if (set.contains(day)) {
                if (set.size > 1) {
                    set.remove(day)
                } // Keep at least one weekday selected
            } else {
                set.add(day)
            }
            current.copy(selectedWeekdays = set, errorMessage = null, infoMessage = null)
        }
        onConfigurationChanged()
    }

    fun setMonthlyMode(mode: MonthlyMode) {
        _uiState.update { it.copy(monthlyMode = mode, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun setMonthlyDay(day: Int) {
        val clamped = day.coerceIn(1, 31)
        _uiState.update { it.copy(monthlyDay = clamped, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun setLeapDayMode(mode: LeapDayMode) {
        _uiState.update { it.copy(leapDayMode = mode, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun setEndMode(mode: EndMode) {
        _uiState.update { current ->
            val defaultEndDate = if (current.endDate == null) current.startDate.plusMonths(3) else current.endDate
            current.copy(endMode = mode, endDate = defaultEndDate, errorMessage = null, infoMessage = null)
        }
        onConfigurationChanged()
    }

    fun setOccurrenceCount(count: Int) {
        if (count < 1) {
            _uiState.update { it.copy(errorMessage = "Occurrence count must be at least 1.") }
            return
        }
        if (count > DateCycleGenerator.MAX_OCCURRENCES) {
            _uiState.update { it.copy(errorMessage = "Maximum is 1000 occurrences.") }
            return
        }
        _uiState.update { it.copy(occurrenceCount = count, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    fun setEndDate(date: LocalDate) {
        if (date.isBefore(_uiState.value.startDate)) {
            _uiState.update { it.copy(errorMessage = "End date must be on or after the start date.") }
            return
        }
        _uiState.update { it.copy(endDate = date, errorMessage = null, infoMessage = null) }
        onConfigurationChanged()
    }

    private fun onConfigurationChanged() {
        persistCurrentConfiguration()
        if (_uiState.value.isLivePreview) {
            generateRecurrence()
        }
    }

    fun generateRecurrence() {
        val state = _uiState.value
        try {
            val dates = generator.generate(
                startDate = state.startDate,
                recurrenceType = state.recurrenceType,
                interval = state.interval,
                intervalUnit = state.intervalUnit,
                selectedWeekdays = state.selectedWeekdays,
                monthlyMode = state.monthlyMode,
                monthlyDay = state.monthlyDay,
                leapDayMode = state.leapDayMode,
                endMode = state.endMode,
                occurrenceCount = state.occurrenceCount,
                endDate = state.endDate
            )

            val warning = if (state.endMode == EndMode.END_DATE && dates.size >= DateCycleGenerator.MAX_OCCURRENCES) {
                "Generation limit reached. Showing the first 1000 occurrences."
            } else {
                null
            }

            _uiState.update {
                it.copy(
                    generatedDates = dates,
                    errorMessage = null,
                    warningMessage = warning
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = e.message ?: "Failed to generate dates.")
            }
        }
    }

    fun reset() {
        val today = LocalDate.now()
        _uiState.update { current ->
            current.copy(
                startDate = today,
                recurrenceType = RecurrenceType.WEEKLY,
                interval = 1,
                intervalUnit = IntervalUnit.WEEKS,
                selectedWeekdays = setOf(today.dayOfWeek),
                monthlyMode = MonthlyMode.SAME_DAY,
                monthlyDay = today.dayOfMonth,
                leapDayMode = LeapDayMode.FEBRUARY_28,
                endMode = EndMode.OCCURRENCE_COUNT,
                occurrenceCount = 10,
                endDate = null,
                generatedDates = emptyList(),
                errorMessage = null,
                warningMessage = null,
                infoMessage = "Configuration reset."
            )
        }
        persistCurrentConfiguration()
    }

    fun saveToHistory(): Boolean {
        val state = _uiState.value
        val entry = DateCycleHistory(
            id = UUID.randomUUID().toString(),
            startDate = state.startDate.toString(),
            recurrenceType = state.recurrenceType.name,
            interval = state.interval,
            intervalUnit = state.intervalUnit.name,
            selectedWeekdays = state.selectedWeekdays.map { it.value },
            monthlyMode = state.monthlyMode.name,
            monthlyDay = state.monthlyDay,
            leapDayMode = state.leapDayMode.name,
            endMode = state.endMode.name,
            occurrenceCount = if (state.endMode == EndMode.OCCURRENCE_COUNT) state.occurrenceCount else null,
            endDate = if (state.endMode == EndMode.END_DATE) state.endDate?.toString() else null,
            savedAt = System.currentTimeMillis()
        )

        val saved = repository.saveHistoryEntry(entry)
        val historyList = repository.getHistory()
        _uiState.update {
            it.copy(
                history = historyList,
                infoMessage = if (saved) "Recurrence saved to History." else "Recurrence already saved."
            )
        }
        return saved
    }

    fun restoreHistory(entry: DateCycleHistory) {
        val parsedStartDate = runCatching { LocalDate.parse(entry.startDate) }.getOrDefault(LocalDate.now())
        val parsedRecurrenceType = runCatching { RecurrenceType.valueOf(entry.recurrenceType) }.getOrDefault(RecurrenceType.WEEKLY)
        val parsedUnit = runCatching { IntervalUnit.valueOf(entry.intervalUnit) }.getOrDefault(IntervalUnit.WEEKS)
        val parsedWeekdays = entry.selectedWeekdays.mapNotNull { runCatching { DayOfWeek.of(it) }.getOrNull() }.toSet()
        val parsedMonthlyMode = entry.monthlyMode?.let { runCatching { MonthlyMode.valueOf(it) }.getOrNull() } ?: MonthlyMode.SAME_DAY
        val parsedLeapDayMode = entry.leapDayMode?.let { runCatching { LeapDayMode.valueOf(it) }.getOrNull() } ?: LeapDayMode.FEBRUARY_28
        val parsedEndMode = runCatching { EndMode.valueOf(entry.endMode) }.getOrDefault(EndMode.OCCURRENCE_COUNT)
        val parsedEndDate = entry.endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

        _uiState.update { current ->
            current.copy(
                startDate = parsedStartDate,
                recurrenceType = parsedRecurrenceType,
                interval = entry.interval.coerceAtLeast(1),
                intervalUnit = parsedUnit,
                selectedWeekdays = if (parsedWeekdays.isEmpty()) setOf(parsedStartDate.dayOfWeek) else parsedWeekdays,
                monthlyMode = parsedMonthlyMode,
                monthlyDay = (entry.monthlyDay ?: parsedStartDate.dayOfMonth).coerceIn(1, 31),
                leapDayMode = parsedLeapDayMode,
                endMode = parsedEndMode,
                occurrenceCount = (entry.occurrenceCount ?: 10).coerceIn(1, 1000),
                endDate = parsedEndDate,
                errorMessage = null,
                infoMessage = "Configuration restored."
            )
        }

        persistCurrentConfiguration()
        generateRecurrence()
    }

    fun deleteHistoryEntry(id: String) {
        repository.deleteHistoryEntry(id)
        _uiState.update { it.copy(history = repository.getHistory(), infoMessage = "Recurrence deleted.") }
    }

    fun clearAllHistory() {
        repository.clearHistory()
        _uiState.update { it.copy(history = emptyList(), infoMessage = "History cleared.") }
    }

    // Settings actions
    fun setThemeMode(mode: ThemeMode) {
        repository.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        repository.dynamicColor = enabled
        _uiState.update { it.copy(dynamicColor = enabled) }
    }

    fun setCalendarPreview(enabled: Boolean) {
        repository.showCalendarPreview = enabled
        _uiState.update { it.copy(showCalendarPreview = enabled) }
    }

    fun setMonthlySummary(enabled: Boolean) {
        repository.showMonthlySummary = enabled
        _uiState.update { it.copy(showMonthlySummary = enabled) }
    }

    fun setYearlySummary(enabled: Boolean) {
        repository.showYearlySummary = enabled
        _uiState.update { it.copy(showYearlySummary = enabled) }
    }

    fun setDayOfWeekDisplay(enabled: Boolean) {
        repository.showDayOfWeek = enabled
        _uiState.update { it.copy(showDayOfWeek = enabled) }
    }

    fun setOccurrenceNumbers(enabled: Boolean) {
        repository.showOccurrenceNumbers = enabled
        _uiState.update { it.copy(showOccurrenceNumbers = enabled) }
    }

    fun setNextOccurrenceDisplay(enabled: Boolean) {
        repository.showNextDate = enabled
        _uiState.update { it.copy(showNextOccurrence = enabled) }
    }

    fun setDateFormat(option: DateFormatOption) {
        repository.dateFormat = option
        _uiState.update { it.copy(dateFormat = option) }
    }

    fun setLivePreview(enabled: Boolean) {
        repository.livePreview = enabled
        _uiState.update { it.copy(isLivePreview = enabled) }
        if (enabled) {
            generateRecurrence()
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        repository.hapticFeedback = enabled
        _uiState.update { it.copy(hapticFeedback = enabled) }
    }

    fun setAnimations(enabled: Boolean) {
        repository.animations = enabled
        _uiState.update { it.copy(animationsEnabled = enabled) }
    }

    fun setConfirmDelete(enabled: Boolean) {
        repository.confirmDelete = enabled
        _uiState.update { it.copy(confirmDelete = enabled) }
    }

    fun setRestoreLastConfiguration(enabled: Boolean) {
        repository.restoreLastConfiguration = enabled
        _uiState.update { it.copy(restoreLastConfiguration = enabled) }
    }

    fun resetSettingsToDefaults() {
        repository.resetSettingsToDefaults()
        _uiState.update {
            it.copy(
                themeMode = repository.themeMode,
                dynamicColor = repository.dynamicColor,
                showCalendarPreview = repository.showCalendarPreview,
                showMonthlySummary = repository.showMonthlySummary,
                showYearlySummary = repository.showYearlySummary,
                showDayOfWeek = repository.showDayOfWeek,
                showOccurrenceNumbers = repository.showOccurrenceNumbers,
                showNextOccurrence = repository.showNextDate,
                dateFormat = repository.dateFormat,
                isLivePreview = repository.livePreview,
                hapticFeedback = repository.hapticFeedback,
                animationsEnabled = repository.animations,
                confirmDelete = repository.confirmDelete,
                restoreLastConfiguration = repository.restoreLastConfiguration,
                infoMessage = "Settings restored to defaults."
            )
        }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    private fun persistCurrentConfiguration() {
        val state = _uiState.value
        repository.saveLastConfiguration(
            startDate = state.startDate,
            recurrenceType = state.recurrenceType,
            interval = state.interval,
            intervalUnit = state.intervalUnit,
            selectedWeekdays = state.selectedWeekdays,
            monthlyMode = state.monthlyMode,
            monthlyDay = state.monthlyDay,
            leapDayMode = state.leapDayMode,
            endMode = state.endMode,
            occurrenceCount = state.occurrenceCount,
            endDate = state.endDate
        )
    }
}
