package com.example.datecycle.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.datecycle.model.RecurrenceType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecurrenceSelector(
    selectedType: RecurrenceType,
    onTypeSelected: (RecurrenceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        RecurrenceType.DAILY to "Every day",
        RecurrenceType.WEEKLY to "Every week",
        RecurrenceType.MONTHLY to "Every month",
        RecurrenceType.YEARLY to "Every year",
        RecurrenceType.CUSTOM to "Custom interval"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "REPEAT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (type, label) ->
                val isSelected = selectedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { onTypeSelected(type) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}
