package com.explosion204.tabatatimer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var fabMenu: FloatingActionMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpBottomNavigation()

        fabMenu = findViewById<FloatingActionMenu>(R.id.fab_menu)

        findViewById<FloatingActionButton>(R.id.fab_timer).setOnClickListener {
            val intent = Intent(this, TimerDetailActivity::class.java)

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }, 100)

        }


    }

    private fun setUpBottomNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavView.setupWithNavController(navController)
    }

    override fun onStop() {
        fabMenu.close(false)
        super.onStop()
    }
}