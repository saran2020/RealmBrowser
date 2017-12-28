package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import java.lang.reflect.Method
import java.util.*

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName
    private val LOADER_ID = 100

    lateinit var progressLoading: ProgressBar
    lateinit var girdLayout: GridLayout

    companion object {
        public fun startActivity(context: Context, className: String, find: Byte) {

            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(Constants.EXTRA_CLASS_NAME, className)
            intent.putExtra(Constants.EXTRA_FIND, find)
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
        val fieldsList = fetchFields()
        addItemsToLayout(fieldsList, 0)
        showLoader(false)
    }

    private fun addItemsToLayout(fields: List<FieldItem>, rowNum: Int) {

        var row = rowNum

        val fieldNameSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
        val fieldValueSpec = GridLayout.spec(GridLayout.UNDEFINED, 3)

        for (field in fields) {

            val type = field.type

            if (field.type == Constants.TYPE_REALM_LIST) {

                val fieldItems = field.value as List<List<FieldItem>>
                fieldItems.forEach { fieldItem -> addItemsToLayout(fieldItem, row) }
                continue

            } else if (field.type == Constants.TYPE_REALM_OBJECT) {
                addItemsToLayout(field.value as List<FieldItem>, row)
                continue
            }

            var fieldValue: String = if (field.value == null) {
                "null"
            } else {
                when (type) {
                    Constants.TYPE_BOOLEAN -> (field.value as Boolean).toString()
                    Constants.TYPE_BYTE -> (field.value as Byte).toString()
                    Constants.TYPE_CHAR -> (field.value as Char).toString()
                    Constants.TYPE_SHORT -> (field.value as Short).toString()
                    Constants.TYPE_INT -> (field.value as Int).toString()
                    Constants.TYPE_LONG -> (field.value as Long).toString()
                    Constants.TYPE_FLOAT -> (field.value as Float).toString()
                    Constants.TYPE_DOUBLE -> (field.value as Double).toString()
                    Constants.TYPE_STRING -> (field.value as String)
                    else -> "Some error occurred"
                }
            }

            if (type == Constants.TYPE_STRING && fieldValue.isEmpty()) fieldValue = "\"\""

            val rowNameSpec = GridLayout.spec(row, 1)
            val rowValueSpec = GridLayout.spec(row, 1)

            val fieldNameParms = GridLayout.LayoutParams(rowNameSpec, fieldNameSpec)
            val fieldValueParms = GridLayout.LayoutParams(rowValueSpec, fieldValueSpec)

            var textViewFieldName = TextView(this@ResultActivity, null, R.style.LabelStyle)
            textViewFieldName.text = field.fieldName
//            textViewFieldName.layoutParams = ViewGroup.LayoutParams(100, 100)

            var textViewFieldValue = TextView(this@ResultActivity, null, R.style.ValueStyle)
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