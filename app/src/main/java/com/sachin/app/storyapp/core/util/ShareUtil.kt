package com.sachin.app.storyapp.core.util

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private suspend fun getSharingLinkForStory(
    storyId: String,
    coverUrl: String
): Uri? = withContext(Dispatchers.IO) {
    generateSharingLink(
        deepLink ="${Constant.PREFIX}/storyId/${storyId}".toUri(),// Uri.parse(Constant.PREFIX).buildUpon().appendQueryParameter("storyId",storyId).build(),
        previewImageLink = coverUrl.toUri(),
    )
}

private suspend fun generateSharingLink(
    deepLink: Uri,
    previewImageLink: Uri,
): Uri? {
    val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink().run {
        link = deepLink
        domainUriPrefix = Constant.PREFIX
        // Pass your preview Image Link here;
        setSocialMetaTagParameters(
            DynamicLink.SocialMetaTagParameters.Builder()
                .setImageUrl(previewImageLink)
                .build()
        )

        // Required
        androidParameters {
            build()
        }
        buildShortDynamicLink()
    }.await()
    Log.w(TAG, "generateSharingLink: ${dynamicLink.previewLink}")
    return dynamicLink.shortLink
}

private const val TAG = "ShareUtil"

fun Fragment.shareStoryLink(
    storyId: String,
    coverUrl: String
) {
    if (!isAdded) return
    val dialog = ProgressDialog(requireActivity())
    dialog.setMessage("Please wait")
    dialog.show()
    viewLifecycleOwner.lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "shareStoryLink: Error generating link.",throwable)
    }) {
        val uri = getSharingLinkForStory(storyId, coverUrl)
        dialog.hide()
        if (uri == null) {
            Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            return@launch
        }
        shareDeepLink(uri.toString())
    }
}

private fun Fragment.shareDeepLink(deepLink: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(
        Intent.EXTRA_SUBJECT,
        "Discover a captivating story! \uD83D\uDCD6 Check it out now! \uD83C\uDF1F"
    )
    intent.putExtra(Intent.EXTRA_TEXT, deepLink)
    if (isAdded) {
        requireContext().startActivity(intent)
    }
}