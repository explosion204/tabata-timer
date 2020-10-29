package com.explosion204.tabatatimer.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants.ACTION_NEXT_TIMER
import com.explosion204.tabatatimer.Constants.ACTION_PREV_TIMER
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_PHASE
import com.explosion204.tabatatimer.Constants.ACTION_SET_TIMER_STATE
import com.explosion204.tabatatimer.Constants.ACTION_TIMER_STATE_CHANGED
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.Constants.TAG_TIMER_FRAGMENT
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.services.TimerPhase
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.TimerViewModel

class TimerFragment : Fragment() {
    private val viewModel: TimerViewModel by activityViewModels()

    private lateinit var startButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton

    private lateinit var prepTextView: TextView
    private lateinit var workoutTextView: TextView
    private lateinit var restTextView: TextView
    private lateinit var cyclesTextView: TextView

    private lateinit var prepLayout: LinearLayout
    private lateinit var workoutLayout: LinearLayout
    private lateinit var restLayout: LinearLayout

    private var selectionColor: Int? = null
    private var transparentColor: Int? = null
    private var nightMode = false
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val nightModeEnabled = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)

        val contextThemeWrapper = if (nightModeEnabled) {
            ContextThemeWrapper(requireActivity(), R.style.DarkTheme)
        }
        else {
            ContextThemeWrapper(requireActivity(), R.style.LightTheme)
        }

        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startButton = view.findViewById(R.id.start_button)
        pauseButton = view.findViewById(R.id.pause_button)
        previousButton = view.findViewById(R.id.previous_button)
        nextButton = view.findViewById(R.id.next_button)
        prepTextView = view.findViewById(R.id.prep)
        workoutTextView = view.findViewById(R.id.workout)
        restTextView = view.findViewById(R.id.rest)
        cyclesTextView = view.findViewById(R.id.cycles)
        prepLayout = view.findViewById(R.id.prep_layout)
        workoutLayout = view.findViewById(R.id.workout_layout)
        restLayout = view.findViewById(R.id.rest_layout)

        selectionColor = ContextCompat.getColor(requireContext(), R.color.selectionColor)
        transparentColor  = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        setObservables()
        setOnClickListeners()

        viewModel.setFragmentCallback(TAG_TIMER_FRAGMENT, object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    ACTION_TIMER_STATE_CHANGED -> {
                        val state = arg as Boolean

                        if (state) {
                            startButton.isEnabled = false
                            pauseButton.isEnabled = true
                            startButton.alpha = 0.5f
                            pauseButton.alpha = 1.0f
                        }
                        else {
                            startButton.isEnabled = true
                            pauseButton.isEnabled = false
                            startButton.alpha = 1.0f
                            pauseButton.alpha = 0.5f
                        }
                    }
                }
            }
        })

        nightMode = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)
        if (nightMode) {
            val darkColor = ContextCompat.getColor(requireContext(), R.color.darkColor)
            startButton.setBackgroundColor(darkColor)
            pauseButton.setBackgroundColor(darkColor)
            previousButton.setBackgroundColor(darkColor)
            nextButton.setBackgroundColor(darkColor)
        }
    }

    private fun setOnClickListeners() {
        startButton.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_SET_TIMER_STATE, true)
        }

        pauseButton.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_SET_TIMER_STATE, false)
        }

        previousButton.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_PREV_TIMER, null)
        }

        nextButton.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_NEXT_TIMER, null)
        }

        prepLayout.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_SELECT_PHASE, TimerPhase.PREPARATION)
        }

        workoutLayout.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_SELECT_PHASE, TimerPhase.WORKOUT)
        }

        restLayout.setOnClickListener {
            viewModel.sendActionToActivity(ACTION_SELECT_PHASE, TimerPhase.REST)
        }
    }

    private fun setObservables() {
        viewModel.currentTimer.observe(viewLifecycleOwner, Observer {
            if (!nightMode) {
                startButton.setBackgroundColor(it.color)
                pauseButton.setBackgroundColor(it.color)
                previousButton.setBackgroundColor(it.color)
                nextButton.setBackgroundColor(it.color)
            }

            prepTextView.text = "${it.preparations / 60}m ${it.preparations % 60}s"
            workoutTextView.text = "${it.workout / 60}m ${it.workout % 60}s"
            restTextView.text = "${it.rest / 60}m ${it.rest % 60}s"
            cyclesTextView.text = "${it.cycles}"

            prepLayout.isClickable = it.preparations != 0
            restLayout.isClickable = it.rest != 0
        })

        viewModel.preparationRemaining.observe(viewLifecycleOwner, Observer {
            if (it >= 0) {
                prepTextView.text = "${it / 60}m ${it % 60}s"
            }
        })

        viewModel.workoutRemaining.observe(viewLifecycleOwner, Observer {
            if (it >= 0) {
                workoutTextView.text = "${it / 60}m ${it % 60}s"
            }
        })

        viewModel.restRemaining.observe(viewLifecycleOwner, Observer {
            if (it >= 0) {
                restTextView.text = "${it / 60}m ${it % 60}s"
            }
        })

        viewModel.cyclesRemaining.observe(viewLifecycleOwner, Observer {
            cyclesTextView.text = it.toString()
        })

        viewModel.currentPhase.observe(viewLifecycleOwner, Observer {
            if (selectionColor != null && transparentColor != null) {
                when (it) {
                    TimerPhase.PREPARATION -> {
                        prepLayout.setBackgroundColor(selectionColor!!)
                        workoutLayout.setBackgroundColor(transparentColor!!)
                        restLayout.setBackgroundColor(transparentColor!!)

                    }
                    TimerPhase.WORKOUT -> {
                        prepLayout.setBackgroundColor(transparentColor!!)
                        workoutLayout.setBackgroundColor(selectionColor!!)
                        restLayout.setBackgroundColor(transparentColor!!)
                    }
                    TimerPhase.REST -> {
                        prepLayout.setBackgroundColor(transparentColor!!)
                        workoutLayout.setBackgroundColor(transparentColor!!)
                        restLayout.setBackgroundColor(selectionColor!!)
                    }
                    TimerPhase.FINISHED -> {
                        prepLayout.setBackgroundColor(transparentColor!!)
                        workoutLayout.setBackgroundColor(transparentColor!!)
                        restLayout.setBackgroundColor(transparentColor!!)
                        startButton.isEnabled = false
                        startButton.alpha = 0.5f
                    }
                }
            }
        })

        viewModel.currentTimerPos.observe(viewLifecycleOwner, Observer {
            if (it == 0 && it == viewModel.timersCount - 1) {
                previousButton.isEnabled = false
                nextButton.isEnabled = false
                previousButton.alpha = 0.5f
                nextButton.alpha = 0.5f
            }
            else if (it == 0) {
                previousButton.isEnabled = false
                nextButton.isEnabled = true
                previousButton.alpha = 0.5f
                nextButton.alpha = 1.0f
            }
            else if (it == viewModel.timersCount - 1) {
                nextButton.isEnabled = false
                previousButton.isEnabled = true
                nextButton.alpha = 0.5f
                previousButton.alpha = 1.0f
            }
            else {
                nextButton.isEnabled = true
                previousButton.isEnabled = true
                nextButton.alpha = 1.0f
                previousButton.alpha = 1.0f
            }
        })
    }
}