package com.explosion204.tabatatimer.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.ui.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.ui.interfaces.OnDialogButtonClickListener

class ItemDialogFragment : DialogFragment() {
    private var dialogButtonClickListener: OnDialogButtonClickListener? = null

    fun setOnDialogButtonClickListener(listener: OnDialogButtonClickListener) {
        dialogButtonClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sequence_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ImageButton>(R.id.start_button).setOnClickListener {
                dialogButtonClickListener?.onStartButtonClick()
        }
        view.findViewById<ImageButton>(R.id.edit_button).setOnClickListener {
            dialogButtonClickListener?.onEditButtonClick()
        }
        view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
            dialogButtonClickListener?.onDeleteButtonClick()
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