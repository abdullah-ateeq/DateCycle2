package com.example.datecycle.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datecycle.domain.calculator.DateCycleCalculator
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.RecurrenceType
import com.example.datecycle.ui.DateCycleViewModel
import com.example.datecycle.ui.home.components.CalendarPreviewView
import com.example.datecycle.ui.home.components.CustomIntervalSelector
import com.example.datecycle.ui.home.components.EndModeSelector
import com.example.datecycle.ui.home.components.GeneratedDateItem
import com.example.datecycle.ui.home.components.IntervalStepper
import com.example.datecycle.ui.home.components.MonthlyRuleSelector
import com.example.datecycle.ui.home.components.MonthlySummaryView
import com.example.datecycle.ui.home.components.NextOccurrenceBanner
import com.example.datecycle.ui.home.components.RecurrenceSelector
import com.example.datecycle.ui.home.components.ResultSummaryCard
import com.example.datecycle.ui.home.components.StartDateCard
import com.example.datecycle.ui.home.components.WeeklyDaySelector
import com.example.datecycle.ui.home.components.YearlyRuleSelector
import com.example.datecycle.ui.home.components.YearlySummaryView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DateCycleViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    fun triggerHaptic() {
        if (uiState.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearInfoMessage()
        }
    }

    val result = remember(uiState.generatedDates, uiState.startDate, uiState.recurrenceType, uiState.interval, uiState.intervalUnit) {
        DateCycleCalculator.calculateResult(
            startDate = uiState.startDate,
            generatedDates = uiState.generatedDates,
            recurrenceType = uiState.recurrenceType,
            interval = uiState.interval,
            intervalUnit = uiState.intervalUnit
        )
    }

    val nextOccurrenceInfo = remember(uiState.generatedDates) {
        DateCycleCalculator.calculateNextOccurrence(uiState.generatedDates)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DateCycle",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Generate recurring dates from a starting date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onNavigateToHistory()
                        }
                    ) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onNavigateToSettings()
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onNavigateToAbout()
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "About")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Start Date Card
            item {
                StartDateCard(
                    startDate = uiState.startDate,
                    dateFormat = uiState.dateFormat,
                    showDayOfWeek = uiState.showDayOfWeek,
                    onDateSelected = { date ->
                        triggerHaptic()
                        viewModel.setStartDate(date)
                    },
                    onTodayClicked = {
                        triggerHaptic()
                        viewModel.setToday()
                    }
                )
            }

            // 2. Recurrence Type Selector
            item {
                RecurrenceSelector(
                    selectedType = uiState.recurrenceType,
                    onTypeSelected = { type ->
                        triggerHaptic()
                        viewModel.setRecurrenceType(type)
                    }
                )
            }

            // 3. Conditional Recurrence Parameters
            item {
                when (uiState.recurrenceType) {
                    RecurrenceType.DAILY -> {
                        IntervalStepper(
                            label = "REPEAT EVERY",
                            unitSingular = "day",
                            unitPlural = "days",
                            interval = uiState.interval,
                            quickOptions = listOf(1, 2, 5, 30),
                            onIntervalChanged = {
                                triggerHaptic()
                                viewModel.setInterval(it)
                            }
                        )
                    }
                    RecurrenceType.WEEKLY -> {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            IntervalStepper(
                                label = "REPEAT EVERY",
                                unitSingular = "week",
                                unitPlural = "weeks",
                                interval = uiState.interval,
                                quickOptions = listOf(1, 2, 4),
                                onIntervalChanged = {
                                    triggerHaptic()
                                    viewModel.setInterval(it)
                                }
                            )

                            WeeklyDaySelector(
                                selectedDays = uiState.selectedWeekdays,
                                onToggleDay = { day ->
                                    triggerHaptic()
                                    viewModel.toggleWeekday(day)
                                }
                            )
                        }
                    }
                    RecurrenceType.MONTHLY -> {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            IntervalStepper(
                                label = "REPEAT EVERY",
                                unitSingular = "month",
                                unitPlural = "months",
                                interval = uiState.interval,
                                quickOptions = listOf(1, 2, 3, 6),
                                onIntervalChanged = {
                                    triggerHaptic()
                                    viewModel.setInterval(it)
                                }
                            )

                            MonthlyRuleSelector(
                                monthlyMode = uiState.monthlyMode,
                                monthlyDay = uiState.monthlyDay,
                                onMonthlyModeChanged = { mode ->
                                    triggerHaptic()
                                    viewModel.setMonthlyMode(mode)
                                },
                                onMonthlyDayChanged = { day ->
                                    triggerHaptic()
                                    viewModel.setMonthlyDay(day)
                                }
                            )
                        }
                    }
                    RecurrenceType.YEARLY -> {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            IntervalStepper(
                                label = "REPEAT EVERY",
                                unitSingular = "year",
                                unitPlural = "years",
                                interval = uiState.interval,
                                quickOptions = listOf(1, 2, 5),
                                onIntervalChanged = {
                                    triggerHaptic()
                                    viewModel.setInterval(it)
                                }
                            )

                            YearlyRuleSelector(
                                startDate = uiState.startDate,
                                leapDayMode = uiState.leapDayMode,
                                onLeapDayModeChanged = { mode ->
                                    triggerHaptic()
                                    viewModel.setLeapDayMode(mode)
                                }
                            )
                        }
                    }
                    RecurrenceType.CUSTOM -> {
                        CustomIntervalSelector(
                            interval = uiState.interval,
                            intervalUnit = uiState.intervalUnit,
                            onIntervalChanged = {
                                triggerHaptic()
                                viewModel.setInterval(it)
                            },
                            onUnitChanged = {
                                triggerHaptic()
                                viewModel.setIntervalUnit(it)
                            }
                        )
                    }
                }
            }

            // 4. Generation Limit / End Mode
            item {
                EndModeSelector(
                    endMode = uiState.endMode,
                    occurrenceCount = uiState.occurrenceCount,
                    endDate = uiState.endDate,
                    startDate = uiState.startDate,
                    dateFormat = uiState.dateFormat,
                    onEndModeChanged = { mode ->
                        triggerHaptic()
                        viewModel.setEndMode(mode)
                    },
                    onOccurrenceCountChanged = { count ->
                        triggerHaptic()
                        viewModel.setOccurrenceCount(count)
                    },
                    onEndDateChanged = { date ->
                        triggerHaptic()
                        viewModel.setEndDate(date)
                    }
                )
            }

            // 5. Action Buttons (Generate Dates / Reset)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            triggerHaptic()
                            viewModel.generateRecurrence()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .semantics { contentDescription = "Generate recurring dates." }
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE DATES",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.reset()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RESET", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Error / Warning messages
            if (uiState.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            if (uiState.warningMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.warningMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // 6. Results Section
            if (result != null && uiState.generatedDates.isNotEmpty()) {
                // Summary Card
                item {
                    ResultSummaryCard(
                        result = result,
                        dateFormat = uiState.dateFormat,
                        onCopyDates = {
                            triggerHaptic()
                            val text = DateCycleFormatter.formatDatesOnly(uiState.generatedDates, uiState.dateFormat)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("DateCycle Dates", text))
                            Toast.makeText(context, "Dates copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onCopySummary = {
                            triggerHaptic()
                            val text = DateCycleFormatter.formatFullSummary(uiState, uiState.dateFormat)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("DateCycle Summary", text))
                            Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onSave = {
                            triggerHaptic()
                            viewModel.saveToHistory()
                        },
                        onShare = {
                            triggerHaptic()
                            val text = DateCycleFormatter.formatFullSummary(uiState, uiState.dateFormat)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "DateCycle Recurrence")
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Dates"))
                        }
                    )
                }

                // Next Occurrence Banner
                if (uiState.showNextOccurrence) {
                    item {
                        NextOccurrenceBanner(
                            info = nextOccurrenceInfo,
                            dateFormat = uiState.dateFormat
                        )
                    }
                }

                // Calendar Preview
                if (uiState.showCalendarPreview) {
                    item {
                        CalendarPreviewView(
                            generatedDates = uiState.generatedDates
                        )
                    }
                }

                // Monthly Summary
                if (uiState.showMonthlySummary) {
                    item {
                        MonthlySummaryView(
                            generatedDates = uiState.generatedDates
                        )
                    }
                }

                // Yearly Summary
                if (uiState.showYearlySummary) {
                    item {
                        YearlySummaryView(
                            generatedDates = uiState.generatedDates
                        )
                    }
                }

                // Date List Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GENERATED SEQUENCE",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.generatedDates.size} items",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Items list
                itemsIndexed(uiState.generatedDates) { index, date ->
                    GeneratedDateItem(
                        occurrenceIndex = index + 1,
                        date = date,
                        isFirst = index == 0,
                        isLast = index == uiState.generatedDates.lastIndex,
                        endMode = uiState.endMode,
                        targetEndDate = uiState.endDate,
                        dateFormat = uiState.dateFormat,
                        showDayOfWeek = uiState.showDayOfWeek,
                        showOccurrenceNumbers = uiState.showOccurrenceNumbers
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
