package com.explosion204.tabatatimer.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.Constants.ACTION_ADD_NEW_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMERS_MODE
import com.explosion204.tabatatimer.Constants.EXTRA_ALL_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_SEQUENCE
import com.explosion204.tabatatimer.Constants.TAG_SEQUENCE_DETAIL_FRAGMENT
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.SequenceDetailViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.github.clans.fab.FloatingActionButton
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SequenceDetailActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sequenceDetailViewModel : SequenceDetailViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferences.getBoolean(Constants.NIGHT_MODE_PREFERENCE, false)

        if (nightModeEnabled) {
            setTheme(R.style.DarkTheme)
        }
        else {
            setTheme(R.style.LightTheme)
        }

        setContentView(R.layout.activity_sequence_detail)

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab_add_associated_timer)

        initViewModel()
        setCallbacks()
    }

    private fun initViewModel() {
        title = if (intent.hasExtra(EXTRA_SEQUENCE)) {
            val sequence = intent.getParcelableExtra<SequenceWithTimers>(EXTRA_SEQUENCE)!!

            sequenceDetailViewModel.id = sequence.sequence.seqId
            sequenceDetailViewModel.title = sequence.sequence.title
            sequenceDetailViewModel.desc = sequence.sequence.description

            getString(R.string.edit_sequence)
        }
        else {
            getString(R.string.new_sequence)
        }

        sequenceDetailViewModel.setTimers(intent.getSerializableExtra(
            EXTRA_ASSOCIATED_TIMERS) as ArrayList<Timer>,
            intent.getSerializableExtra(EXTRA_ALL_TIMERS) as ArrayList<Timer>)
    }

    private fun setCallbacks() {
        fab.setOnClickListener {
            sequenceDetailViewModel.sendActionToFragment(TAG_SEQUENCE_DETAIL_FRAGMENT, ACTION_ADD_NEW_ASSOCIATED_TIMERS, null)
        }

        sequenceDetailViewModel.setActivityCallback(object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    ACTION_SELECT_TIMERS_MODE -> {
                        val flag = arg as Boolean

                        if (flag) {
                            fab.hide(true)
                        }
                        else {
                            fab.show(true)
                        }
                    }
                }
            }
        })
    }
}