package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName

    private lateinit var progressLoading: ProgressBar
    private lateinit var scrollView: HorizontalScrollView

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
        scrollView = findViewById(R.id.scroll_view)
    }

    override fun onStart() {
        super.onStart()

        showLoader(true)
        val fieldsList = GetFields()
                .from(intent.extras).findFirst()

        val view = addItemsToLayout(fieldsList)
        scrollView.addView(view)
        showLoader(false)

    }

    private fun addItemsToLayout(fields: List<FieldItem>): GridLayout {

        val gridLayout = createGridLayout()

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
            val textViewFieldName = TextView(this@ResultActivity, null, R.style.LabelStyle)
            textViewFieldName.text = String.format("%s:", field.fieldName)
            gridLayout.addView(textViewFieldName, index++)

            val fieldValue: View

            // TODO: Print realm list
            // if type is realm object, only print the field name and then  recursively
            // call the function again to print data inside
            if (field.type == TYPE_REALM_OBJECT) {

                @Suppress("UNCHECKED_CAST")
                fieldValue = addItemsToLayout(field.value as List<FieldItem>)
            } else {

                // print field value
                val textViewFieldValue = TextView(this@ResultActivity, null, R.style.ValueStyle)
                textViewFieldValue.text = fieldData

                fieldValue = textViewFieldValue
            }

            gridLayout.addView(fieldValue, index++)
        }

        return gridLayout
    }

    private fun createGridLayout(): GridLayout {

        val gridLayout = GridLayout(this)
        gridLayout.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        gridLayout.columnCount = 2
        gridLayout.isColumnOrderPreserved = true

        return gridLayout
    }

    /**
     * Show loading icon
     */
    private fun showLoader(show: Boolean) {

        if (show) {
            progressLoading.visibility = View.VISIBLE
            scrollView.visibility = View.INVISIBLE
        } else {
            progressLoading.visibility = View.INVISIBLE
            scrollView.visibility = View.VISIBLE
        }
    }
}