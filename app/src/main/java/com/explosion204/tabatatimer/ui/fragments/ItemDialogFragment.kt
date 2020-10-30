package com.explosion204.tabatatimer.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.ui.interfaces.OnItemDialogButtonClickListener

class ItemDialogFragment : DialogFragment() {
    private var itemDialogButtonClickListener: OnItemDialogButtonClickListener? = null

    fun setOnItemDialogButtonClickListener(listenerItem: OnItemDialogButtonClickListener) {
        itemDialogButtonClickListener = listenerItem
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val nightModeEnabled = preferences.getBoolean(Constants.NIGHT_MODE_PREFERENCE, false)
        val fontSize = preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")

        val contextThemeWrapper = if (nightModeEnabled) {
            when (fontSize) {
                "0" -> ContextThemeWrapper(requireContext(), R.style.DarkTheme_SmallFont)
                "1" -> ContextThemeWrapper(requireContext(), R.style.DarkTheme_MediumFont)
                else -> ContextThemeWrapper(requireContext(), R.style.DarkTheme_LargeFont)
            }
        }
        else {
            when (fontSize) {
                "0" -> ContextThemeWrapper(requireContext(), R.style.LightTheme_SmallFont)
                "1" -> ContextThemeWrapper(requireContext(), R.style.LightTheme_MediumFont)
                else -> ContextThemeWrapper(requireContext(), R.style.LightTheme_LargeFont)
            }
        }

        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_item_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ImageButton>(R.id.start_button).setOnClickListener {
            itemDialogButtonClickListener?.onStartButtonClick()
        }
        view.findViewById<ImageButton>(R.id.edit_button).setOnClickListener {
            itemDialogButtonClickListener?.onEditButtonClick()
        }
        view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
            itemDialogButtonClickListener?.onDeleteButtonClick()
        }

        view.findViewById<TextView>(R.id.item_title).text = arguments?.getString(EXTRA_TITLE)
        view.findViewById<TextView>(R.id.item_desc).text = arguments?.getString(EXTRA_DESCRIPTION)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }
}