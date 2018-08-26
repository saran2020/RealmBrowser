package com.github.saran2020.realmbrowser

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Saran on 11-Nov-17.
 */
class FindAdapter(private var context: Context,
                  private var itemList: List<String>,
                  private var findSelectListener: (pos: Int) -> RecyclerView.ViewHolder?) :
        RecyclerView.Adapter<FindAdapter.ViewHolder>() {

    var selectedItem = NO_ITEM_SELECTED

    private val inflater = LayoutInflater.from(context)

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.chip_find_item, parent, false)
        return ViewHolder(view as TextView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position]
    }

    private fun setSelectedItem(textView: TextView, position: Int) {

        val selectedPosition: Byte = when (position) {
            0 -> FIND_ALL
            1 -> FIND_FIRST
            else -> NO_ITEM_SELECTED
        }

        if (selectedPosition == selectedItem) {
            textView.isSelected = false
            selectedItem = NO_ITEM_SELECTED
        } else {

            if (selectedItem != NO_ITEM_SELECTED) {
                val previousSelectedViewHolder =
                        findSelectListener(selectedItem.toInt()) as ViewHolder?
                previousSelectedViewHolder?.textView?.isSelected = false
            }

            textView.isSelected = true
            selectedItem = selectedPosition
        }


        textView.isSelected = selectedItem != NO_ITEM_SELECTED
    }

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {


        private var cancelOnClickListener = View.OnClickListener {
            val position = adapterPosition
            setSelectedItem(it as TextView, position)
        }

        init {
            textView.setOnClickListener(cancelOnClickListener)
        }
    }
}