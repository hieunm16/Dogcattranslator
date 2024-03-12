package com.wa.dog.cat.sound.prank.extension

import java.util.Calendar

fun Long.getDateTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar.get(Calendar.DATE).toString()
        .padStart(2, '0') + "/" + (calendar.get(Calendar.MONTH) + 1).toString()
        .padStart(2, '0') + "/" + calendar.get(
        Calendar.YEAR
    ).toString().padStart(2, '0')
}


