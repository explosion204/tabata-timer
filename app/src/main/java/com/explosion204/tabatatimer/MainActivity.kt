package com.explosion204.tabatatimer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants.ACTION_CONTEXTUAL_MENU
import com.explosion204.tabatatimer.Constants.ACTION_NEW_SEQUENCE
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.Constants.SETTINGS_ACTIVITY_RESULT_CODE
import com.explosion204.tabatatimer.Constants.TAG_TIMER_LIST_FRAGMENT
import com.explosion204.tabatatimer.ui.activities.SettingsActivity
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import com.explosion204.tabatatimer.ui.helpers.ToolbarFontSizeHelper
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import java.util.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var fabMenu: FloatingActionMenu
    private lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val timerListViewModel : TimerListViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)
        val fontSize = preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")
        val toolbarTextAppearance: Int

        if (nightModeEnabled) {
            when (fontSize) {
                "0" -> setTheme(R.style.DarkTheme_SmallFont)
                "1" -> setTheme(R.style.DarkTheme_MediumFont)
                else -> setTheme(R.style.DarkTheme_LargeFont)
            }
        }
        else {
            when (fontSize) {
                "0" -> setTheme(R.style.LightTheme_SmallFont)
                "1" -> setTheme(R.style.LightTheme_MediumFont)
                else -> setTheme(R.style.LightTheme_LargeFont)
            }
        }

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        ToolbarFontSizeHelper.setToolbarFontSize(toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        fabMenu = findViewById(R.id.fab_menu)
        setUpBottomNavigation()
        findViewById<FloatingActionButton>(R.id.fab_timer).setOnClickListener {
            val intent = Intent(this, TimerDetailActivity::class.java)

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
            }, 100)
        }
        findViewById<FloatingActionButton>(R.id.fab_sequence).setOnClickListener {
            timerListViewModel.sendActionToFragment(TAG_TIMER_LIST_FRAGMENT, ACTION_NEW_SEQUENCE, null)
        }

        timerListViewModel.setActivityCallback(object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    ACTION_CONTEXTUAL_MENU -> {
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
        navController = findNavController(R.id.nav_fragment)
        bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.label) {
                "fragment_timers_list" -> {
                    fabMenu.showMenu(true)
                }
                "fragment_sequences_list" -> {
                    fabMenu.hideMenu(true)
                }
            }
        })
        bottomNavView.setOnNavigationItemSelectedListener {
            if (it.itemId != bottomNavView.selectedItemId) {
                NavigationUI.onNavDestinationSelected(it, navController)
            }

            true
        }
    }

    override fun onStop() {
        fabMenu.close(false)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_ACTIVITY_RESULT_CODE)
            }
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SETTINGS_ACTIVITY_RESULT_CODE -> {
                recreate()
            }
        }
    }
}