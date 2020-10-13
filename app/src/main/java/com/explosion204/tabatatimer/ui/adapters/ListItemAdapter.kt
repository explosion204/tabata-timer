package com.explosion204.tabatatimer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.Timer

class ListItemAdapter : RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>() {
    var items: List<Timer> = ArrayList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val currentItem = items[position]

        holder.title.text = currentItem.title
    }

     class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.item_title)
    }
}