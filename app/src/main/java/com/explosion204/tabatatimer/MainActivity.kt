package com.explosion204.tabatatimer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.explosion204.tabatatimer.ui.Constants.CALLBACK_ACTION_CONTEXTUAL_MENU
import com.explosion204.tabatatimer.ui.Constants.CALLBACK_ACTION_PAUSE
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.SequenceListViewModel
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    companion object {
        const val CALLBACK_ACTION_NEW_SEQUENCE = "com.explosion204.tabatatimer.NEW_SEQUENCE_ACTION"
    }

    private lateinit var fabMenu: FloatingActionMenu

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val timerListViewModel : TimerListViewModel by viewModels {
        viewModelFactory
    }

    private val sequenceListViewModel : SequenceListViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpBottomNavigation()

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        fabMenu = findViewById(R.id.fab_menu)
        findViewById<FloatingActionButton>(R.id.fab_timer).setOnClickListener {
            val intent = Intent(this, TimerDetailActivity::class.java)

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }, 100)
        }
        findViewById<FloatingActionButton>(R.id.fab_sequence).setOnClickListener {
            timerListViewModel.sendActionToFragment(CALLBACK_ACTION_NEW_SEQUENCE, null)
        }

        timerListViewModel.setActivityCallback(object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    CALLBACK_ACTION_CONTEXTUAL_MENU -> {
                        val flag = arg as Boolean

                        if (flag) {
                            fabMenu.hideMenu(true)
                        }
                        else {
                            fabMenu.showMenu(true)
                        }
                    }

                    CALLBACK_ACTION_PAUSE -> {
                        val flag = arg as Boolean

                        if (flag) {
                            fabMenu.hideMenu(true)
                        }
                        else {
                            fabMenu.showMenu(true)
                        }
                    }
                }
            }
        })
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