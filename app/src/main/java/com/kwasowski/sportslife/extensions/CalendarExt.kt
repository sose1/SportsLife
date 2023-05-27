package com.kwasowski.sportslife.extensions

import java.util.Calendar
import java.util.Locale

fun Calendar.getNarrowName(): String =
    this.getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.NARROW_FORMAT,
        Locale.getDefault()
    ) as String