package com.example.datecycle.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.DateFormatOption
import com.example.datecycle.model.EndMode
import java.time.LocalDate

@Composable
fun GeneratedDateItem(
    occurrenceIndex: Int,
    date: LocalDate,
    isFirst: Boolean,
    isLast: Boolean,
    endMode: EndMode,
    targetEndDate: LocalDate?,
    dateFormat: DateFormatOption,
    showDayOfWeek: Boolean,
    showOccurrenceNumbers: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedDate = DateCycleFormatter.formatDate(date, dateFormat)
    val dayOfWeek = DateCycleFormatter.formatDayOfWeek(date)
    val occurrenceNumberFormatted = String.format("#%02d", occurrenceIndex)

    val contentDesc = "Occurrence $occurrenceIndex, $dayOfWeek, $formattedDate."

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isFirst -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                isLast -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDesc }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showOccurrenceNumbers) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isFirst -> MaterialTheme.colorScheme.primary
                            isLast -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.padding(end = 14.dp)
                    ) {
                        Text(
                            text = occurrenceNumberFormatted,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                isFirst -> MaterialTheme.colorScheme.onPrimary
                                isLast -> MaterialTheme.colorScheme.onSecondary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (showDayOfWeek) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dayOfWeek,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tag for START / LAST / ON END DATE / BEFORE END DATE
            when {
                isFirst -> {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "START",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                isLast -> {
                    val endLabel = if (endMode == EndMode.END_DATE && targetEndDate != null) {
                        if (date == targetEndDate) "ON END DATE" else "BEFORE END"
                    } else {
                        "LAST"
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = endLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
