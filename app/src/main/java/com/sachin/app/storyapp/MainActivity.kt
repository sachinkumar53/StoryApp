package com.sachin.app.storyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.sachin.app.storyapp.core.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        if (auth.currentUser != null) {
            val uid = auth.currentUser!!.uid
            if (uid == Constant.ADMIN_UID) {
                navController.navigate(
                    R.id.adminFragment,
                    null,
                    navOptions = NavOptions.Builder().setPopUpTo(R.id.signupFragment, true).build()
                )
            } else {
                navController.navigate(
                    R.id.dashboardFragment,
                    null,
                    navOptions = NavOptions.Builder().setPopUpTo(R.id.signupFragment, true).build()
                )
            }
        }
        auth.addAuthStateListener(this)

    }

    override fun onAuthStateChanged(authState: FirebaseAuth) {
        updateStartDestination(authState)
    }

    private fun updateStartDestination(authState: FirebaseAuth) {
        navController.graph.apply {
            /*if (authState.currentUser != null) {
                R.id.signupFragment
            } else {
                R.id.dashboardFragment
            }*/
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(this)
    }
}