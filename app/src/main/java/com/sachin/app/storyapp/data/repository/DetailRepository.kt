package com.sachin.app.storyapp.data.repository

import android.content.Context
import android.text.format.DateFormat
import com.google.firebase.firestore.CollectionReference
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.Rating
import com.sachin.app.storyapp.data.model.StoryDetail
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DetailRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @PublicationCollectionRef
    private val publications: CollectionReference,
    @UserCollectionRef
    private val users: CollectionReference,
    private val reviewRepository: ReviewRepository
) {

    suspend fun getStory(storyId: String): StoryDetail {
        return publications.document(storyId)
            .get().await().let { document ->
                val author = users.document(document["author_id"].toString()).get().await()
                val rating = reviewRepository.getTotalRating(storyId).firstOrNull()
                StoryDetail(
                    id = document.id,
                    coverUrl = document["cover_url"].toString(),
                    title = document["title"].toString(),
                    description = document["description"].toString(),
                    content = document["content"].toString(),
                    publishedDate = DateFormat.getLongDateFormat(context).format(
                        document["published_on"].toString().toLong()
                    ),
                    authorName = author["name"].toString(),
                    authorPhotoUrl = author["photo_url"].toString(),
                    rating = rating ?: Rating.zero(),
                    genre = document["genre"].toString()
                )
            }
    }
}


