package com.github.saran2020.realmbrowser

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Saran on 11-Nov-17.
 */
class FindAdapter(private var context: Context, private var itemList: List<String>) : RecyclerView.Adapter<FindAdapter.ViewHolder>() {

    private val inflator = LayoutInflater.from(context)

    var selectedItem = NO_ITEM_SELECTED

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflator.inflate(R.layout.chip_find_item, parent, false)
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

        selectedItem = if (selectedPosition == selectedItem)
            NO_ITEM_SELECTED
        else
            selectedPosition

        if (selectedItem == NO_ITEM_SELECTED) {

            // No item selected, set it back to default
            val defaultBackGround = ResourcesCompat.getDrawable(context.resources, R.drawable.background_chip, null)
            textView.background = defaultBackGround

            val textColor = ResourcesCompat.getColor(context.resources, R.color.findTextColor, null)
            textView.setTextColor(textColor)
        } else {

            // Item selected, set selected color
            val selectedColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
            textView.setBackgroundColor(selectedColor)

            val textColor = ResourcesCompat.getColor(context.resources, R.color.buttonTextColor, null)
            textView.setTextColor(textColor)
        }
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