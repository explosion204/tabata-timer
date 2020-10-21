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
import com.explosion204.tabatatimer.data.entities.BaseEntity

class ItemListAdapter<T : BaseEntity> :
    ListAdapter<T, ItemListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.id == newItem.id;
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.isSame(oldItem)
        }

    }) {

    interface OnItemClickListener {
        fun onItemClick(item: Any)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(item: Any)
    }

    interface OnItemCheckedChangeListener {
        fun onItemChecked(item: Any, checkBox: CheckBox)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val viewHolder = ItemViewHolder(view)

        val checkBox = view.findViewById<CheckBox>(R.id.item_checkbox)


        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                val item = getItem(position)

                if (itemCheckedChangeListener != null && isContextualMenuEnabled) {
                    checkBox.isChecked = !checkBox.isChecked
                }
                else {
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

        checkBox.setOnCheckedChangeListener { _, _ ->
            val position = viewHolder.adapterPosition
            if (itemCheckedChangeListener != null && position != RecyclerView.NO_POSITION) {
                itemCheckedChangeListener!!.onItemChecked(getItem(position), checkBox)
            }
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.title.text = currentItem.title
        holder.description.text = currentItem.description

        if (isContextualMenuEnabled) {
            holder.checkbox.visibility = View.VISIBLE
        }
        else {
            holder.checkbox.visibility = View.GONE
            holder.checkbox.isChecked = false
        }
    }

    fun getItemAt(position: Int): T = getItem(position)


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_desc)
        val checkbox: CheckBox = itemView.findViewById(R.id.item_checkbox)
    }
}