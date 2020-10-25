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
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.ui.interfaces.OnItemCheckedChangeListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener
import com.explosion204.tabatatimer.ui.interfaces.OnItemLongClickListener

open class TimerListAdapter :
    ListAdapter<Timer, TimerListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<Timer>() {
        override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem.timerId == newItem.timerId
        }

        override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem.copy(timerId = newItem.timerId) == newItem
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timer_item, parent, false)
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

        holder.title.text = currentItem.title
        holder.description.text = currentItem.description

        holder.prepTextView.text = "${currentItem.preparations / 60}m ${currentItem.preparations % 60}s"
        holder.workoutTextView.text = "${currentItem.workout / 60}m ${currentItem.workout % 60}s"
        holder.restTextView.text = "${currentItem.rest / 60}m ${currentItem.rest % 60}s"
        holder.cyclesTextView.text = currentItem.cycles.toString()

        val total = with(currentItem) { preparations + workout + rest }
        holder.totalTextView.text = "${total / 60}m ${total % 60}s"


        if (isContextualMenuEnabled) {
            holder.checkbox.visibility = View.VISIBLE
        }
        else {
            holder.checkbox.visibility = View.GONE
            holder.checkbox.isChecked = false
        }
    }

    fun getItemAt(position: Int): Timer = getItem(position)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_desc)
        val checkbox: CheckBox = itemView.findViewById(R.id.item_checkbox)
        val prepTextView: TextView = itemView.findViewById(R.id.prep_textview)
        val workoutTextView: TextView = itemView.findViewById(R.id.workout_textview)
        val restTextView: TextView = itemView.findViewById(R.id.rest_textview)
        val cyclesTextView: TextView = itemView.findViewById(R.id.cycles_textview)
        val totalTextView: TextView = itemView.findViewById(R.id.total_textview)
    }
}