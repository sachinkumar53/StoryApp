package com.sachin.app.storyapp.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.data.model.Story
import com.sachin.app.storyapp.databinding.CommunityItemBinding

class CommunityListAdapter(
    private val onItemClick: (story: Story) -> Unit,
    private val onAddToLibraryClick: (story: Story) -> Unit,
    private val onShareClick: (story: Story) -> Unit,
) : ListAdapter<Story, CommunityListAdapter.CommunityViewHolder>(Story.DiffUtilItemCallback()) {

    inner class CommunityViewHolder(
        private val binding: CommunityItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.run {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onItemClick(getItem(adapterPosition))
                    }
                }

                readMoreButton.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onItemClick(getItem(adapterPosition))
                    }
                }

                addToLibraryButton.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onAddToLibraryClick(getItem(adapterPosition))
                    }
                }

                shareButton.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onShareClick(getItem(adapterPosition))
                    }
                }
            }
        }

        fun bind(item: Story) = with(binding) {
            Glide.with(root).load(item.coverUrl).into(coverImageView)
            titleTextView.text = item.title
            authorTextView.text = root.resources.getString(R.string.author_name, item.authorName)
            publishDateTextView.text = item.publishedDate
            descTextView.text = item.description
            ratingTextView.text = if (item.rating > 0f) String.format("%.1f", item.rating) else "--"
            genreTextview.text = item.genre
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommunityViewHolder(
        CommunityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}