package com.explosion204.tabatatimer.ui.helpers

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.R

class FragmentThemeHelper {
    companion object {
        fun buildCustomInflater(context: Context, baseInflater: LayoutInflater) : LayoutInflater {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val nightModeEnabled = preferences.getBoolean(Constants.NIGHT_MODE_PREFERENCE, false)
            val fontSize = preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")

            val contextThemeWrapper = if (nightModeEnabled) {
                when (fontSize) {
                    "0" -> ContextThemeWrapper(context, R.style.DarkTheme_SmallFont)
                    "1" -> ContextThemeWrapper(context, R.style.DarkTheme_MediumFont)
                    else -> ContextThemeWrapper(context, R.style.DarkTheme_LargeFont)
                }
            }
            else {
                when (fontSize) {
                    "0" -> ContextThemeWrapper(context, R.style.LightTheme_SmallFont)
                    "1" -> ContextThemeWrapper(context, R.style.LightTheme_MediumFont)
                    else -> ContextThemeWrapper(context, R.style.LightTheme_LargeFont)
                }
            }

            return baseInflater.cloneInContext(contextThemeWrapper)
        }
    }
}