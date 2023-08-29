package com.sachin.app.storyapp.core.util

import android.text.format.DateUtils

class RelativeTimeFormatter {
    companion object {
        fun getRelativeTimeSpanString(timeInMillis: Long): String {
            val currentTime = System.currentTimeMillis()
            return DateUtils.getRelativeTimeSpanString(
                timeInMillis,
                currentTime,
                DateUtils.SECOND_IN_MILLIS
            ).toString()
        }
    }
}