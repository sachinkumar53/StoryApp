package com.sachin.app.storyapp.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val binding: FragmentDashboardBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToSearchFragment()
            )
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToNavGraphCompose()
            )
        }

        val navHostFragment =
            childFragmentManager.findFragmentById(binding.homeNavHostFragment.id) as NavHostFragment
        val childNavController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(childNavController)
        setupBackNavigation(childNavController)

        FirebaseAuth.getInstance().currentUser?.let {
            Glide.with(requireContext())
                .load(it.photoUrl)
                .placeholder(R.drawable.ic_user_filled)
                .circleCrop()
                .into(binding.profileImageView)
            binding.toolbar.title = "Hello, " + it.displayName
        }

        binding.profileImageView.setOnClickListener {
            childNavController.navigate(R.id.profileFragment)
        }
    }


    private fun setupBackNavigation(navController: NavController) {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack(
                    navController.graph.startDestinationId,
                    inclusive = false
                )
            }

        }
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            val isAtStartDestination = destination.id == controller.graph.startDestinationId
            backPressedCallback.isEnabled = !isAtStartDestination
        }

        (activity as? AppCompatActivity)?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }
}