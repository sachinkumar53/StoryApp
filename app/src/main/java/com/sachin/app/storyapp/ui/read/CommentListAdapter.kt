package com.sachin.app.storyapp.ui.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.RelativeTimeFormatter
import com.sachin.app.storyapp.data.model.Comment
import com.sachin.app.storyapp.databinding.CommentItemBinding

class CommentListAdapter(
    private val onDeleteClick: (commentId: String) -> Unit
) : ListAdapter<Comment, CommentListAdapter.CommentViewHolder>(Comment.DiffUtilItemCallback()) {

    inner class CommentViewHolder(
        val binding: CommentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.moreButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    PopupMenu(binding.root.context, binding.moreButton).apply {
                        menuInflater.inflate(R.menu.rating_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.action_delete -> {
                                    onDeleteClick(getItem(adapterPosition).id)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                    //onDeleteClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: Comment) {
            binding.run {
                Glide.with(root)
                    .load(item.photoUrl)
                    .circleCrop()
                    .into(profileImageView)
                userNameTextview.text = item.username
                commentTextview.text = item.text
                timestampTextview.text =
                    RelativeTimeFormatter.getRelativeTimeSpanString(item.timestamp)
                moreButton.isVisible = item.userId == FirebaseAuth.getInstance().currentUser?.uid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(
        CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}