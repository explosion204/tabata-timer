package com.explosion204.tabatatimer.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.Constants.ACTION_ADD_NEW_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMERS_MODE
import com.explosion204.tabatatimer.Constants.EXTRA_ALL_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.Constants.EXTRA_ID
import com.explosion204.tabatatimer.Constants.EXTRA_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.Constants.TAG_SEQUENCE_DETAIL_FRAGMENT
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
        setContentView(R.layout.activity_sequence_detail)

        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab_add_associated_timer)

        initViewModel()
        setCallbacks()
    }

    private fun initViewModel() {
        title = if (intent.hasExtra(EXTRA_ID)) {
            sequenceDetailViewModel.id = intent.getIntExtra(EXTRA_ID, 0)
            sequenceDetailViewModel.title = intent.getStringExtra(EXTRA_TITLE)!!
            sequenceDetailViewModel.desc = intent.getStringExtra(EXTRA_DESCRIPTION)!!

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