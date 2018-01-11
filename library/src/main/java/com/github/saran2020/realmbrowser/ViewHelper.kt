package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * A helper class to get the gridview with populated field items
 * Created by its me on 02-Jan-18.
 */

val TAG = "ViewHelper"

fun getGridLayoutForFieldItem(context: Context, fields: List<FieldItem>): GridLayout {
    val gridLayout = createGridLayout(context)
    val nameColSpec = GridLayout.spec(0, 2)
    val valueColSpec = GridLayout.spec(2, 3, 1F)

    var rowIndex = 0
    for (field in fields) {

        var fieldData: String

        if (field.value == null) {
            fieldData = "null"
        } else {

            fieldData = when (field.type) {
                TYPE_BOOLEAN -> (field.value as Boolean).toString()
                TYPE_BYTE -> (field.value as Byte).toString()
                TYPE_CHAR -> (field.value as Char).toString()
                TYPE_SHORT -> (field.value as Short).toString()
                TYPE_INT -> (field.value as Int).toString()
                TYPE_LONG -> (field.value as Long).toString()
                TYPE_FLOAT -> (field.value as Float).toString()
                TYPE_DOUBLE -> (field.value as Double).toString()
                TYPE_STRING -> {
                    if ((field.value as String).isEmpty())
                        "\"\""
                    else
                        field.value
                }
                else -> "Some error occurred"
            }
        }

        val rowSpec = GridLayout.spec(rowIndex)

        // print field name
        val textViewFieldName = TextView(context, null, R.style.LabelStyle)
        textViewFieldName.setBackgroundResource(R.drawable.field_item_background)
        textViewFieldName.text = String.format("%s:", field.fieldName)

        val layoutParms = GridLayout.LayoutParams(rowSpec, nameColSpec)
        layoutParms.setGravity(Gravity.FILL)
        gridLayout.addView(textViewFieldName, layoutParms)

        val fieldValue: View

        // TODO: Print realm list
        // if type is realm object, only print the field name and then  recursively
        // call the function again to print data inside
        if (field.type == TYPE_REALM_OBJECT) {

            @Suppress("UNCHECKED_CAST")
            fieldValue = getGridLayoutForFieldItem(context, field.value as List<FieldItem>)
        } else {

            // print field value
            val textViewFieldValue = TextView(context, null, R.style.ValueStyle)
            textViewFieldValue.text = fieldData

            fieldValue = textViewFieldValue
        }

        val valueLayoutParms = GridLayout.LayoutParams(rowSpec, valueColSpec)
        valueLayoutParms.setGravity(Gravity.FILL)
        fieldValue.setBackgroundResource(R.drawable.field_item_background)
        gridLayout.addView(fieldValue, valueLayoutParms)

        rowIndex++
    }

    return gridLayout
}

private fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().getDisplayMetrics().density).toInt()
}

private fun createGridLayout(context: Context): GridLayout {

    val gridLayout = GridLayout(context)
    gridLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    gridLayout.columnCount = 5
    gridLayout.isColumnOrderPreserved = true

    return gridLayout
}

fun getLinearLayout(context: Context, size: Int): LinearLayout {
    val layout = getLinearLayout(context)
    val widthInPx = getTextViewWidth(context)
    val textPadding = dpToPx(8)

    for (index in 0 until size) {
        val textView = getTextView(context, widthInPx, textPadding)
        layout.addView(textView)
    }

    return layout
}

private fun getLinearLayout(context: Context): LinearLayout {
    val layout = LinearLayout(context)
    layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
    layout.orientation = LinearLayout.HORIZONTAL
    return layout
}

private fun getTextView(context: Context, widthPx: Int, padding: Int): TextView {

    val layoutParms = LinearLayout.LayoutParams(widthPx,
            ViewGroup.LayoutParams.WRAP_CONTENT)

    val textView = TextView(context)
    textView.layoutParams = layoutParms
    textView.maxLines = 1
    textView.ellipsize = TextUtils.TruncateAt.END
    textView.setPadding(padding, padding, padding, padding)

    return textView
}

fun populateHeader(context: Context, layout: LinearLayout, item: List<FieldItem>) {

    val fieldCount = item.size
    val widthInPx = getTextViewWidth(context)
    val textPadding = dpToPx(8)

    for (index in 0 until fieldCount) {
        val textHeader = getHeaderText(context, item.get(index), widthInPx, textPadding)
        layout.addView(textHeader)
    }

    Log.d(TAG, "Completed")
}

private fun getHeaderText(context: Context, field: FieldItem, textWidth: Int, textPadding: Int): TextView {

    val textHeader = getTextView(context, textWidth, textPadding)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        textHeader.setTextAppearance(context, R.style.headerTextAppearance)
    } else {
        textHeader.setTextAppearance(R.style.headerTextAppearance)
    }

    textHeader.text = field.fieldName
    Log.d(TAG, "data = ${field.fieldName}")

    return textHeader
}

fun populateViews(layout: LinearLayout?, item: List<FieldItem>) {

    if (layout == null)
        return

    val viewCount = layout.childCount
    for (index in 0 until viewCount) {
        setTextToView(layout.getChildAt(index) as TextView, item.get(index))
    }
}

private fun setTextToView(textView: TextView, field: FieldItem) {

    val fieldData: String

    if (field.value == null) {
        fieldData = "null"
    } else {

        fieldData = when (field.type) {
            TYPE_BOOLEAN -> (field.value as Boolean).toString()
            TYPE_BYTE -> (field.value as Byte).toString()
            TYPE_CHAR -> (field.value as Char).toString()
            TYPE_SHORT -> (field.value as Short).toString()
            TYPE_INT -> (field.value as Int).toString()
            TYPE_LONG -> (field.value as Long).toString()
            TYPE_FLOAT -> (field.value as Float).toString()
            TYPE_DOUBLE -> (field.value as Double).toString()
            TYPE_STRING -> {
                if ((field.value as String).isEmpty())
                    "\"\""
                else
                    field.value
            }
            else -> "Some error occurred"
        }
    }

    Log.d(TAG, "data = $fieldData")
    textView.text = fieldData
}


private fun getTextViewWidth(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return metrics.widthPixels / 3
}