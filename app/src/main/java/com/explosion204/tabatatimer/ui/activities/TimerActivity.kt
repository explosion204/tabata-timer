package com.explosion204.tabatatimer.ui.activities

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.explosion204.tabatatimer.Constants.ACTION_NEXT_TIMER
import com.explosion204.tabatatimer.Constants.ACTION_PREV_TIMER
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_PHASE
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMER
import com.explosion204.tabatatimer.Constants.ACTION_SET_TIMER_STATE
import com.explosion204.tabatatimer.Constants.ACTION_TIMER_STATE_CHANGED
import com.explosion204.tabatatimer.Constants.EXTRA_SEQUENCE
import com.explosion204.tabatatimer.Constants.EXTRA_TIMER
import com.explosion204.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import com.explosion204.tabatatimer.Constants.TAG_TIMER_FRAGMENT
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.Constants.TIMER_BROADCAST_ACTION
import com.explosion204.tabatatimer.Constants.TIMER_STARTED
import com.explosion204.tabatatimer.Constants.TIMER_ACTION_TYPE
import com.explosion204.tabatatimer.Constants.TIMER_STOPPED
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.services.TimerPhase
import com.explosion204.tabatatimer.services.TimerService
import com.explosion204.tabatatimer.ui.adapters.TimerPagerAdapter
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.TimerViewModel
import me.relex.circleindicator.CircleIndicator3

class TimerActivity : AppCompatActivity() {

    private val viewModel : TimerViewModel by viewModels()

    private lateinit var rootLayout: LinearLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var timerTitleTextView: TextView
    private lateinit var phaseTitleTextView: TextView

    private var broadcastReceiver: BroadcastReceiver? = null
    private var timerServiceConnection: ServiceConnection? = null
    private var timerService: TimerService? = null
    private var nightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)

        if (nightMode) {
            setTheme(R.style.DarkTheme)
        }
        else {
            setTheme(R.style.LightTheme)
        }

        setContentView(R.layout.activity_timer)

        rootLayout = findViewById(R.id.root_layout)
        toolbar = findViewById(R.id.app_bar)
        setSupportActionBar(toolbar)
        timerTitleTextView = findViewById(R.id.current_timer_name)
        phaseTitleTextView = findViewById(R.id.current_phase)

        if (intent.hasExtra(EXTRA_SEQUENCE)) {
            val sequence = intent.getParcelableExtra<SequenceWithTimers>(EXTRA_SEQUENCE)!!
            viewModel.allTimers = ArrayList(sequence.timers)

            val pagerAdapter = TimerPagerAdapter(supportFragmentManager, lifecycle, isSingle = false)
            viewPager = findViewById(R.id.view_pager)
            viewPager.adapter = pagerAdapter
            viewPager.currentItem = 1

            val viewPagerIndicator = findViewById<CircleIndicator3>(R.id.view_pager_indicator)
            viewPagerIndicator.setViewPager(viewPager)

            startTimerService(sequence)
        }
        else {
            val timer = intent.getParcelableExtra<Timer>(EXTRA_TIMER)!!

            val pagerAdapter = TimerPagerAdapter(supportFragmentManager, lifecycle, isSingle = true)
            viewPager = findViewById(R.id.view_pager)
            viewPager.adapter = pagerAdapter

            val viewPagerIndicator = findViewById<CircleIndicator3>(R.id.view_pager_indicator)
            viewPagerIndicator.isVisible = false

            startTimerService(timer)
        }

        if (nightMode) {
            rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkColor))
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.deepDarkColor))
        }

        setObservables()
        setActivityCallback()
    }

    private fun setObservables() {
        viewModel.currentTimer.observe(this, Observer {
            if (!nightMode) {
                rootLayout.setBackgroundColor(it.color)
                toolbar.setBackgroundColor(it.color)
            }

            timerTitleTextView.text = it.title
        })

        viewModel.currentPhase.observe(this, Observer {
            phaseTitleTextView.text =
                when (it) {
                    TimerPhase.PREPARATION ->
                        getString(R.string.preparation)
                    TimerPhase.WORKOUT ->
                        getString(R.string.workout)
                    TimerPhase.REST ->
                        getString(R.string.rest)
                    TimerPhase.FINISHED ->
                        getString(R.string.finished)
            }
        })
    }

    private fun startTimerService(item: Any) { // in fact timer or sequence
        setBroadcastReceiver()

        val intent = Intent(this, TimerService::class.java)
        startService(intent)

        timerServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {}

            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                timerService = (binder as TimerService.TimerServiceBinder).getService()

                if (item is Timer) {
                    timerService!!.setTimer(item)
                }
                else {
                    timerService!!.setSequence(item as SequenceWithTimers)
                }


                bindServiceToViewModel(timerService!!)
            }
        }

        bindService(intent, timerServiceConnection!!, 0)
    }

    private fun bindServiceToViewModel(service: TimerService) {
        service.timersCount.observe(this, Observer {
            viewModel.timersCount = it
        })

        service.currentTimerPos.observe(this, Observer {
            viewModel.currentTimerPos.value = it
        })

        service.currentTimer.observe(this, Observer {
            viewModel.currentTimer.value = it
        })

        service.currentPhase.observe(this, Observer {
            viewModel.currentPhase.value = it
        })

        service.preparationRemaining.observe(this, Observer {
            viewModel.preparationRemaining.value = it
        })

        service.workoutRemaining.observe(this, Observer {
            viewModel.workoutRemaining.value = it
        })

        service.restRemaining.observe(this, Observer {
            viewModel.restRemaining.value = it
        })

        service.cyclesRemaining.observe(this, Observer {
            viewModel.cyclesRemaining.value = it
        })
    }

    private fun setBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra(TIMER_ACTION_TYPE)) {
                    TIMER_STARTED -> {
                        viewModel.sendActionToFragment(TAG_TIMER_FRAGMENT, ACTION_TIMER_STATE_CHANGED, true)
                    }
                    TIMER_STOPPED -> {
                        viewModel.sendActionToFragment(TAG_TIMER_FRAGMENT, ACTION_TIMER_STATE_CHANGED, false)
                    }
                }
            }
        }

        val filter = IntentFilter(TIMER_BROADCAST_ACTION)
        registerReceiver(broadcastReceiver, filter)
    }

    private fun setActivityCallback() {
        viewModel.setActivityCallback(object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                if (timerService != null) {
                    when (action)
                    {
                        ACTION_SET_TIMER_STATE -> {
                            val state = arg as Boolean

                            if (state) {
                                timerService!!.start()
                            }
                            else {
                                timerService!!.stop()
                            }
                        }
                        ACTION_PREV_TIMER -> {
                            timerService!!.prevTimer(false)
                        }
                        ACTION_NEXT_TIMER -> {
                            timerService!!.nextTimer(false)
                        }
                        ACTION_SELECT_PHASE -> {
                            val phase = arg as TimerPhase
                            timerService!!.selectPhase(phase, false)
                        }
                        ACTION_SELECT_TIMER -> {
                            val timerPos = arg as Int
                            timerService!!.selectTimer(timerPos)
                        }
                    }
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver)
        }

        if (timerServiceConnection != null) {
            unbindService(timerServiceConnection!!)
        }

        stopService(Intent(this, TimerService::class.java))
    }
}