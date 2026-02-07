package com.example.core.ui.utils.formatting

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun Instant.toPickupDisplayLabel(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault(),
): String {
    val dateFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(locale)
        .withZone(zoneId)

    val timeFormatter = DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(locale)
        .withZone(zoneId)

    return "${dateFormatter.format(this)}, ${timeFormatter.format(this)}"
}

fun formatForOrderCard(
    pickupAtEpochMs: Long,
    locale: Locale = Locale.getDefault(),
    zoneId: ZoneId = ZoneId.systemDefault(),
): String {
    val instant = Instant.ofEpochMilli(pickupAtEpochMs)

    val dateFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.LONG)
        .withLocale(locale)
        .withZone(zoneId)

    val timeFormatter = DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(locale)
        .withZone(zoneId)

    return "${dateFormatter.format(instant)}, ${timeFormatter.format(instant)}"
}
