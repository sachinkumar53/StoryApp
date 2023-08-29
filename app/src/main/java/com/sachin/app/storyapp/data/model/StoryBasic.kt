package com.sachin.app.storyapp.data.model

import androidx.recyclerview.widget.DiffUtil

data class StoryBasic(
    val id: String,
    val coverUrl: String,
    val title: String,
    val authorName: String,
    val rating: Float,
    val genre: String
) {

    class DiffUtilItemCallback : DiffUtil.ItemCallback<StoryBasic>() {
        override fun areItemsTheSame(oldItem: StoryBasic, newItem: StoryBasic): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryBasic, newItem: StoryBasic): Boolean {
            return oldItem == newItem
        }

    }

}