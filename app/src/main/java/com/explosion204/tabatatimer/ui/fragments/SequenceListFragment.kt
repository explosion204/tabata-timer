package com.explosion204.tabatatimer.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.Constants
import com.explosion204.tabatatimer.Constants.EXTRA_ALL_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_ASSOCIATED_TIMERS
import com.explosion204.tabatatimer.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.Constants.EXTRA_SEQUENCE
import com.explosion204.tabatatimer.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.ui.activities.SequenceDetailActivity
import com.explosion204.tabatatimer.ui.activities.TimerActivity
import com.explosion204.tabatatimer.ui.adapters.SequenceListAdapter
import com.explosion204.tabatatimer.ui.helpers.FragmentThemeHelper
import com.explosion204.tabatatimer.ui.interfaces.OnItemDialogButtonClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemCheckedChangeListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemLongClickListener
import com.explosion204.tabatatimer.viewmodels.SequenceListViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SequenceListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : SequenceListViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var toolbar: ActionBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: SequenceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentThemeHelper.buildCustomInflater(requireContext(), inflater)
            .inflate(R.layout.fragment_sequence_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        toolbar = (requireActivity() as DaggerAppCompatActivity).supportActionBar!!

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listAdapter = SequenceListAdapter()
        recyclerView.adapter = listAdapter

        setObservables()
        setAdapterListeners()
    }

    private fun setObservables() {
        viewModel.getAll().observe(viewLifecycleOwner, Observer {
            listAdapter.submitList(it)
        })

        viewModel.fetchAllTimers().observe(viewLifecycleOwner, Observer {
            viewModel.allTimers = ArrayList(it)
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
                val sequence = listAdapter.getItemAt(viewHolder.adapterPosition)
                viewModel.delete(sequence)
                Snackbar.make(view!!, R.string.item_removed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo_action) {
                        if (viewModel.recentlyDeletedSequence != null) {
                            viewModel.insert(sequence)
                        }
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onShown(sb: Snackbar?) {
                            viewModel.recentlyDeletedSequence = sequence
                            super.onShown(sb)
                        }
                    })
                    .show()
            }
        }).attachToRecyclerView(recyclerView)

        listAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(item: Any) {
                val item = item as SequenceWithTimers

                val args = Bundle()
                args.putString(EXTRA_TITLE, item.sequence.title)
                args.putString(EXTRA_DESCRIPTION, item.sequence.description)

                val dialogFragment = ItemDialogFragment()
                dialogFragment.arguments = args
                dialogFragment.setOnItemDialogButtonClickListener(object : OnItemDialogButtonClickListener {
                    override fun onStartButtonClick() {
                        val intent = Intent(requireContext(), TimerActivity::class.java)
                        intent.putExtra(EXTRA_SEQUENCE, item)
                        startActivity(intent)
                        dialogFragment.dismiss()
                    }

                    override fun onEditButtonClick() {
                        val intent = Intent(requireContext(), SequenceDetailActivity::class.java)
                        intent.putExtra(EXTRA_SEQUENCE, item)
                        intent.putParcelableArrayListExtra(EXTRA_ALL_TIMERS, viewModel.allTimers)
                        intent.putParcelableArrayListExtra(EXTRA_ASSOCIATED_TIMERS, item.timers as ArrayList<Timer>)

                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(intent)
                        }, 100)

                        dialogFragment.dismiss()
                    }

                    override fun onDeleteButtonClick() {
                        AlertDialog.Builder(requireContext())
                            .setMessage(getString(R.string.delete_this_item))
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
                    enterContextualActionMode()
                }
            }
        })

        listAdapter.setOnItemCheckedChangeListener(object : OnItemCheckedChangeListener {
            override fun onItemChecked(item: Any, flag: Boolean) {
                val sequence = item as SequenceWithTimers

                if (flag) {
                    viewModel.selectedItems.add(sequence)
                }
                else {
                    viewModel.selectedItems.remove(sequence)
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

        requireActivity().menuInflater.inflate(R.menu.list_contextual_menu, menu)

        if (listAdapter.isContextualMenuEnabled) {
            menu.getItem(0).isVisible = true
            menu.getItem(1).isVisible = false
            menu.getItem(2).isVisible = false

        }
        else {
            menu.getItem(0).isVisible = false
            menu.getItem(1).isVisible = false
            menu.getItem(2).isVisible = true

            toolbar.title = getString(R.string.app_name)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_item -> {
                if (viewModel.selectedItems.size != 0) {
                    AlertDialog.Builder(requireContext())
                        .setMessage(getString(R.string.delete_selected_items))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            viewModel.delete()
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

    private fun enterContextualActionMode() {
        listAdapter.isContextualMenuEnabled = true
        requireActivity().invalidateOptionsMenu()
        toolbar.title = "0 ${getString(R.string.items_selected)}"
        toolbar.setDisplayHomeAsUpEnabled(true)

        viewModel.sendActionToActivity(Constants.ACTION_CONTEXTUAL_MENU, true)
    }

    private fun quitContextualActionMode() {
        viewModel.selectedItems.clear()
        listAdapter.uncheckAllItems()

        listAdapter.isContextualMenuEnabled = false
        requireActivity().invalidateOptionsMenu()
        viewModel.sendActionToActivity(Constants.ACTION_CONTEXTUAL_MENU, false)
        toolbar.setDisplayHomeAsUpEnabled(false)
        toolbar.title = getString(R.string.app_name)
    }

    override fun onPause() {
        super.onPause()
        quitContextualActionMode()
    }
}