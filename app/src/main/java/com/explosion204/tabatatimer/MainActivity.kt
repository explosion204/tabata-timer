package com.explosion204.tabatatimer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val timerViewModel : TimerListViewModel by viewModels {
        viewModelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavView.setupWithNavController(navController)
    }
}