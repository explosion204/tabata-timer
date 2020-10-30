package com.explosion204.tabatatimer.ui.helpers

import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.R

class ToolbarFontSizeHelper {
    companion object {
        fun setToolbarFontSize(toolbar: Toolbar) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(toolbar.context)

            when (preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")) {
                "0" -> toolbar.setTitleTextAppearance(toolbar.context, R.style.TitleTextAppearanceSmall)
                "1" -> toolbar.setTitleTextAppearance(toolbar.context, R.style.TitleTextAppearanceMedium)
                else -> toolbar.setTitleTextAppearance(toolbar.context, R.style.TitleTextAppearanceLarge)
            }
        }
    }
}