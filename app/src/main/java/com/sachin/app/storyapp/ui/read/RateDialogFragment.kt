package com.sachin.app.storyapp.ui.read

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.ui.dialog.MaterialDialogFragment
import com.sachin.app.storyapp.databinding.FragmentRatingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RateDialogFragment : MaterialDialogFragment(R.layout.fragment_rating) {
    private val binding: FragmentRatingBinding by viewBinding()
    private val viewModel: RatingViewModel by viewModels()

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingText = resources.getStringArray(R.array.rating_text)

        binding.discardButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val rateLevel = rating.toInt() - 1
            binding.ratingText.apply {
                text = ratingText[maxOf(0, rateLevel)]
                isInvisible = rateLevel < 0
            }
            binding.doneButton.isEnabled = rating >= 1.0
        }

        binding.doneButton.setOnClickListener {
            val pd = ProgressDialog(requireActivity())
            pd.setMessage("Please wait")
            pd.show()
            viewModel.rateStory(binding.ratingBar.rating.toInt()) {
                pd.hide()
                findNavController().navigateUp()
            }
        }
    }

}