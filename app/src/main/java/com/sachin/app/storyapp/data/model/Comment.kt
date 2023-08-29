package com.sachin.app.storyapp.data.model

import androidx.recyclerview.widget.DiffUtil

data class Comment(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val text: String,
    val username: String,
    val photoUrl: String
) {

    class DiffUtilItemCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

    }
}