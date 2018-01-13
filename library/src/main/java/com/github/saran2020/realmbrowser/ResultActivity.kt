package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.github.saran2020.realmbrowser.model.ClassItem
import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName

    private lateinit var progressLoading: ProgressBar
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var linearResultHeader: LinearLayout
    private lateinit var recyclerResults: RecyclerView

    companion object {
        // TODO: Review if the find parm is required
        fun startActivity(context: Context, className: String, find: Byte) {

            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(EXTRA_CLASS_NAME, className)
            intent.putExtra(EXTRA_FIND, find)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_library)

        progressLoading = findViewById(R.id.progress_loading_library)
        scrollView = findViewById(R.id.scroll_view)
        linearResultHeader = findViewById(R.id.headers)
        recyclerResults = findViewById(R.id.recycler_results_library)
    }

    override fun onStart() {
        super.onStart()

        showLoader(true)
        GetFields()
                .from(intent.extras)
                .find(callback)
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

    private val callback: FieldsTaskCompleteCallback = object : FieldsTaskCompleteCallback() {

        override fun onFetchComplete(fields: List<ClassItem>?) {
            super.onFetchComplete(fields)

            if (fields == null) {
                return
            }

            linearResultHeader.removeAllViews()
            populateHeader(this@ResultActivity, linearResultHeader, fields[0])

            recyclerResults.layoutManager =
                    LinearLayoutManager(this@ResultActivity, LinearLayoutManager.VERTICAL, false)
            recyclerResults.adapter = RecyclerAdapter(this@ResultActivity, fields)
            showLoader(false)
        }
    }
}