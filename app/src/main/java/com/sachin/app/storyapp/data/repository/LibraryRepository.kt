package com.sachin.app.storyapp.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.snapshots
import com.sachin.app.storyapp.data.di.LibraryCollectionRef
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.StoryBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    @LibraryCollectionRef
    private val library: CollectionReference,
    @UserCollectionRef
    private val users: CollectionReference,
    @PublicationCollectionRef
    private val publications: CollectionReference,
    private val reviewRepository: ReviewRepository

) {

    suspend fun addToLibrary(storyId: String) {
        if (isPresentInLibrary(storyId)) {
            throw Exception("Story already added to library!")
        }
        library.document(storyId).set(mapOf("added" to true)).await()
    }

    suspend fun removeFromLibrary(storyId: String) {
        if (!isPresentInLibrary(storyId)) {
            throw Exception("Story doesn't present in library!")
        }
        library.document(storyId).delete().await()
    }

    fun getLibraryItems(): Flow<List<StoryBasic>> {
        return library.snapshots().map { snapshot ->
            snapshot.documents.map { document ->
                val details = publications.document(document.id).get().await()
                val authorName = users.document(details["author_id"].toString()).get()
                    .await()
                    .data
                    ?.get("name")
                    .toString()

                StoryBasic(
                    coverUrl = details.get("cover_url").toString(),
                    title = details.get("title").toString(),
                    authorName = authorName,
                    id = document.id,
                    rating = reviewRepository.getTotalRating(document.id).firstOrNull()?.totalRating
                        ?: 0f,
                    genre = details["genre"].toString()
                )
            }
        }
    }

    private suspend fun isPresentInLibrary(storyId: String): Boolean {
        return library.document(storyId).get().await().exists()
    }

}