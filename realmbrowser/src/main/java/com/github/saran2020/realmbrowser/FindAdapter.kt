package com.github.saran2020.realmbrowser

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by its me on 11-Nov-17.
 */
class FindAdapter(private var itemList: List<Short>) : RecyclerView.Adapter<FindAdapter.ViewHolder>() {

    private var selectedItem = Constants.NO_ITEM_SELECTED

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.chip_find_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        var fromText = ""

        when (itemList[position]) {
            Constants.FIND_ALL -> fromText = Constants.FIND_ALL_STRING
            Constants.FIND_FIRST -> fromText = Constants.FIND_FIRST_STRING
        }

        holder?.textItemFind?.text = fromText
    }

    private fun setSelectedItem(position: Int) {
        selectedItem = if (position == selectedItem)
            Constants.NO_ITEM_SELECTED
        else
            position
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