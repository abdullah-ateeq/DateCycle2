package com.example.datecycle.ui.history

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.DateCycleHistory
import com.example.datecycle.model.IntervalUnit
import com.example.datecycle.model.MonthlyMode
import com.example.datecycle.model.RecurrenceType
import com.example.datecycle.ui.DateCycleViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: DateCycleViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    var itemToDelete by remember { mutableStateOf<DateCycleHistory?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (uiState.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("History", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(onClick = {
                            triggerHaptic()
                            showClearAllDialog = true
                        }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all history")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (uiState.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved recurrences yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap 'Save' on the home screen to store your recurrence configurations (up to 10 entries).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.history, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onRestore = {
                            triggerHaptic()
                            viewModel.restoreHistory(item)
                            onNavigateBack()
                        },
                        onDelete = {
                            triggerHaptic()
                            if (uiState.confirmDelete) {
                                itemToDelete = item
                            } else {
                                viewModel.deleteHistoryEntry(item.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // Delete single item confirmation dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Recurrence?") },
            text = { Text("Are you sure you want to delete this saved recurrence from history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteHistoryEntry(it.id) }
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear all history confirmation dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("Are you sure you want to clear all saved recurrences?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearAllDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: DateCycleHistory,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val parsedStart = runCatching { LocalDate.parse(item.startDate) }.getOrNull()
    val formattedStart = if (parsedStart != null) {
        DateCycleFormatter.formatDate(parsedStart)
    } else {
        item.startDate
    }

    val type = runCatching { RecurrenceType.valueOf(item.recurrenceType) }.getOrDefault(RecurrenceType.WEEKLY)
    val unit = runCatching { IntervalUnit.valueOf(item.intervalUnit) }.getOrDefault(IntervalUnit.WEEKS)
    val weekdays = item.selectedWeekdays.mapNotNull { runCatching { DayOfWeek.of(it) }.getOrNull() }.toSet()
    val monthlyMode = item.monthlyMode?.let { runCatching { MonthlyMode.valueOf(it) }.getOrNull() } ?: MonthlyMode.SAME_DAY

    val ruleStr = DateCycleFormatter.formatRecurrenceRule(
        type,
        item.interval,
        unit,
        weekdays,
        monthlyMode,
        item.monthlyDay ?: 1
    )

    val endDesc = if (item.endMode == "OCCURRENCE_COUNT") {
        "${item.occurrenceCount ?: 10} occurrences"
    } else {
        "Until ${item.endDate ?: ""}"
    }

    val timeFormatted = remember(item.savedAt) {
        val sdf = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(item.savedAt))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRestore() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "START: $formattedStart",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = ruleStr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = endDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Saved $timeFormatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRestore) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore this recurrence",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete this recurrence",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
