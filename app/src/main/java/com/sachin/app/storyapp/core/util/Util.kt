package com.sachin.app.storyapp.core.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


fun Activity.hideSoftKeyboard() {
    val view: View? = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.clearFocus()
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}