package com.explosion204.tabatatimer.ui.list_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.MainActivity
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.adapters.ListItemAdapter
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TimersListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val timerViewModel : TimerListViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_timers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val adapter = ListItemAdapter()
        recyclerView.adapter = adapter

        timerViewModel.getAll().observe(viewLifecycleOwner, Observer {
            adapter.items = it
        })
    }
}