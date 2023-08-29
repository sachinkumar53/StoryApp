package com.sachin.app.storyapp.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.StoryBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @PublicationCollectionRef
    private val publications: CollectionReference,
    private val reviewRepository: ReviewRepository,
    @UserCollectionRef
    private val users: CollectionReference
) {

    fun getRecommended(): Flow<List<StoryBasic>> {
        return firestore.collection("recommended")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map {
                    val document = publications.document(it.id).get().await()
                    val authorName = users.document(document["author_id"].toString()).get()
                        .await()
                        .data
                        ?.get("name")
                        .toString()

                    StoryBasic(
                        coverUrl = document.get("cover_url").toString(),
                        title = document.get("title").toString(),
                        authorName = authorName,
                        id = document.id,
                        rating = reviewRepository.getTotalRating(document.id)
                            .firstOrNull()?.totalRating
                            ?: 0f,
                        genre = document["genre"].toString()
                    )
                }
            }
    }


    fun getTopRated(): Flow<List<StoryBasic>> {
        return firestore.collection("top_rated")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map {
                    val document = publications.document(it.id).get().await()
                    val authorName = users.document(document["author_id"].toString()).get()
                        .await()
                        .data
                        ?.get("name")
                        .toString()

                    StoryBasic(
                        coverUrl = document.get("cover_url").toString(),
                        title = document.get("title").toString(),
                        authorName = authorName,
                        id = document.id,
                        rating = reviewRepository.getTotalRating(document.id)
                            .firstOrNull()?.totalRating
                            ?: 0f,
                        genre = document["genre"].toString()
                    )
                }
            }
    }

    fun getNewReleases(): Flow<List<StoryBasic>> {
        return publications
            .orderBy("published_on", Query.Direction.DESCENDING)
            .limit(5)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { document ->
                    val authorName = users.document(document["author_id"].toString()).get()
                        .await()
                        .data
                        ?.get("name")
                        .toString()

                    StoryBasic(
                        coverUrl = document.get("cover_url").toString(),
                        title = document.get("title").toString(),
                        authorName = authorName,
                        id = document.id,
                        rating = reviewRepository.getTotalRating(document.id)
                            .firstOrNull()?.totalRating
                            ?: 0f,
                        genre = document["genre"].toString()
                    )
                }
            }
    }
}