package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.StyleRes
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.saran2020.realmbrowser.data.model.classrepresentation.ClassItem
import com.github.saran2020.realmbrowser.data.model.classrepresentation.ObjectType
import io.realm.RealmFieldType

/**
 * A helper class to get the gridview with populated field items
 * Created by Saran on 02-Jan-18.
 */

val TAG = "ViewHelper"

private fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun getLinearLayout(context: Context, size: Int, verticalViewsInScreen: Int): LinearLayout {
    val layout = getLinearLayout(context)
    val widthInPx = getTextViewWidth(context, verticalViewsInScreen)
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

fun populateHeader(context: Context, layout: LinearLayout, item: ClassItem, verticalViewsInScreen: Int) {

    val fieldCount = item.fieldsList.size
    val widthInPx = getTextViewWidth(context, verticalViewsInScreen)
    val textPadding = dpToPx(8)

    // primary key first
    layout.addView(getHeaderTextView(context, item.primaryKey.name, widthInPx, textPadding, true))

    (0 until fieldCount)
            .map { getHeaderTextView(context, item.fieldsList[it].name, widthInPx, textPadding, false) }
            .forEach { layout.addView(it) }

    Log.d(TAG, "Completed")
}

fun populateHeader(context: Context, layout: LinearLayout, item: String, verticalViewsInScreen: Int) {

    val widthInPx = getTextViewWidth(context, verticalViewsInScreen)

    // primary key first
    layout.addView(getHeaderTextView(context, item, widthInPx, 0, false))
    Log.d(TAG, "Completed")
}

private fun getHeaderTextView(context: Context, field: String, textWidth: Int, textPadding: Int, isPrimaryKey: Boolean): TextView {

    val textHeader = getTextView(context, textWidth, textPadding)
    setStyleToText(context, textHeader, R.style.headerTextAppearance)

    if (isPrimaryKey) {
        textHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_primary_key_icon, 0, 0, 0)
        textHeader.compoundDrawablePadding = dpToPx(4)
    }

    textHeader.text = field
    Log.d(TAG, "data = ${field}")

    return textHeader
}

/**
 * populate views of object type
 */
fun populateViews(context: Context, layout: LinearLayout?,
                  objectClickListener: View.OnClickListener,
                  item: ClassItem, position: Int) {

    if (layout == null)
        return

    val viewCount = layout.childCount

    // primary key item first
    setTextToView(context, layout.getChildAt(0) as TextView, item.primaryKey.value,
            item.primaryKey.type, objectClickListener, position, 0)

    // other fields
    for (index in 1 until viewCount) {
        // index - 1 because first item of view is primary key
        val fieldItem = item.fieldsList[index - 1]
        setTextToView(context, layout.getChildAt(index) as TextView, fieldItem.value,
                fieldItem.type, objectClickListener, position, index)
    }
}

/**
 * populate views of native type
 */
fun populateViews(context: Context, layout: LinearLayout?,
                  value: Any?, valueType: Int, position: Int) {

    if (layout == null)
        return

    setTextToView(context, layout.getChildAt(0) as TextView,
            value, valueType, null, position, 0)
}

private fun setTextToView(context: Context, textView: TextView, value: Any?, valueType: Int,
                          objectClickListener: View.OnClickListener?, rowPos: Int, viewPos: Int) {

    val fieldData: String
    var isTextHyperlinkStyleable = false

    if (value == null) {
        fieldData = "null"
        setStyleToText(context, textView, R.style.nullTextAppearance)
    } else {

        // TODO: Check casting of native values might not be required because of RMI
        fieldData = when (valueType) {
            RealmFieldType.BOOLEAN.nativeValue -> (value as Boolean).toString()
            RealmFieldType.FLOAT.nativeValue -> (value as Float).toString()
            RealmFieldType.DOUBLE.nativeValue -> (value as Double).toString()

            RealmFieldType.STRING.nativeValue -> {
                if ((value as String).isEmpty())
                    "\"\""
                else
                    value
            }

            RealmFieldType.INTEGER.nativeValue -> {
                when (value) {
                    is Long -> value.toString()
                    is Int -> value.toString()
                    is Short -> value.toString()
                    is Byte -> value.toString()
                    else -> ERROR_TEXT
                }
            }

            RealmFieldType.OBJECT.nativeValue -> {
                isTextHyperlinkStyleable = true
                (value as ObjectType).displayText
            }
            RealmFieldType.LIST.nativeValue -> {
                isTextHyperlinkStyleable = true
                (value as ObjectType).displayText
            }

            else -> {
                if (valueType > RealmFieldType.LIST.nativeValue) {
                    isTextHyperlinkStyleable = true
                    (value as ObjectType).displayText
                } else {
                    ERROR_TEXT
                }
            }
        }
    }

    if (isTextHyperlinkStyleable) {
        makeTextViewHyperlink(textView, fieldData, objectClickListener, "$rowPos|$viewPos")
    } else {
        Log.d(TAG, "data = $fieldData")
        textView.text = fieldData
    }
}

private fun setStyleToText(context: Context, textView: TextView, @StyleRes resId: Int) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        textView.setTextAppearance(context, resId)
    } else {
        textView.setTextAppearance(resId)
    }
}

private fun makeTextViewHyperlink(textView: TextView, text: String,
                                  objectClickListener: View.OnClickListener?, tag: String) {

    val ssb = SpannableStringBuilder(text)
    ssb.setSpan(URLSpan("#"), 0, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.setText(ssb, TextView.BufferType.SPANNABLE)

    textView.tag = tag
    textView.setOnClickListener(objectClickListener)
}

private fun getTextViewWidth(context: Context, verticalViewsInScreen: Int): Int {
    val metrics = context.resources.displayMetrics
    return metrics.widthPixels / verticalViewsInScreen
}