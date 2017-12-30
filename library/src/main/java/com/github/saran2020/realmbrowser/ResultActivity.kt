package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName

    lateinit var progressLoading: ProgressBar
    lateinit var girdLayout: GridLayout

    companion object {
        public fun startActivity(context: Context, className: String, find: Byte) {

            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(EXTRA_CLASS_NAME, className)
            intent.putExtra(EXTRA_FIND, find)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_library)

        progressLoading = findViewById(R.id.progress_loading)
        girdLayout = findViewById(R.id.grid_layout)
    }

    override fun onStart() {
        super.onStart()

        showLoader(true)
        val fieldsList = GetFields()
                .from(intent.extras).findFirst()

        addItemsToLayout(fieldsList, 0)
        showLoader(false)
    }

    private fun addItemsToLayout(fields: List<FieldItem>, rowNum: Int) {

        var row = rowNum

        val fieldNameSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
        val fieldValueSpec = GridLayout.spec(GridLayout.UNDEFINED, 3)

        // TODO: handle null values of value
        for (field in fields) {

            val type = field.type
            var fieldValue: String = ""

            if (field.value == null) {
                fieldValue = "null"

            } else {

                if (field.type == TYPE_REALM_LIST) {

                    val fieldItems = field.value as List<List<FieldItem>>
                    fieldItems.forEach { fieldItem -> addItemsToLayout(fieldItem, row) }
                    continue

                } else if (field.type == TYPE_REALM_OBJECT) {
                    addItemsToLayout(field.value as List<FieldItem>, row)
                    continue
                }

                fieldValue = when (type) {
                    TYPE_BOOLEAN -> (field.value as Boolean).toString()
                    TYPE_BYTE -> (field.value as Byte).toString()
                    TYPE_CHAR -> (field.value as Char).toString()
                    TYPE_SHORT -> (field.value as Short).toString()
                    TYPE_INT -> (field.value as Int).toString()
                    TYPE_LONG -> (field.value as Long).toString()
                    TYPE_FLOAT -> (field.value as Float).toString()
                    TYPE_DOUBLE -> (field.value as Double).toString()
                    TYPE_STRING -> (field.value as String)
                    else -> "Some error occurred"
                }

                if (fieldValue.equals("Some error occurred")) {
                    Log.d(TAG, "addItemsToLayout: FieldName = ${field.fieldName} Type = ${field.type}")
                }
            }

            if (type == TYPE_STRING && fieldValue.isEmpty()) fieldValue = "\"\""

            val rowNameSpec = GridLayout.spec(row, 1)
            val rowValueSpec = GridLayout.spec(row, 1)

            val fieldNameParms = GridLayout.LayoutParams(rowNameSpec, fieldNameSpec)
            val fieldValueParms = GridLayout.LayoutParams(rowValueSpec, fieldValueSpec)

            val textViewFieldName = TextView(this@ResultActivity, null, R.style.LabelStyle)
            textViewFieldName.text = field.fieldName
//            textViewFieldName.layoutParams = ViewGroup.LayoutParams(100, 100)

            val textViewFieldValue = TextView(this@ResultActivity, null, R.style.ValueStyle)
            textViewFieldValue.text = fieldValue

            girdLayout.addView(textViewFieldName, fieldNameParms)
            girdLayout.addView(textViewFieldValue, fieldValueParms)

            row++
        }
    }

    /**
     * Show loading icon
     */
    private fun showLoader(show: Boolean) {

        if (show) {
            progressLoading.visibility = View.VISIBLE
            girdLayout.visibility = View.INVISIBLE
        } else {
            progressLoading.visibility = View.INVISIBLE
            girdLayout.visibility = View.VISIBLE
        }
    }
}