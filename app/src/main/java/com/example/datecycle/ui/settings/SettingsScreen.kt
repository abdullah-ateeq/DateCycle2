package com.example.datecycle.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.ThemeMode
import com.example.datecycle.ui.DateCycleViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: DateCycleViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showResetDefaultsDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (uiState.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Appearance
            item {
                SectionHeader("APPEARANCE")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                ThemeMode.SYSTEM to "System",
                                ThemeMode.LIGHT to "Light",
                                ThemeMode.DARK to "Dark"
                            ).forEach { (mode, label) ->
                                FilterChip(
                                    selected = uiState.themeMode == mode,
                                    onClick = {
                                        triggerHaptic()
                                        viewModel.setThemeMode(mode)
                                    },
                                    label = { Text(label) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SettingToggleRow(
                            title = "Dynamic Color",
                            subtitle = "Use Material You dynamic accent colors where available",
                            checked = uiState.dynamicColor,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setDynamicColor(it)
                            }
                        )
                    }
                }
            }

            // Date Format
            item {
                SectionHeader("DATE FORMAT")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DateFormatOption.values().forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        triggerHaptic()
                                        viewModel.setDateFormat(option)
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (uiState.dateFormat == option) FontWeight.Bold else FontWeight.Normal,
                                        color = if (uiState.dateFormat == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = option.pattern,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (uiState.dateFormat == option) {
                                    androidx.compose.material3.RadioButton(
                                        selected = true,
                                        onClick = null
                                    )
                                } else {
                                    androidx.compose.material3.RadioButton(
                                        selected = false,
                                        onClick = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Display Settings
            item {
                SectionHeader("DISPLAY SETTINGS")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingToggleRow(
                            title = "Show Calendar Preview",
                            subtitle = "Interactive month view with marked recurrence dates",
                            checked = uiState.showCalendarPreview,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setCalendarPreview(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Show Monthly Summary",
                            subtitle = "Group recurrence counts by month",
                            checked = uiState.showMonthlySummary,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setMonthlySummary(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Show Yearly Summary",
                            subtitle = "Group recurrence counts by year",
                            checked = uiState.showYearlySummary,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setYearlySummary(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Show Day of Week",
                            subtitle = "Display Monday, Tuesday, etc. alongside dates",
                            checked = uiState.showDayOfWeek,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setDayOfWeekDisplay(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Show Occurrence Numbers",
                            subtitle = "Display #01, #02 badge markers",
                            checked = uiState.showOccurrenceNumbers,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setOccurrenceNumbers(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Show Next Occurrence",
                            subtitle = "Highlight upcoming occurrence and distance from today",
                            checked = uiState.showNextOccurrence,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setNextOccurrenceDisplay(it)
                            }
                        )
                    }
                }
            }

            // Behavior Settings
            item {
                SectionHeader("BEHAVIOR SETTINGS")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingToggleRow(
                            title = "Live Preview",
                            subtitle = "Regenerate sequence automatically as you adjust parameters",
                            checked = uiState.isLivePreview,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setLivePreview(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Haptic Feedback",
                            subtitle = "Subtle tactile responses for clicks and changes",
                            checked = uiState.hapticFeedback,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setHapticFeedback(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Animations",
                            subtitle = "Smooth transitions and state changes",
                            checked = uiState.animationsEnabled,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setAnimations(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Confirm Delete",
                            subtitle = "Require confirmation before deleting saved entries",
                            checked = uiState.confirmDelete,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setConfirmDelete(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingToggleRow(
                            title = "Restore Last Configuration",
                            subtitle = "Load previous recurrence parameters when opening the app",
                            checked = uiState.restoreLastConfiguration,
                            onCheckedChange = {
                                triggerHaptic()
                                viewModel.setRestoreLastConfiguration(it)
                            }
                        )
                    }
                }
            }

            // Reset Settings
            item {
                SectionHeader("DEFAULTS & DATA")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        triggerHaptic()
                        showResetDefaultsDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Reset Settings to Defaults")
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showResetDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showResetDefaultsDialog = false },
            title = { Text("Reset All Settings?") },
            text = { Text("Restore all display, appearance, and behavior preferences to default values? History will not be affected.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettingsToDefaults()
                        showResetDefaultsDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDefaultsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
