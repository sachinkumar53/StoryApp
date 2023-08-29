package com.sachin.app.storyapp.data.model

data class Rating(
    val totalRating: Float,
    val ratingCount: Int
) {
    companion object {
        fun zero(): Rating {
            return Rating(0f, 0)
        }
    }
}