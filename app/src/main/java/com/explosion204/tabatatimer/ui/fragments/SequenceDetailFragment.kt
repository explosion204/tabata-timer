package com.explosion204.tabatatimer.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.Constants.ACTION_ADD_NEW_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMERS_MODE
import com.explosion204.tabatatimer.Constants.TAG_SEQUENCE_DETAIL_FRAGMENT
import com.explosion204.tabatatimer.ui.adapters.TimerListAdapter
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.SequenceDetailViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SequenceDetailFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : SequenceDetailViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var toolbar: ActionBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: TimerListAdapter
    private lateinit var titleEditText: EditText
    private lateinit var descEditText: EditText

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val nightModeEnabled = preferences.getBoolean(Constants.NIGHT_MODE_PREFERENCE, false)

        val contextThemeWrapper = if (nightModeEnabled) {
            ContextThemeWrapper(requireActivity(), R.style.DarkTheme)
        }
        else {
            ContextThemeWrapper(requireActivity(), R.style.LightTheme)
        }

        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_sequence_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = (activity as DaggerAppCompatActivity).supportActionBar!!
        setHasOptionsMenu(true)
        toolbar.title = if (view != null && id == 0) {
            getString(R.string.new_sequence)
        }
        else {
            getString(R.string.edit_sequence)
        }

        titleEditText = view.findViewById(R.id.sequence_title)
        descEditText = view.findViewById(R.id.sequence_desc)
        titleEditText.setText(viewModel.title)
        descEditText.setText(viewModel.desc)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listAdapter = TimerListAdapter()
        recyclerView.adapter = listAdapter

        setObservables()
        setAdapterListeners()
        setOnEditTextChangedListeners()

        viewModel.setFragmentCallback(TAG_SEQUENCE_DETAIL_FRAGMENT, object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    ACTION_ADD_NEW_ASSOCIATED_TIMERS -> {
                        Navigation.findNavController(view).navigate(R.id.action_select_timers)
                        viewModel.sendActionToActivity(ACTION_SELECT_TIMERS_MODE, true)
                    }
                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        })
    }

    private fun setObservables() {
        if (viewModel.associatedTimers != null) {
            viewModel.associatedTimers!!.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
            })
        }
    }

    private fun setAdapterListeners() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (!listAdapter.isContextualMenuEnabled) {
                    makeMovementFlags(0, ItemTouchHelper.LEFT)
                } else {
                    makeMovementFlags(0, 0)
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val timer = listAdapter.getItemAt(position)
                viewModel.delete(timer)
                listAdapter.notifyItemRemoved(position)
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun setOnEditTextChangedListeners() {
        titleEditText.addTextChangedListener(EditTextWatcher(titleEditText))
        descEditText.addTextChangedListener(EditTextWatcher(descEditText))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        toolbar.setHomeAsUpIndicator(R.drawable.ic_close_24)
        toolbar.setDisplayHomeAsUpEnabled(true)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                if (viewModel.saveSequence()) {
                    requireActivity().finish()
                }

                return true
            }
            android.R.id.home -> requireActivity().finish()
        }

        return super.onOptionsItemSelected(item)
    }

    inner class EditTextWatcher(private val editText: EditText) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newVal = editText.text.toString()

            if (newVal.isNotEmpty()) {
                when (editText.id) {
                    R.id.sequence_title -> viewModel.title = newVal
                    R.id.sequence_desc -> viewModel.desc = newVal
                }
            }
        }

    }
}