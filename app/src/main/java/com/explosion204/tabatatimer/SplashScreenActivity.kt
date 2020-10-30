package com.explosion204.tabatatimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.ui.activities.ListActivity
import com.explosion204.tabatatimer.ui.helpers.ActivityThemeHelper

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityThemeHelper.setActivityTheme(this)
        setContentView(R.layout.activity_splash_screen)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)) {
            val splashScreen = findViewById<ConstraintLayout>(R.id.splash_screen)
            splashScreen.setBackgroundColor(ContextCompat.getColor(this, R.color.deepDarkColor))
        }

        Handler(mainLooper).postDelayed({
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}