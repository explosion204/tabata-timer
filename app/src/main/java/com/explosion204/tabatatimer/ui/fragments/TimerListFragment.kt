package com.explosion204.tabatatimer.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.MainActivity
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.Constants.ACTION_CONTEXTUAL_MENU
import com.explosion204.tabatatimer.Constants.EXTRA_ALL_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.Constants.EXTRA_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_TIMER
import com.explosion204.tabatatimer.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.Constants.TAG_TIMER_LIST_FRAGMENT
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.ui.activities.SequenceDetailActivity
import com.explosion204.tabatatimer.ui.activities.TimerActivity
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import com.explosion204.tabatatimer.ui.adapters.TimerListAdapter
import com.explosion204.tabatatimer.ui.interfaces.OnDialogButtonClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemCheckedChangeListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemLongClickListener
import com.explosion204.tabatatimer.viewmodels.BaseViewModel
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TimerListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : TimerListViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var toolbar: ActionBar
    private lateinit var listAdapter: TimerListAdapter
    private lateinit var recyclerView: RecyclerView

    private var contextualActionMode = ContextualActionMode.NONE
    enum class ContextualActionMode {
        SELECTION, NEW_SEQUENCE, NONE
    }

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

        listAdapter = TimerListAdapter()
        recyclerView.adapter = listAdapter

        setObservables()
        setAdapterListeners()

        viewModel.setFragmentCallback(TAG_TIMER_LIST_FRAGMENT, object : BaseViewModel.ActionCallback {
            override fun callback(action: String, arg: Any?) {
                when (action) {
                    MainActivity.CALLBACK_ACTION_NEW_SEQUENCE -> {
                        enterContextualActionMode(ContextualActionMode.NEW_SEQUENCE)
                    }
                }
            }
        })
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
                            viewModel.insertTimer(viewModel.recentlyDeletedTimer!!)
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

        listAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(item: Any) {
                val item = item as Timer

                val args = Bundle()
                args.putString(EXTRA_TITLE, item.title)
                args.putString(EXTRA_DESCRIPTION, item.description)

                val dialogFragment = ItemDialogFragment()
                dialogFragment.arguments = args
                dialogFragment.setOnDialogButtonClickListener(object : OnDialogButtonClickListener {
                    override fun onStartButtonClick() {
                        val intent = Intent(context, TimerActivity::class.java)
                        intent.putExtra(EXTRA_TIMER, item)
                        startActivity(intent)
                        dialogFragment.dismiss()
                    }

                    override fun onEditButtonClick() {
                        val intent = Intent(context, TimerDetailActivity::class.java)
                        intent.putExtra(EXTRA_TIMER, item)

                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(intent)
                            if (activity != null) {
                                activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            }
                        }, 100)

                        dialogFragment.dismiss()
                    }

                    override fun onDeleteButtonClick() {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage(getString(R.string.delete_this_item))
                            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                                viewModel.delete(item)
                                dialogFragment.dismiss()
                            }
                            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                dialogFragment.dismiss()
                            }
                            .setCancelable(true)
                            .create()
                            .show()
                    }

                })
                dialogFragment.show(requireActivity().supportFragmentManager, "DIALOG_FRAGMENT")
            }

        })

        listAdapter.setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemLongClick(item: Any) {
                if (!listAdapter.isContextualMenuEnabled) {
                    enterContextualActionMode(ContextualActionMode.SELECTION)
                }
            }
        })

        listAdapter.setOnItemCheckedChangeListener(object : OnItemCheckedChangeListener {
            override fun onItemChecked(item: Any, flag: Boolean) {
                val timer = item as Timer

                if (flag) {
                    viewModel.selectedItems.add(timer)
                }
                else {
                    viewModel.selectedItems.remove(timer)
                }

                if (listAdapter.isContextualMenuEnabled) {
                    toolbar.title = "${viewModel.selectedItems.size} ${getString(R.string.items_selected)}"
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()

        if (listAdapter.isContextualMenuEnabled) {
            (context as DaggerAppCompatActivity).menuInflater.inflate(R.menu.list_contextual_menu, menu)

            when (contextualActionMode) {
                ContextualActionMode.SELECTION -> {
                    menu.getItem(0).isVisible = true
                    menu.getItem(1).isVisible = false
                }

                ContextualActionMode.NEW_SEQUENCE -> {
                    menu.getItem(0).isVisible = false
                    menu.getItem(1).isVisible = true
                }
            }
        }
        else {
            toolbar.title = getString(R.string.app_name)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_item -> {
                if (viewModel.selectedItems.size != 0) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(getString(R.string.delete_selected_items))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            viewModel.delete()
                            quitContextualActionMode(notifyActivity = true)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            quitContextualActionMode(notifyActivity = true)
                        }
                        .setCancelable(true)
                        .create()
                        .show()
                }
                else {
                    quitContextualActionMode(notifyActivity = true)
                }
            }
            R.id.create_sequence -> {
                if (viewModel.selectedItems.size > 0) {
                    val intent = Intent(context, SequenceDetailActivity::class.java)

                    val allTimers = arrayListOf<Timer>()
                    for (timer in listAdapter.currentList) {
                        allTimers.add(timer)
                    }

                    intent.putParcelableArrayListExtra(EXTRA_ALL_TIMERS, allTimers)
                    intent.putParcelableArrayListExtra(EXTRA_ASSOCIATED_TIMERS, viewModel.selectedItems)

                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(intent)
                        (context as DaggerAppCompatActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }, 100)
                }
            }
            android.R.id.home -> {
                quitContextualActionMode(notifyActivity = true)
            }
        }

        return true
    }

    private fun enterContextualActionMode(mode: ContextualActionMode) {
        contextualActionMode = mode

        listAdapter.isContextualMenuEnabled = true
        (activity as DaggerAppCompatActivity).invalidateOptionsMenu()
        toolbar.title = "0 ${getString(R.string.items_selected)}"
        toolbar.setDisplayHomeAsUpEnabled(true)

        viewModel.sendActionToActivity(ACTION_CONTEXTUAL_MENU, true)
    }

    private fun quitContextualActionMode(notifyActivity: Boolean) {
        contextualActionMode = ContextualActionMode.NONE
        viewModel.selectedItems.clear()
        listAdapter.uncheckAllItems()

        if (notifyActivity) {
            viewModel.sendActionToActivity(ACTION_CONTEXTUAL_MENU, false)
        }

        listAdapter.isContextualMenuEnabled = false
        (activity as DaggerAppCompatActivity).invalidateOptionsMenu()
        toolbar.setDisplayHomeAsUpEnabled(false)
        toolbar.title = getString(R.string.app_name)
    }

    override fun onPause() {
        super.onPause()
        quitContextualActionMode(notifyActivity = false)
    }
}