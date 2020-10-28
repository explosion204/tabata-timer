package com.explosion204.tabatatimer.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.explosion204.tabatatimer.ui.fragments.TimerFragment
import com.explosion204.tabatatimer.ui.fragments.UpcomingTimersListFragment

class TimerPagerAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle, private val isSingle: Boolean) :
    FragmentStateAdapter(fragmentManager, lifeCycle)  {

    override fun createFragment(position: Int): Fragment {
        return if (!isSingle) {
            when (position) {
                0 -> UpcomingTimersListFragment()
                1 -> TimerFragment()
                else -> TimerFragment()
            }
        }
        else {
            TimerFragment()
        }
    }

    override fun getItemCount() = if (!isSingle) 2 else 1
}