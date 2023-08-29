package com.sachin.app.storyapp.ui.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.ui.dialog.MaterialDialogFragment
import com.sachin.app.storyapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : MaterialDialogFragment(R.layout.fragment_profile) {
    private val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            Glide.with(requireContext())
                .load(it.photoUrl)
                .placeholder(R.drawable.ic_user_filled)
                .circleCrop()
                .into(binding.profileImageView)
            binding.nameTextView.text = it.displayName
            binding.emailTextView.text = it.email
        }
        val navController = findNavController()
        val rootNavController =
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)

        binding.editProfileButton.setOnClickListener {
            navController.navigateUp()
            rootNavController.navigate(R.id.action_dashboardFragment_to_editProfileFragment)
        }

        binding.myLibraryLayout.setOnClickListener {
            navController.navigate(ProfileFragmentDirections.actionProfileFragmentToLibraryFragment())
        }

        binding.myPublicationsLayout.setOnClickListener {
            navController.navigate(ProfileFragmentDirections.actionProfileFragmentToPublicationFragment())
        }
        binding.logoutLayout.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    auth.signOut()
                    rootNavController.navigate(R.id.action_dashboardFragment_to_loginFragment)
                }.setNegativeButton(R.string.no) { _, _ -> }
                .setCancelable(false)
                .show()

        }
    }
}