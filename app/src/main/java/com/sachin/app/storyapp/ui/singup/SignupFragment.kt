package com.sachin.app.storyapp.ui.singup

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignupFragment : Fragment(R.layout.fragment_signup) {
    private val viewModel: SignupViewModel by viewModels()
    private val binding: FragmentSignupBinding by viewBinding()
    private val galleryLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.onEvent(SignUpEvent.OnProfilePictureChanged(it.data?.data))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameField.addTextChangedListener {
            viewModel.onEvent(SignUpEvent.OnNameChanged(it?.toString() ?: ""))
        }
        binding.emailField.addTextChangedListener {
            viewModel.onEvent(SignUpEvent.OnEmailChanged(it?.toString() ?: ""))
        }
        binding.passwordField.addTextChangedListener {
            viewModel.onEvent(SignUpEvent.OnPasswordChanged(it?.toString() ?: ""))
        }

        binding.confirmPasswordField.addTextChangedListener {
            viewModel.onEvent(SignUpEvent.OnConfirmPasswordChanged(it?.toString() ?: ""))
        }
        binding.singupButton.setOnClickListener {
            viewModel.onEvent(SignUpEvent.OnSignUpClick)
        }

        binding.goToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }

        subscribe()
    }

    private fun subscribe() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            handleUi(it)
        }

        viewModel.resultFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                handleResult(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun handleResult(result: SignUpResult?) {
        when (result) {
            is SignUpResult.Error -> Toast.makeText(
                requireContext(),
                result.message,
                Toast.LENGTH_SHORT
            ).show()

            is SignUpResult.Success -> {
                Toast.makeText(
                    requireContext(),
                    result.message,
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(SignupFragmentDirections.actionSignupToDashboard())
            }

            else -> Unit
        }
    }

    private fun handleUi(uiState: SignUpUiState) {
        binding.progressBar.isInvisible = !uiState.isLoading
        binding.singupButton.isInvisible = uiState.isLoading

        binding.profileImageView.load(uiState.profileBitmap)
        binding.addProfileImageView.isVisible = !uiState.isLoading && uiState.profileBitmap == null
        binding.profileProgressBar.isVisible = uiState.isLoadingProfileImage
        binding.addProfileImageView.setOnClickListener {
            galleryLauncher.launch(Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI))
        }

        binding.nameField.isEnabled = !uiState.isLoading
        binding.emailField.isEnabled = !uiState.isLoading
        binding.passwordField.isEnabled = !uiState.isLoading
        binding.singupButton.isEnabled = !uiState.isLoading

        binding.nameInputLayout.error = uiState.nameError
        binding.emailInputLayout.error = uiState.emailError
        binding.passwordInputLayout.error = uiState.passwordError
        binding.confirmPasswordInputLayout.error = uiState.confirmPasswordError
    }
}