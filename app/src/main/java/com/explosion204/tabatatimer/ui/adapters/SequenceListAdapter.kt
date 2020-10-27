package com.explosion204.tabatatimer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.ui.interfaces.OnItemCheckedChangeListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemLongClickListener

open class SequenceListAdapter :
    ListAdapter<SequenceWithTimers, SequenceListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<SequenceWithTimers>() {
        override fun areItemsTheSame(oldItem: SequenceWithTimers, newItem: SequenceWithTimers): Boolean {
            return oldItem.sequence.seqId == newItem.sequence.seqId
        }

        override fun areContentsTheSame(oldItem: SequenceWithTimers, newItem: SequenceWithTimers): Boolean {
            return (oldItem.sequence.copy(seqId = newItem.sequence.seqId) == newItem.sequence) &&
                    (oldItem.timers == newItem.timers)
        }

    }) {

    // indicates whether or not certain item at certain position has to be checked
    private var itemStatuses = ArrayList<Boolean>()

    fun uncheckAllItems() {
        itemStatuses.fill(false)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }

    fun setOnItemCheckedChangeListener(listener: OnItemCheckedChangeListener) {
        itemCheckedChangeListener = listener
    }

    var isContextualMenuEnabled = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null
    private var itemCheckedChangeListener: OnItemCheckedChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sequence_item, parent, false)
        val viewHolder = ItemViewHolder(view)
        val checkBox = view.findViewById<CheckBox>(R.id.item_checkbox)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                if (itemCheckedChangeListener != null && isContextualMenuEnabled) {
                    checkBox.isChecked = !checkBox.isChecked
                    itemStatuses[position] = !itemStatuses[position]

                    itemCheckedChangeListener!!.onItemChecked(getItem(position), itemStatuses[position])
                }

                if (itemClickListener != null && !isContextualMenuEnabled) {
                    val item = getItem(position)
                    itemClickListener!!.onItemClick(item)
                }
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            if (itemLongClickListener != null && position != RecyclerView.NO_POSITION) {
                itemLongClickListener!!.onItemLongClick(getItem(position))
            }

            true
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)

        while (position >= itemStatuses.size) {
            itemStatuses.add(false)
        }

        holder.checkbox.isChecked = itemStatuses[position]

        holder.title.text = currentItem.sequence.title
        holder.description.text = currentItem.sequence.description
        holder.totalTimersTextView.text = currentItem.timers.size.toString()

        var totalTime = 0
        for (timer in currentItem.timers) {
            totalTime += with(timer) { (preparations + workout + rest) * cycles }
        }

        holder.totalTimeTextView.text = "${totalTime / 60}m ${totalTime % 60}s"

        if (isContextualMenuEnabled) {
            holder.checkbox.visibility = View.VISIBLE
        }
        else {
            holder.checkbox.visibility = View.GONE
            holder.checkbox.isChecked = false
        }
    }

    fun getItemAt(position: Int): SequenceWithTimers = getItem(position)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_desc)
        val checkbox: CheckBox = itemView.findViewById(R.id.item_checkbox)
        val totalTimersTextView: TextView = itemView.findViewById(R.id.total_timers_textview)
        val totalTimeTextView: TextView = itemView.findViewById(R.id.total_time_textview)
    }
}