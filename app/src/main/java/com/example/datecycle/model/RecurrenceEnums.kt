package com.example.datecycle.model

enum class RecurrenceType {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}

enum class IntervalUnit {
    DAYS,
    WEEKS,
    MONTHS,
    YEARS
}

enum class MonthlyMode {
    SAME_DAY,
    LAST_DAY,
    CLAMP_INVALID,
    SKIP_INVALID
}

enum class LeapDayMode {
    FEBRUARY_28,
    FEBRUARY_29_OR_28,
    MARCH_1,
    SKIP
}

enum class EndMode {
    OCCURRENCE_COUNT,
    END_DATE
}

enum class DateFormatOption(val pattern: String, val label: String) {
    DAY_MONTH_YEAR("d MMMM yyyy", "24 October 2026"),
    MONTH_DAY_YEAR("MMM d, yyyy", "Oct 24, 2026"),
    SLASH_DD_MM_YYYY("dd/MM/yyyy", "24/10/2026"),
    ISO_YYYY_MM_DD("yyyy-MM-dd", "2026-10-24")
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
