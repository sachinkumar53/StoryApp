package com.sachin.app.storyapp.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.Coil
import coil.request.ImageRequest
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BitmapDecoder @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    suspend fun decodeSampledBitmapResource(
        any: Any?,
        reqWidth: Int = 100,
        reqHeight: Int = 100
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (any == null) return@withContext null
        val request = ImageRequest.Builder(context)
            .data(any)
            .size(reqWidth, reqHeight)
            .build()

        return@withContext (Coil.imageLoader(context)
            .execute(request).drawable as? BitmapDrawable)?.bitmap
    }

}