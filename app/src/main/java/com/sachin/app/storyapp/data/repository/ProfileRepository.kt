package com.sachin.app.storyapp.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.sachin.app.storyapp.core.util.BitmapDecoder
import com.sachin.app.storyapp.data.di.ProfilePictureStorageRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import com.sachin.app.storyapp.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @UserCollectionRef
    private val users: CollectionReference,
    @ProfilePictureStorageRef
    private val profileStorageRef: StorageReference,
    private val bitmapDecoder: BitmapDecoder
) {
    private val uid get() = auth.currentUser?.uid!!

    fun getProfile(): Flow<UserProfile> {
        return users.document(uid)
            .snapshots()
            .map { document ->
                UserProfile(
                    name = document["name"].toString(),
                    email = document["email"].toString(),
                    photoUrl = document["photo_url"].toString(),
                    uid = document.id
                )
            }
    }

    suspend fun updateUserDetails(name: String, photoUri: Uri?, localImage: Boolean) {
        var photoUrl: Uri? = null

        if (photoUri != null && localImage) {
            bitmapDecoder.decodeSampledBitmapResource(photoUri)?.let { bitmap ->
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val ref = profileStorageRef.child(uid)
                try {
                    ref.delete().await()
                } catch (_: StorageException) {
                }
                ref.putBytes(baos.toByteArray()).await()
                photoUrl = profileStorageRef.child(uid).downloadUrl.await()
            }
        }

        users.document(uid).update(
            if (photoUrl == null) {
                mapOf("name" to name)
            } else {
                mapOf(
                    "name" to name,
                    "photo_url" to photoUrl
                )
            }
        ).await()

        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build()
        )?.await()
    }
}