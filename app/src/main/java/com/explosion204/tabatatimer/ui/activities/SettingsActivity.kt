package com.explosion204.tabatatimer.ui.activities

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.Constants.LOCALE_PREFERENCE
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.fragments.SettingsFragment
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.ui.helpers.ToolbarFontSizeHelper
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)
        val fontSize = preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")

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

        updateLocale()

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            recreate()
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        ToolbarFontSizeHelper.setToolbarFontSize(toolbar)
        toolbar.title = getString(R.string.settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(getDrawable(R.drawable.ic_close_24))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateLocale() {
        val lang = preferences.getString(LOCALE_PREFERENCE, "en")
        val config = Configuration()
        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}