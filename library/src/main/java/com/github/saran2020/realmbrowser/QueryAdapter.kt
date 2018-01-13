package com.github.saran2020.realmbrowser

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.saran2020.realmbrowser.model.QueryItem

/**
 * Created by its me on 11-Nov-17.
 */
class QueryAdapter : RecyclerView.Adapter<QueryAdapter.ViewHolder>() {

    private var itemList: MutableList<QueryItem> = ArrayList<QueryItem>()

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.chip_query_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        var queryString = ""

        when (itemList.get(position).type) {
            QUERY_EQUAL_TO -> queryString = QUERY_EQUAL_TO_STRING
        }

        holder?.textItemQuery?.text = queryString
    }

    private fun removeItemFromList(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    public fun addItemToList(queryItem: QueryItem) {
        var position = itemList.size
        itemList.add(queryItem)
        notifyItemInserted(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textItemQuery: TextView = itemView.findViewById(R.id.queryItemText_library)
        var imageItemCancel: ImageView = itemView.findViewById(R.id.queryItemCancel_library)

        private var cancelOnClickListener = View.OnClickListener {
            var position = adapterPosition
            removeItemFromList(position)
        }

        init {
            itemView.setOnClickListener(cancelOnClickListener)
        }
    }
}