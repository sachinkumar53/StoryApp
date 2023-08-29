package com.sachin.app.storyapp.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.Constant
import com.sachin.app.storyapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by viewModels()
    private val binding: FragmentLoginBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailField.addTextChangedListener {
            viewModel.onEvent(LoginEvent.OnEmailChanged(it?.toString() ?: ""))
        }

        binding.passwordField.addTextChangedListener {
            viewModel.onEvent(LoginEvent.OnPasswordChanged(it?.toString() ?: ""))
        }

        binding.loginButton.setOnClickListener {
            viewModel.onEvent(LoginEvent.OnLoginClick)
        }

        binding.goToSignup.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }

        subscribe()
    }

    private fun subscribe() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) { loginState ->
            handleUi(loginState)
        }

        viewModel.resultFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                handleResult(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun handleUi(uiState: LoginUiState) {
        binding.progressBar.isInvisible = !uiState.isLoading

        binding.emailField.isEnabled = !uiState.isLoading
        binding.passwordField.isEnabled = !uiState.isLoading
        binding.loginButton.isEnabled = !uiState.isLoading

        binding.emailField.error = "Please enter email".takeIf { uiState.isEmailError }
        binding.passwordField.error = "Please password email".takeIf { uiState.isPasswordError }
    }

    private fun handleResult(result: LoginResult?) {
        when (result) {
            is LoginResult.Error -> Toast.makeText(
                requireContext(),
                result.message,
                Toast.LENGTH_SHORT
            ).show()

            is LoginResult.Success -> {

                Toast.makeText(
                    requireContext(),
                    result.message,
                    Toast.LENGTH_SHORT
                ).show()

                if (result.userId == Constant.ADMIN_UID) {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAdminFragment())
                } else {
                    findNavController().navigate(LoginFragmentDirections.actionLoginToDashboard())
                }
            }

            else -> Unit
        }
    }


}