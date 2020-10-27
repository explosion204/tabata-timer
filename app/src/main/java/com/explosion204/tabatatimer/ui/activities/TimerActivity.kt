package com.explosion204.tabatatimer.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.Constants.EXTRA_SEQUENCE_ID
import com.explosion204.tabatatimer.ui.adapters.TimerPagerAdapter
import com.explosion204.tabatatimer.viewmodels.TimerViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import me.relex.circleindicator.CircleIndicator3
import javax.inject.Inject

class TimerActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : TimerViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var rootLayout: LinearLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var timerTitleTextView: TextView
    private lateinit var phaseTitleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
        else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        rootLayout = findViewById(R.id.root_layout)
        toolbar = findViewById(R.id.app_bar)
        setSupportActionBar(toolbar)
        timerTitleTextView = findViewById(R.id.current_timer_name)
        phaseTitleTextView = findViewById(R.id.current_phase)

        val pagerAdapter = TimerPagerAdapter(supportFragmentManager, lifecycle)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1

        val viewPagerIndicator = findViewById<CircleIndicator3>(R.id.view_pager_indicator)
        viewPagerIndicator.setViewPager(viewPager)

        initViewModel()
        setObservables()
    }

    private fun initViewModel() {
        if (intent.hasExtra(EXTRA_SEQUENCE_ID)) {
            viewModel.initViewModel(intent.getIntExtra(EXTRA_SEQUENCE_ID, 0))
        }
    }

    private fun setObservables() {
        viewModel.currentTimer.observe(this, Observer {
            rootLayout.setBackgroundColor(it.color)
            toolbar.setBackgroundColor(it.color)
            timerTitleTextView.text = it.title
        })

        viewModel.currentPhase.observe(this, Observer {
            phaseTitleTextView.text =
                when (it) {
                    TimerViewModel.TimerPhase.PREPARATION ->
                        getString(R.string.preparation)
                    TimerViewModel.TimerPhase.WORKOUT ->
                        getString(R.string.workout)
                    TimerViewModel.TimerPhase.REST ->
                        getString(R.string.rest)
            }
        })
    }
}