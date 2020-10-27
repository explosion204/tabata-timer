package com.explosion204.tabatatimer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.adapters.UpcomingTimersListAdapter
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.viewmodels.TimerViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class UpcomingTimersListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TimerViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: UpcomingTimersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_timers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setObservables()
    }

    private fun setObservables() {
        viewModel.allUpcomingTimers.observe(viewLifecycleOwner, Observer {
            listAdapter = UpcomingTimersListAdapter(requireContext(), it)
            recyclerView.adapter = listAdapter

            listAdapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(item: Any) {
                    val timerPos = item as Int
                    viewModel.selectTimer(timerPos)
                }

            })

        })

        viewModel.currentTimer.observe(viewLifecycleOwner, Observer {
            listAdapter.setSelection(viewModel.currentTimerPos.value!!)
        })
    }
}