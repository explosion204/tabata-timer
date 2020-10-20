package com.explosion204.tabatatimer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.ui.adapters.ItemListAdapter
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TimerListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val timerListViewModel : TimerListViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //populate()
        return inflater.inflate(R.layout.fragment_timer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val adapter = ItemListAdapter<Timer>()
        recyclerView.adapter = adapter

        timerListViewModel.getAll().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                timerListViewModel.delete(adapter.getItemAt(viewHolder.adapterPosition))
            }
        }).attachToRecyclerView(recyclerView)
    }

    fun populate() {
        timerListViewModel.insert(Timer(1, "Timer 1", "Desedddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddc", 0, 0, 0, 0))
        timerListViewModel.insert(Timer(2, "Timer 2", "Desc", 0, 0, 0, 0))
        timerListViewModel.insert(Timer(3, "Timer 3", "Desc", 0, 0, 0, 0))
        timerListViewModel.insert(Timer(4, "Timer 4", "Desc", 0, 0, 0, 0))
        timerListViewModel.insert(Timer(5, "Timer 5", "Desc", 0, 0, 0, 0))


    }
}