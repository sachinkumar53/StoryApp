package com.sachin.app.storyapp.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DataModule {


    @PublicationCollectionRef
    @Provides
    fun providePublicationRef(
        db: FirebaseFirestore
    ): CollectionReference {
        return db.collection("publications")
    }

    @UserCollectionRef
    @Provides
    fun provideUsersRef(
        db: FirebaseFirestore
    ): CollectionReference {
        return db.collection("users")
    }

    @LibraryCollectionRef
    @Provides
    fun provideLibrariesRef(
        @UserCollectionRef
        users: CollectionReference,
        auth: FirebaseAuth
    ): CollectionReference {
        return users.document(auth.currentUser?.uid ?: "").collection("library")
    }

    @CoverStorageRef
    @Provides
    fun provideCoverStorageReference(
        storage: FirebaseStorage
    ): StorageReference {
        return storage.getReference("covers")
    }

    @ProfilePictureStorageRef
    @Provides
    fun provideProfilePictureStorageReference(
        storage: FirebaseStorage
    ): StorageReference {
        return storage.getReference("profile_pictures")
    }
}

@Retention
@Qualifier
annotation class LibraryCollectionRef

@Retention
@Qualifier
annotation class PublicationCollectionRef

@Retention
@Qualifier
annotation class UserCollectionRef

@Retention
@Qualifier
annotation class CoverStorageRef

@Retention
@Qualifier
annotation class ProfilePictureStorageRef

