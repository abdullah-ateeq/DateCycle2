package com.example.datecycle.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datecycle.domain.formatter.DateCycleFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarPreviewView(
    generatedDates: List<LocalDate>,
    initialMonth: YearMonth = if (generatedDates.isNotEmpty()) YearMonth.from(generatedDates.first()) else YearMonth.now(),
    modifier: Modifier = Modifier
) {
    var currentMonth by remember(generatedDates) {
        mutableStateOf(if (generatedDates.isNotEmpty()) YearMonth.from(generatedDates.first()) else initialMonth)
    }

    val today = LocalDate.now()
    val markedDatesSet = remember(generatedDates) { generatedDates.toSet() }

    val daysOfWeek = remember {
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                }

                Text(
                    text = DateCycleFormatter.formatYearMonth(currentMonth).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day of week header (Mon - Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dow ->
                    val name = dow.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days grid
            val firstDayOfMonth = currentMonth.atDay(1)
            val daysInMonth = currentMonth.lengthOfMonth()
            val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1) // Monday = 0

            val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (weekIndex in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (dayIndex in 0 until 7) {
                            val cellIndex = weekIndex * 7 + dayIndex
                            val dayNumber = cellIndex - dayOfWeekOffset + 1

                            if (dayNumber in 1..daysInMonth) {
                                val cellDate = currentMonth.atDay(dayNumber)
                                val isMarked = cellDate in markedDatesSet
                                val isTodayDate = cellDate == today

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isMarked -> MaterialTheme.colorScheme.primary
                                                else -> androidx.compose.ui.graphics.Color.Transparent
                                            }
                                        )
                                        .then(
                                            if (isTodayDate && !isMarked) {
                                                Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                fontWeight = if (isMarked || isTodayDate) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isMarked -> MaterialTheme.colorScheme.onPrimary
                                                isTodayDate -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
