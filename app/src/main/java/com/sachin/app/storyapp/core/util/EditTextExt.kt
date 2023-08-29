package com.sachin.app.storyapp.core.util

import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

inline fun EditText.afterTextChanged(crossinline action: (Editable?) -> Unit) {
    addTextChangedListener(afterTextChanged = action)
}