package com.example.datecycle.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.DateCycleResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultSummaryCard(
    result: DateCycleResult,
    dateFormat: DateFormatOption,
    onCopyDates: () -> Unit,
    onCopySummary: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedFirst = DateCycleFormatter.formatDate(result.firstDate, dateFormat)
    val formattedLast = DateCycleFormatter.formatDate(result.lastDate, dateFormat)
    val recurrenceLabel = DateCycleFormatter.formatRecurrenceRule(
        result.recurrenceType,
        result.interval,
        result.intervalUnit,
        emptySet(),
        com.example.datecycle.model.MonthlyMode.SAME_DAY,
        result.startDate.dayOfMonth
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "GENERATED DATES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "${result.totalOccurrences}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (result.totalOccurrences == 1) "OCCURRENCE" else "OCCURRENCES",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = recurrenceLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$formattedFirst  →  $formattedLast",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${result.spanDays} days between first and last date",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action row for Copy, Save, Share
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCopyDates,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Dates", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onCopySummary,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Summary", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
