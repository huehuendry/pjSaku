package com.hendry.saku.utils

import java.text.NumberFormat
import java.util.Locale

fun Long.toRupiah(): String {

    val localeID = Locale("in", "ID")

    val formatter =
        NumberFormat.getCurrencyInstance(localeID)

    formatter.maximumFractionDigits = 0

    return formatter.format(this)
}