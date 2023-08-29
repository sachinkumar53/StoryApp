package com.sachin.app.storyapp.ui.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.afterTextChanged
import com.sachin.app.storyapp.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private val binding: FragmentEditProfileBinding by viewBinding()
    private val viewModel: EditProfileViewModel by viewModels()
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.onPhotoUriChanged(uri)
        }

    /*private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->

        }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { uiState ->
                    Glide.with(requireContext())
                        .load(uiState.photoUrl)
                        .circleCrop()
                        .into(binding.profileImageView)
                    if (binding.nameField.text.toString() != uiState.name) {
                        binding.nameField.setText(uiState.name)
                    }
                    binding.nameInputLayout.error = uiState.nameError
                    binding.emailField.setText(uiState.email)
                }
        }

        binding.updateButton.setOnClickListener {
            val pd = ProgressDialog(requireActivity())
            pd.setMessage(getString(R.string.please_wait))
            pd.show()
            viewModel.update { success ->
                pd.hide()
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        R.string.profile_updated_successfully,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }
        }

        binding.nameField.afterTextChanged { viewModel.onNameChanged(it.toString()) }

        binding.profileImageView.setOnClickListener {
            galleryLauncher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
            )

            /*MaterialAlertDialogBuilder(requireActivity())
                .setItems(arrayOf("Camera", "Gallery")) { d, position ->
                    when (position) {
                        0 -> {
                            val file = File(requireContext().externalCacheDir, "temp.jpg")
                            val imageUri = Uri.fromFile(file)
                            cameraLauncher.launch(imageUri)
                        }

                        1 -> galleryLauncher.launch(
                            PickVisualMediaRequest.Builder().setMediaType(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            ).build()
                        )

                        else -> Unit
                    }
                }.show()*/

        }

    }

}