package com.sachin.app.storyapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.Comment
import com.sachin.app.storyapp.data.model.Rating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class ReviewRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @PublicationCollectionRef
    private val publications: CollectionReference,
    @UserCollectionRef
    private val users: CollectionReference
) {
    private val currentUserId get() = auth.currentUser!!.uid

    fun getMyRating(storyId: String): Flow<Int> {
        return publications.document(storyId)
            .collection("ratings")
            .document(currentUserId)
            .snapshots()
            .map { document ->
                if (document.exists()) {
                    (document["rating"] as Long).toInt()
                } else {
                    0
                }
            }
    }

    fun getTotalRating(storyId: String): Flow<Rating> {
        return publications.document(storyId)
            .collection("ratings")
            .snapshots()
            .map { snapshot ->
                val totalRating = snapshot.documents.sumOf { it["rating"] as Long }
                val count = snapshot.size()
                if (count > 0) {
                    Rating(totalRating.toFloat() / count.toFloat(), count)
                } else {
                    Rating(0f, 0)
                }
            }
    }

    suspend fun rateStory(storyId: String, rating: Int) {
        publications.document(storyId)
            .collection("ratings")
            .document(currentUserId)
            .set(mapOf("rating" to rating))
            .await()
    }

    suspend fun deleteRating(storyId: String) {
        publications.document(storyId)
            .collection("ratings")
            .document(currentUserId)
            .delete()
            .await()
    }

    suspend fun postComment(storyId: String, text: String) {
        val commentsRef = publications.document(storyId).collection("comments")
        val commentId = commentsRef.document().id
        commentsRef.document(commentId).set(
            mapOf(
                "text" to text,
                //       "id" to commentId,
                "user_id" to currentUserId,
                "timestamp" to System.currentTimeMillis(),
            )
        ).await()
    }

    suspend fun deleteComment(storyId: String, commentId: String) {
        publications.document(storyId)
            .collection("comments")
            .document(commentId)
            .delete()
            .await()
    }

    fun getComments(storyId: String): Flow<List<Comment>> {
        return publications.document(storyId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { document ->
                    val uid = document["user_id"].toString()
                    val userDoc = users.document(uid).get().await()
                    Comment(
                        id = document.id,
                        userId = uid,
                        timestamp = document["timestamp"] as Long,
                        text = document["text"].toString(),
                        username = userDoc["name"].toString(),
                        photoUrl = userDoc["photo_url"].toString()
                    )
                }
            }
    }

    fun getTopComment(storyId: String): Flow<Comment?> {
        return publications.document(storyId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { document ->
                    val uid = document["user_id"].toString()
                    val userDoc = users.document(uid).get().await()
                    Comment(
                        id = document.id,
                        userId = uid,
                        timestamp = document["timestamp"] as Long,
                        text = document["text"].toString(),
                        username = userDoc["name"].toString(),
                        photoUrl = userDoc["photo_url"].toString()
                    )
                }.firstOrNull()
            }
    }
}