package com.github.saran2020.realmbrowser

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * A helper class to get the gridview with populated field items
 * Created by its me on 02-Jan-18.
 */
fun getGridLayoutForFieldItem(context: Context, fields: List<FieldItem>): GridLayout {
    val gridLayout = createGridLayout(context)

    var index = 0
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

        // print field name
        val textViewFieldName = TextView(context, null, R.style.LabelStyle)
        textViewFieldName.text = String.format("%s:", field.fieldName)
        gridLayout.addView(textViewFieldName, index++)

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

        gridLayout.addView(fieldValue, index++)
    }

    return gridLayout
}

private fun createGridLayout(context: Context): GridLayout {

    val gridLayout = GridLayout(context)
    gridLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    gridLayout.columnCount = 2
    gridLayout.isColumnOrderPreserved = true

    return gridLayout
}