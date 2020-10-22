package com.explosion204.tabatatimer.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import com.explosion204.tabatatimer.ui.adapters.ItemListAdapter
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import java.util.ArrayList
import javax.inject.Inject

class TimerListFragment : DaggerFragment() {
    companion object {
        const val CALLBACK_ACTION_CONTEXTUAL_MENU =
            "com.explosion204.tabatatimer.ui.fragments.CONTEXTUAL_MENU_ACTION"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : TimerListViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var toolbar: ActionBar
    private lateinit var listAdapter: ItemListAdapter<Timer>
    private lateinit var recyclerView: RecyclerView
    private var selectedItems = ArrayList<Timer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_timer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = (activity as DaggerAppCompatActivity).supportActionBar!!
        toolbar.setHomeAsUpIndicator(R.drawable.ic_close_24)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listAdapter = ItemListAdapter()
        recyclerView.adapter = listAdapter

        setObservables()
        setAdapterListeners()
    }

    private fun setObservables() {
        viewModel.getAll().observe(viewLifecycleOwner, Observer {
            listAdapter.submitList(it)
        })
    }

    private fun setAdapterListeners() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (!listAdapter.isContextualMenuEnabled) {
                    makeMovementFlags(0, ItemTouchHelper.LEFT)
                } else {
                    makeMovementFlags(0, 0)
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val timer = listAdapter.getItemAt(viewHolder.adapterPosition)
                viewModel.delete(timer)
                Snackbar.make(view!!, R.string.item_removed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo_action) {
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

        listAdapter.setOnItemClickListener(object : ItemListAdapter.OnItemClickListener {
            override fun onItemClick(item: Any) {
                val timer = item as Timer
                val intent = Intent(context, TimerDetailActivity::class.java)
                intent.putExtra(TimerDetailActivity.EXTRA_ID, timer.id)
                intent.putExtra(TimerDetailActivity.EXTRA_TITLE, timer.title)
                intent.putExtra(TimerDetailActivity.EXTRA_DESCRIPTION, timer.description)
                intent.putExtra(TimerDetailActivity.EXTRA_PREP, timer.preparations)
                intent.putExtra(TimerDetailActivity.EXTRA_WORKOUT, timer.workout)
                intent.putExtra(TimerDetailActivity.EXTRA_REST, timer.rest)
                intent.putExtra(TimerDetailActivity.EXTRA_CYCLES, timer.cycles)

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    if (activity != null) {
                        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }, 100)
            }

        })

        listAdapter.setOnItemLongClickListener(object : ItemListAdapter.OnItemLongClickListener {
            override fun onItemLongClick(item: Any) {
                if (!listAdapter.isContextualMenuEnabled) {
                    listAdapter.isContextualMenuEnabled = true
                    (activity as DaggerAppCompatActivity).invalidateOptionsMenu()
                    toolbar.title = "0 ${getString(R.string.items_selected)}"
                    toolbar.setDisplayHomeAsUpEnabled(true)

                    viewModel.sendActionToActivity(CALLBACK_ACTION_CONTEXTUAL_MENU, true)
                }
            }
        })

        listAdapter.setOnItemCheckedChangeListener(object : ItemListAdapter.OnItemCheckedChangeListener {
            override fun onItemChecked(item: Any, checkBox: CheckBox) {
                val timer = item as Timer

                if (checkBox.isChecked) {
                    selectedItems.add(timer)
                }
                else {
                    selectedItems.remove(timer)
                }

                if (listAdapter.isContextualMenuEnabled) {
                    toolbar.title = "${selectedItems.size} ${getString(R.string.items_selected)}"
                }
            }
        })


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()

        if (listAdapter.isContextualMenuEnabled) {
            (context as DaggerAppCompatActivity).menuInflater.inflate(R.menu.list_contextual_menu, menu)
        }
        else {
            toolbar.title = getString(R.string.app_name)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_item -> {
                if (selectedItems.size != 0) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(getString(R.string.delete_selected_items))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            viewModel.delete(selectedItems)
                            quitContextualActionMode()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            quitContextualActionMode()
                        }
                        .setCancelable(true)
                        .create()
                        .show()
                }
                else {
                    quitContextualActionMode()
                }
            }
            android.R.id.home -> {
                quitContextualActionMode()
            }
        }

        return true
    }

    private fun quitContextualActionMode() {
        listAdapter.isContextualMenuEnabled = false
        (activity as DaggerAppCompatActivity).invalidateOptionsMenu()
        viewModel.sendActionToActivity(CALLBACK_ACTION_CONTEXTUAL_MENU, false)
        toolbar.setDisplayHomeAsUpEnabled(false)
        toolbar.title = getString(R.string.app_name)
    }

    override fun onPause() {
        super.onPause()

        quitContextualActionMode()
    }


}