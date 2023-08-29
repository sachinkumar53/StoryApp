package com.sachin.app.storyapp.ui.publication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.data.model.StoryBasic
import com.sachin.app.storyapp.databinding.LibraryItemBinding

class PublicationAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<StoryBasic, PublicationAdapter.PublicationViewHolder>(StoryBasic.DiffUtilItemCallback()) {

    inner class PublicationViewHolder(
        private val binding: LibraryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition).id)
                }
            }
        }

        fun bind(story: StoryBasic) = binding.run {
            Glide.with(root).load(story.coverUrl).into(coverImageView)
            titleTextView.text = story.title
            authorTextView.text = root.resources.getString(R.string.author_name, story.authorName)
            menuButton.isVisible = false
            //llInfo.isVisible = false
            ratingTextView.text = if (story.rating > 0f) String.format("%.1f", story.rating) else "--"
            //ratingTextView.isVisible = story.rating > 0f
            genreTextview.text = story.genre
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PublicationViewHolder(
        LibraryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}