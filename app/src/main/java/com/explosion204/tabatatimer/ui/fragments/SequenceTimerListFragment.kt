package com.explosion204.tabatatimer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.Constants.ACTION_SELECT_TIMERS_MODE
import com.explosion204.tabatatimer.ui.adapters.TimerListAdapter
import com.explosion204.tabatatimer.ui.interfaces.OnItemCheckedChangeListener
import com.explosion204.tabatatimer.viewmodels.SequenceDetailViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import java.util.ArrayList
import javax.inject.Inject

class SequenceTimerListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : SequenceDetailViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: ActionBar
    private lateinit var listAdapter: TimerListAdapter
    private var selectedItems = ArrayList<Timer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        toolbar = (activity as DaggerAppCompatActivity).supportActionBar!!

        listAdapter = TimerListAdapter()

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listAdapter
        listAdapter.isContextualMenuEnabled = true

        toolbar.title = getString(R.string.select_items)

        setObservables()
        setCallbacks()
    }

    private fun setObservables() {
        viewModel.getNotAssociatedTimers().observe(viewLifecycleOwner, Observer {
            listAdapter.submitList(it)
        })
    }

    private fun setCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })

        listAdapter.setOnItemCheckedChangeListener(object : OnItemCheckedChangeListener {
            override fun onItemChecked(item: Any, flag: Boolean) {
                val timer = item as Timer

                if (flag) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        val toolbar = (requireActivity() as DaggerAppCompatActivity).supportActionBar!!
        toolbar.setHomeAsUpIndicator(R.drawable.ic_close_24)
        toolbar.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                viewModel.addToAssociated(selectedItems)
                navigateBack()
            }
            android.R.id.home -> {
                navigateBack()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigateBack() {
        viewModel.sendActionToActivity(ACTION_SELECT_TIMERS_MODE, false)
        Navigation.findNavController(requireView()).navigate(R.id.action_return_to_sequenceDetailFragment)
    }
}