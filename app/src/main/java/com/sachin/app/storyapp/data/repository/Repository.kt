package com.sachin.app.storyapp.data.repository

import kotlinx.coroutines.CancellationException

abstract class Repository {

    suspend fun <T> suspendSafeCall(block: suspend () -> T): Result<T> {
        return try {
            val result = block()
            Result.success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }
}