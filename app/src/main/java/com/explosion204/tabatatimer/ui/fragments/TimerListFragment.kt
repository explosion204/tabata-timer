package com.explosion204.tabatatimer.ui.fragments

import android.os.Bundle
import android.os.Handler
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
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.security.auth.callback.Callback

class TimerListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : TimerListViewModel by viewModels {
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

        viewModel.getAll().observe(viewLifecycleOwner, Observer {
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
                val timer = adapter.getItemAt(viewHolder.adapterPosition)
                viewModel.delete(timer)
                Snackbar.make(view, R.string.undo_delete_label, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) {
                            if (viewModel.recentlyDeletedTimer != null) {
                                viewModel.insert(viewModel.recentlyDeletedTimer!!)
                            }
                        }
                        .addCallback(object : Snackbar.Callback() {
                            override fun onShown(sb: Snackbar?) {
                                viewModel.recentlyDeletedTimer = timer
                                super.onShown(sb)
                            }
                        })
                        .show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    fun populate() {
        viewModel.insert(Timer(1, "Timer 1", "Desedddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddc", 0, 0, 0, 0))
        viewModel.insert(Timer(2, "Timer 2", "Desc", 0, 0, 0, 0))
        viewModel.insert(Timer(3, "Timer 3", "Desc", 0, 0, 0, 0))
        viewModel.insert(Timer(4, "Timer 4", "Desc", 0, 0, 0, 0))
        viewModel.insert(Timer(5, "Timer 5", "Desc", 0, 0, 0, 0))


    }
}