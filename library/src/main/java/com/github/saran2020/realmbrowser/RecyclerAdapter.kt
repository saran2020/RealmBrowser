package com.github.saran2020.realmbrowser

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * Created by its me on 08-Jan-18.
 */
class RecyclerAdapter(val context: Context, val items: List<List<FieldItem>>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    val size: Int = items.get(0).size

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(getLinearLayout(context, size))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        populateViews(holder?.item, items.get(position))
    }

    class ViewHolder(val item: LinearLayout) : RecyclerView.ViewHolder(item) {

    }
}