package com.explosion204.tabatatimer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.ui.interfaces.OnItemClickListener

class UpcomingTimersListAdapter(private val context: Context, private val timers: ArrayList<Timer>) :
    RecyclerView.Adapter<UpcomingTimersListAdapter.ItemViewHolder>() {

    private var selected = Array(timers.size) { false }
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_timer_item, parent, false)
        val viewHolder = ItemViewHolder(view)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener!!.onItemClick(position)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = timers[position]

        if (selected[position]) {
            holder.itemLinearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.selectionColor))
        }
        else {
            holder.itemLinearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
    }

    override fun getItemCount(): Int {
        return timers.size
    }

    fun setSelection(position: Int) {
        selected.fill(false)
        selected[position] = true
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemLinearLayout: LinearLayout = view.findViewById(R.id.item_layout)
        val title: TextView = view.findViewById(R.id.item_title)
        val description: TextView = view.findViewById(R.id.item_desc)
    }
}