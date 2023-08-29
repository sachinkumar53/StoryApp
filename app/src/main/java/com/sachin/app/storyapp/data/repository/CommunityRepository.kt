package com.sachin.app.storyapp.data.repository

import android.icu.text.RelativeDateTimeFormatter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.sachin.app.storyapp.data.di.PublicationCollectionRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.Story
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CommunityRepository @Inject constructor(
    @PublicationCollectionRef
    private val publications: CollectionReference,
    @UserCollectionRef
    private val users: CollectionReference,
    private val reviewRepository: ReviewRepository
) {

    suspend fun getRecentPublications(): List<Story> {
        return publications.orderBy(
            "published_on",
            Query.Direction.DESCENDING
        ).get().await().documents
            .map { snapshot ->
                val authorName = users.document(snapshot["author_id"].toString()).get()
                    .await()
                    .data
                    ?.get("name")
                    .toString()

                val millisDiff = System.currentTimeMillis()
                    .toDuration(DurationUnit.MILLISECONDS) - (snapshot.get("published_on") as? Long
                    ?: 0L).toDuration(DurationUnit.MILLISECONDS)

                getRelativeTimeSpanString(millisDiff)

                Story(
                    coverUrl = snapshot.get("cover_url").toString(),
                    title = snapshot.get("title").toString(),
                    description = snapshot.get("description").toString(),
                    content = snapshot.get("content").toString(),
                    publishedDate = getRelativeTimeSpanString(millisDiff).toString()
                    /*DateUtils.getRelativeTimeSpanString(
                        snapshot.get("published_on") as? Long ?: 0L,
                        System.currentTimeMillis(),
                        DateUtils.HOUR_IN_MILLIS
                    ).toString()*/,
                    authorName = authorName,
                    id = snapshot.id,
                    rating = reviewRepository.getTotalRating(snapshot.id).firstOrNull()?.totalRating
                        ?: 0f,
                    genre = snapshot["genre"].toString()
                )
            }
    }

    private fun getRelativeTimeSpanString(millisDiff: Duration): String? {
        val formatter = RelativeDateTimeFormatter.getInstance()
        return when {
            millisDiff.inWholeSeconds == 0L -> {
                "moments ago"
            }

            millisDiff.inWholeMinutes == 0L -> {
                formatter.format(
                    millisDiff.inWholeSeconds.toDouble(),
                    RelativeDateTimeFormatter.Direction.LAST,
                    RelativeDateTimeFormatter.RelativeUnit.SECONDS
                )
            }

            millisDiff.inWholeHours == 0L -> {
                formatter.format(
                    millisDiff.inWholeMinutes.toDouble(),
                    RelativeDateTimeFormatter.Direction.LAST,
                    RelativeDateTimeFormatter.RelativeUnit.MINUTES
                )
            }

            millisDiff.inWholeDays == 0L -> {
                formatter.format(
                    millisDiff.inWholeHours.toDouble(),
                    RelativeDateTimeFormatter.Direction.LAST,
                    RelativeDateTimeFormatter.RelativeUnit.HOURS
                )
            }

            else -> {
                val days = millisDiff.inWholeDays
                val months = (days / 30.417).toInt()
                formatter.format(
                    days.toDouble(),
                    RelativeDateTimeFormatter.Direction.LAST,
                    RelativeDateTimeFormatter.RelativeUnit.DAYS
                )


                val t = if (months == 0) {
                    formatter.format(
                        millisDiff.inWholeDays.toDouble(),
                        RelativeDateTimeFormatter.Direction.LAST,
                        RelativeDateTimeFormatter.RelativeUnit.DAYS
                    )
                } else if (months < 12) {
                    formatter.format(
                        months.toDouble(),
                        RelativeDateTimeFormatter.Direction.LAST,
                        RelativeDateTimeFormatter.RelativeUnit.MONTHS
                    )
                } else {
                    formatter.format(
                        (months.toDouble() / 12).toInt().toDouble(),
                        RelativeDateTimeFormatter.Direction.LAST,
                        RelativeDateTimeFormatter.RelativeUnit.MONTHS
                    )
                }
                t
            }
        }
    }

}


