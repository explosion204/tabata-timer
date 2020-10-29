package com.explosion204.tabatatimer.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.fragments.SettingsFragment
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.Constants.SETTINGS_THEME_CHANGED

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)

        if (nightModeEnabled) {
            setTheme(R.style.DarkTheme)
        }
        else {
            setTheme(R.style.LightTheme)
        }

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                NIGHT_MODE_PREFERENCE -> {
                    recreate()
                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        toolbar.title = getString(R.string.settings)
        setSupportActionBar(toolbar)
    }
}