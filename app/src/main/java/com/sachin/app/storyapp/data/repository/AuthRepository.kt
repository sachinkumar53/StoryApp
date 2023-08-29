package com.sachin.app.storyapp.data.repository

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.sachin.app.storyapp.data.di.ProfilePictureStorageRef
import com.sachin.app.storyapp.data.di.UserCollectionRef
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ProfilePictureStorageRef
    private val profileStorageRef: StorageReference,
    @UserCollectionRef
    private val userCollectionRef: CollectionReference
) : Repository() {

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String> = suspendSafeCall {
        auth.signInWithEmailAndPassword(email, password).await()?.user?.uid
            ?: throw IllegalStateException("Invalid uid returned")
    }

    suspend fun signUpWithEmailAndPassword(
        profileImage: Bitmap?,
        name: String,
        email: String,
        password: String
    ): Result<String> = suspendSafeCall {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: return@suspendSafeCall "Failed to sign up"

        val photoUrl = if (profileImage != null) {
            val baos = ByteArrayOutputStream()
            profileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            profileStorageRef.child(user.uid).putBytes(baos.toByteArray()).await()
            profileStorageRef.child(user.uid).downloadUrl.await()
        } else null

        userCollectionRef.document(user.uid).set(
            mapOf(
                "id" to user.uid,
                "name" to name,
                "email" to email,
                "photo_url" to photoUrl
            )
        )

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build()
        )

        return@suspendSafeCall "Account created successfully!"
    }

}