package com.explosion204.tabatatimer.ui.fragments

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMER
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.adapters.UpcomingTimersListAdapter
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.viewmodels.TimerViewModel

class UpcomingTimersListFragment : Fragment() {
    private val viewModel: TimerViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: UpcomingTimersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        return localInflater.inflate(R.layout.fragment_upcoming_timers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listAdapter = UpcomingTimersListAdapter(requireContext(), viewModel.allTimers)
        recyclerView.adapter = listAdapter

        setListeners()
        setObservables()
    }

    private fun setListeners() {
        listAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(item: Any) {
                val timerPos = item as Int
                viewModel.sendActionToActivity(ACTION_SELECT_TIMER, timerPos)
            }
        })
    }

    private fun setObservables() {
        viewModel.currentTimer.observe(viewLifecycleOwner, Observer {
            listAdapter.setSelection(viewModel.currentTimerPos.value!!)
        })
    }
}