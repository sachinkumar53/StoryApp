package com.sachin.app.storyapp.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.StoryBasic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val TAG = "PublicationRepository"

class PublicationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    @PublicationCollectionRef
    private val publicationRef: CollectionReference,
    private val storage: FirebaseStorage,
    @UserCollectionRef
    private val users: CollectionReference,
    private val reviewRepository: ReviewRepository
) {
    private val currentUser = auth.currentUser

    suspend fun publish(
        cover: Bitmap,
        title: String,
        description: String,
        content: String,
        genre: String
    ) {
        //val coverName = title.replace(" ", "_").replace("-", "_").lowercase() + ".jpg"
        val storyId = publicationRef.document().id
        val baos = ByteArrayOutputStream()
        cover.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val result = storage.getReference("covers/$storyId").putBytes(baos.toByteArray()).await()
        val coverUrl = if (result.task.isSuccessful) {
            Log.i(TAG, "publish: getting url")
            result.storage.downloadUrl.await().also {
                Log.i(TAG, "publish: Url = $it")
            }
        } else ""

        publicationRef.document(storyId).set(
            mapOf(
                "cover_url" to coverUrl,
                "title" to title,
                "description" to description,
                "content" to content,
                "published_on" to System.currentTimeMillis(),
                "author_id" to currentUser?.uid,
                "genre" to genre
            )
        ).await()
    }

    fun getAllPublications(): Flow<List<StoryBasic>> {
        return publicationRef
            .orderBy("published_on", Query.Direction.DESCENDING)
            .snapshots()
            .mapLatest { snapshot ->
                snapshot.documents.map { document ->
                    val userId = document["author_id"].toString()
                    val username = users.document(userId).get().await()["name"].toString()
                    StoryBasic(
                        id = document.id,
                        coverUrl = document["cover_url"].toString(),
                        title = document["title"].toString(),
                        authorName = username,
                        rating = reviewRepository.getTotalRating(document.id)
                            .firstOrNull()?.totalRating ?: 0f,
                        genre = document["genre"].toString()
                    )
                }
            }
    }

    fun getMyPublications(): Flow<List<StoryBasic>> {
        return publicationRef
            .whereEqualTo("author_id", currentUser!!.uid)
            .orderBy("published_on", Query.Direction.DESCENDING)
            .snapshots()
            .mapLatest { snapshot ->
                snapshot.documents.map { document ->
                    StoryBasic(
                        id = document.id,
                        coverUrl = document["cover_url"].toString(),
                        title = document["title"].toString(),
                        authorName = currentUser.displayName.toString(),
                        rating = reviewRepository.getTotalRating(document.id)
                            .firstOrNull()?.totalRating ?: 0f,
                        genre = document["genre"].toString()
                    )
                }
            }
    }

    suspend fun queryStory(query: String): List<StoryBasic> {
        val snapshot = publicationRef
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()

        return snapshot.documents.map { document ->
            StoryBasic(
                id = document.id,
                coverUrl = document["cover_url"].toString(),
                title = document["title"].toString(),
                authorName = currentUser?.displayName.toString(),
                rating = reviewRepository.getTotalRating(document.id)
                    .firstOrNull()?.totalRating ?: 0f,
                genre = document["genre"].toString()
            )
        }
    }

    suspend fun deleteStory(storyId: String) {
        firestore.collection("recommended").document(storyId).delete().await()
        firestore.collection("top_rated").document(storyId).delete().await()
        publicationRef.document(storyId).delete().await()
        storage.getReference("covers/$storyId").delete()
    }

    private suspend fun CollectionReference.delete(batchSize: Int = 10) {
        try {
            // Retrieve a small batch of documents to avoid out-of-memory errors/
            val documents = this.limit(batchSize.toLong()).get().await()
            if (documents.isEmpty) return
            documents.forEach { document -> document.reference.delete() }
            // retrieve and delete another batch
            this.delete(batchSize)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            println("Error deleting collection : " + e.message)
        }
    }
}