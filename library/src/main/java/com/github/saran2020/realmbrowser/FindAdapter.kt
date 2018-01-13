package com.github.saran2020.realmbrowser

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by its me on 11-Nov-17.
 */
class FindAdapter(private var itemList: List<String>) : RecyclerView.Adapter<FindAdapter.ViewHolder>() {

    var selectedItem = NO_ITEM_SELECTED

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.chip_find_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.textItemFind?.text = itemList[position]
    }

    private fun setSelectedItem(position: Int) {

        var selectedPosition: Byte = when (position) {
            0 -> FIND_ALL
            1 -> FIND_FIRST
            else -> NO_ITEM_SELECTED
        }

        selectedItem = if (selectedPosition == selectedItem)
            NO_ITEM_SELECTED
        else
            selectedPosition

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textItemFind: TextView = itemView.findViewById(R.id.findItemText_library)

        private var cancelOnClickListener = View.OnClickListener {
            var position = adapterPosition
            setSelectedItem(position)
        }

        init {
            itemView.setOnClickListener(cancelOnClickListener)
        }
    }
}