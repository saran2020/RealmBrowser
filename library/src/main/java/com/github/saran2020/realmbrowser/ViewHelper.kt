package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.StyleRes
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.github.saran2020.realmbrowser.model.ClassItem
import com.github.saran2020.realmbrowser.model.FieldItem
import com.github.saran2020.realmbrowser.model.ObjectType
import io.realm.RealmFieldType

/**
 * A helper class to get the gridview with populated field items
 * Created by its me on 02-Jan-18.
 */

val TAG = "ViewHelper"

//fun getGridLayoutForFieldItem(context: Context, fields: List<FieldItem>): GridLayout {
//    val gridLayout = createGridLayout(context)
//    val nameColSpec = GridLayout.spec(0, 2)
//    val valueColSpec = GridLayout.spec(2, 3, 1F)
//
//    var rowIndex = 0
//    for (field in fields) {
//
//        var fieldData: String
//
//        if (field.value == null) {
//            fieldData = "null"
//        } else {
//
//            fieldData = when (field.type) {
//                TYPE_BOOLEAN -> (field.value as Boolean).toString()
//                TYPE_BYTE -> (field.value as Byte).toString()
//                TYPE_CHAR -> (field.value as Char).toString()
//                TYPE_SHORT -> (field.value as Short).toString()
//                TYPE_INT -> (field.value as Int).toString()
//                TYPE_LONG -> (field.value as Long).toString()
//                TYPE_FLOAT -> (field.value as Float).toString()
//                TYPE_DOUBLE -> (field.value as Double).toString()
//                TYPE_STRING -> {
//                    if ((field.value as String).isEmpty())
//                        "\"\""
//                    else
//                        field.value
//                }
//                else -> "Some error occurred"
//            }
//        }
//
//        val rowSpec = GridLayout.spec(rowIndex)
//
//        // print field name
//        val textViewFieldName = TextView(context, null, R.style.LabelStyle)
//        textViewFieldName.setBackgroundResource(R.drawable.field_item_background)
//        textViewFieldName.text = String.format("%s:", field.fieldName)
//
//        val layoutParms = GridLayout.LayoutParams(rowSpec, nameColSpec)
//        layoutParms.setGravity(Gravity.FILL)
//        gridLayout.addView(textViewFieldName, layoutParms)
//
//        val fieldValue: View
//
//        // TODO: Print realm list
//        // if type is realm object, only print the field name and then  recursively
//        // call the function again to print data inside
//        if (field.type == TYPE_REALM_OBJECT) {
//
//            @Suppress("UNCHECKED_CAST")
//            fieldValue = getGridLayoutForFieldItem(context, field.value as List<FieldItem>)
//        } else {
//
//            // print field value
//            val textViewFieldValue = TextView(context, null, R.style.ValueStyle)
//            textViewFieldValue.text = fieldData
//
//            fieldValue = textViewFieldValue
//        }
//
//        val valueLayoutParms = GridLayout.LayoutParams(rowSpec, valueColSpec)
//        valueLayoutParms.setGravity(Gravity.FILL)
//        fieldValue.setBackgroundResource(R.drawable.field_item_background)
//        gridLayout.addView(fieldValue, valueLayoutParms)
//
//        rowIndex++
//    }
//
//    return gridLayout
//}

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

fun populateHeader(context: Context, layout: LinearLayout, item: ClassItem) {

    val fieldCount = item.fieldsList.size
    val widthInPx = getTextViewWidth(context)
    val textPadding = dpToPx(8)

    // primary key first
    layout.addView(getHeaderText(context, item.primaryKey, widthInPx, textPadding))

    for (index in 0 until fieldCount) {
        val textHeader = getHeaderText(context, item.fieldsList[index], widthInPx, textPadding)
        layout.addView(textHeader)
    }

    Log.d(TAG, "Completed")
}

private fun getHeaderText(context: Context, field: FieldItem, textWidth: Int, textPadding: Int): TextView {

    val textHeader = getTextView(context, textWidth, textPadding)
    setStyleToText(context, textHeader, R.style.headerTextAppearance)

    textHeader.text = field.name
    Log.d(TAG, "data = ${field.name}")

    return textHeader
}

fun populateViews(context: Context, layout: LinearLayout?, item: ClassItem) {

    if (layout == null)
        return

    val viewCount = layout.childCount

    // primary key item first
    setTextToView(context, layout.getChildAt(0) as TextView, item.primaryKey)

    // other fields
    for (index in 1 until viewCount) {
        // index - 1 because first item of view is primary key
        setTextToView(context, layout.getChildAt(index) as TextView, item.fieldsList[index - 1])
    }
}

private fun setTextToView(context: Context, textView: TextView, field: FieldItem) {

    val fieldData: String

    if (field.value == null) {
        fieldData = "null"
        setStyleToText(context, textView, R.style.nullTextAppearance)
    } else {

        fieldData = when (field.type) {
            RealmFieldType.BOOLEAN -> (field.value as Boolean).toString()
            RealmFieldType.FLOAT -> (field.value as Float).toString()
            RealmFieldType.DOUBLE -> (field.value as Double).toString()

            RealmFieldType.STRING -> {
                if ((field.value as String).isEmpty())
                    "\"\""
                else
                    field.value as String
            }

            RealmFieldType.INTEGER -> {
                when (field.value) {
                    is Long -> (field.value as Long).toString()
                    is Int -> (field.value as Int).toString()
                    is Short -> (field.value as Short).toString()
                    is Byte -> (field.value as Byte).toString()
                    else -> ERROR_TEXT
                }
            }

            RealmFieldType.OBJECT -> (field.value as ObjectType).fieldText
            RealmFieldType.LIST -> field.value as String

            else -> ERROR_TEXT
        }
    }

    Log.d(TAG, "data = $fieldData")
    textView.text = fieldData
}

private fun setStyleToText(context: Context, textView: TextView, @StyleRes resId: Int){

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        textView.setTextAppearance(context, resId)
    } else {
        textView.setTextAppearance(resId)
    }
}

private fun getTextViewWidth(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return metrics.widthPixels / 3
}