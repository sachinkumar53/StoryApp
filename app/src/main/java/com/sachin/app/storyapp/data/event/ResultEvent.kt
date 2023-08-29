package com.sachin.app.storyapp.data.event

import android.content.Context
import android.widget.Toast

sealed interface ResultEvent<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultEvent<T>
    data class Error(val message: String) : ResultEvent<Nothing>
}

fun ResultEvent<String>.showToast(context: Context) {
    when (this) {
        is ResultEvent.Error -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        is ResultEvent.Success -> Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
    }
}