package com.github.saran2020.realmbrowser

import android.content.Context
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * Created by Saran on 08-Jan-18.
 */
class NativeResultsAdapter(private val context: Context,
                           private val items: List<Any?>,
                           private val itemType: Int) : RecyclerView.Adapter<NativeResultsAdapter.ViewHolder>() {

    private val ITEM_PER_SCREEN_VERTICAL: Int = context.resources.getInteger(R.integer.ITEMS_PER_SCREEN_NATIVE)
    val TAG = NativeResultsAdapter::class.java.canonicalName

    private val size = 1

    @ColorInt
    private val oddColor: Int
    @ColorInt
    private val evenColor: Int

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            oddColor = context.getColor(R.color.tableBackroundOdd)
            evenColor = context.getColor(R.color.tableBackroundEven)
        } else {
            oddColor = context.resources.getColor(R.color.tableBackroundOdd)
            evenColor = context.resources.getColor(R.color.tableBackroundEven)
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getLinearLayout(context, size, ITEM_PER_SCREEN_VERTICAL))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        populateViews(context, holder.item, items[position], itemType, position)

        if (position % 2 == 0) {
            holder.item.setBackgroundColor(evenColor)
        } else {
            holder.item.setBackgroundColor(oddColor)
        }
    }

    class ViewHolder(val item: LinearLayout) : RecyclerView.ViewHolder(item)
}