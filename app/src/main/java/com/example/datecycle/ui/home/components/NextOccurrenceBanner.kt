package com.example.datecycle.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.datecycle.domain.calculator.NextOccurrenceInfo
import com.example.datecycle.domain.formatter.DateCycleFormatter
import com.example.datecycle.model.DateFormatOption

@Composable
fun NextOccurrenceBanner(
    info: NextOccurrenceInfo,
    dateFormat: DateFormatOption,
    modifier: Modifier = Modifier
) {
    if (info.nextDate == null) return

    val formattedDate = DateCycleFormatter.formatDate(info.nextDate, dateFormat)
    val dayOfWeek = DateCycleFormatter.formatDayOfWeek(info.nextDate)

    val distanceText = when {
        info.isToday -> "TODAY"
        info.hasPastOnly -> "Final date in cycle (${info.daysAway} days ago)"
        info.daysAway == 1L -> "Tomorrow (1 day away)"
        else -> "${info.daysAway} days away"
    }

    val contentDesc = "Next occurrence, $dayOfWeek, $formattedDate."

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDesc }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "NEXT OCCURRENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$formattedDate ($dayOfWeek)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = distanceText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
