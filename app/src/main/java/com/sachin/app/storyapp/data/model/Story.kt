package com.sachin.app.storyapp.data.model

import androidx.recyclerview.widget.DiffUtil.ItemCallback

data class Story(
    val id: String,
    val coverUrl: String,
    val title: String,
    val description: String,
    val content: String,
    val authorName: String,
    val publishedDate: String,
    val rating: Float,
    val genre: String
) {

    class DiffUtilItemCallback : ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }

    }
}

fun Story.toBasic(): StoryBasic {
    return StoryBasic(
        id = id,
        coverUrl = coverUrl,
        title = title,
        authorName = authorName,
        rating = rating,
        genre = genre
    )
}
