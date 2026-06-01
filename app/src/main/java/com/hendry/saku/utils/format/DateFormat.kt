package com.hendry.saku.utils.format

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toReadableDate(): String {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale("id", "ID")
    )

    return formatter.format(Date(this))
}