package com.sachin.app.storyapp.data.model

data class StoryDetail(
    val id: String,
    val coverUrl: String,
    val title: String,
    val description: String,
    val content: String,
    val authorName: String,
    val authorPhotoUrl: String,
    val publishedDate: String,
    val rating: Rating,
    val genre: String
)