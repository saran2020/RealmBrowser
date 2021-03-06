package com.github.saran2020.realmbrowser

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.saran2020.realmbrowser.data.model.classrepresentation.ClassItem
import com.github.saran2020.realmbrowser.data.model.classrepresentation.ObjectType

/**
 * Created by Saran on 08-Jan-18.
 */
class ResultAdapter(private val context: Context, private val items: List<ClassItem>) :
        RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    val TAG = ResultAdapter::class.java.canonicalName

    private val ITEM_PER_SCREEN_VERTICAL: Int = context.resources.getInteger(R.integer.ITEMS_PER_SCREEN_OBJECT)
    private val size: Int = items[0].fieldsList.size + 1
    @ColorInt
    private val oddColor = ResourcesCompat.getColor(context.resources, R.color.tableBackroundOdd, null)
    @ColorInt
    private val evenColor = ResourcesCompat.getColor(context.resources, R.color.tableBackroundEven, null)

    private val onObjectClickListener = View.OnClickListener { view: View? ->

        // tag is of format "rowPos|colPos"
        val tag = view?.tag.toString().split("|")
                .map { it.toInt() }.toList()

        val item = items[tag[0]]
        val fieldItem = if (tag[1] == 0) {
            // position 0 is primary key
            item.primaryKey.value as ObjectType
        } else {
            // pos 1 to fieldsList.size is occupied by other field items hence tag[1] - 1
            item.fieldsList[tag[1] - 1].value as ObjectType
        }

        Log.d(TAG, "Received click for pos ${tag[0]}|${tag[1]} item = $fieldItem")
        startResultActivity(context, fieldItem)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getSingleResultLayout(context, size, ITEM_PER_SCREEN_VERTICAL))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        populateViews(context, holder.item, onObjectClickListener, items[position], position)

        if (position % 2 == 0) {
            holder.item.setBackgroundColor(evenColor)
        } else {
            holder.item.setBackgroundColor(oddColor)
        }
    }

    class ViewHolder(val item: LinearLayout) : RecyclerView.ViewHolder(item)
}