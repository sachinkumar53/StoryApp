package com.sachin.app.storyapp.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.data.model.StoryBasic
import com.sachin.app.storyapp.databinding.LibraryItemBinding

class LibraryListAdapter(
    private val onItemClick: (story: StoryBasic) -> Unit,
    private val onMenuClick: (anchor: View, story: StoryBasic) -> Unit
) : ListAdapter<StoryBasic, LibraryListAdapter.LibraryViewHolder>(StoryBasic.DiffUtilItemCallback()) {

    inner class LibraryViewHolder(
        private val binding: LibraryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition))
                }
            }

            binding.menuButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onMenuClick(it, getItem(adapterPosition))
                }
            }
        }

        fun bind(item: StoryBasic) = binding.run {
            Glide.with(root).load(item.coverUrl).into(coverImageView)
            titleTextView.text = item.title
            authorTextView.text = root.resources.getString(R.string.author_name, item.authorName)
            ratingTextView.text = if (item.rating > 0f) String.format("%.1f", item.rating) else "--"
//            ratingTextView.isVisible = item.rating > 0f
            genreTextview.text = item.genre
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LibraryViewHolder(
        LibraryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}