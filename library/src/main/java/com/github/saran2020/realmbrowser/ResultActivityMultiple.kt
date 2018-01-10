package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivityMultiple : AppCompatActivity() {

    private val TAG = ResultActivityMultiple::class.java.simpleName

    private lateinit var progressLoading: ProgressBar
    private lateinit var recyclerResults: RecyclerView

    companion object {
        public fun startActivity(context: Context, className: String, find: Byte) {

            val intent = Intent(context, ResultActivityMultiple::class.java)
            intent.putExtra(EXTRA_CLASS_NAME, className)
            intent.putExtra(EXTRA_FIND, find)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_library)

        progressLoading = findViewById(R.id.progress_loading_library)
        recyclerResults = findViewById(R.id.recycler_results_library)
    }

    override fun onStart() {
        super.onStart()

        showLoader(true)
        val fieldsList = GetFields()
                .from(intent.extras).findFirst()

        recyclerResults.layoutManager =
                LinearLayoutManager(this@ResultActivityMultiple, LinearLayoutManager.VERTICAL, false)
        recyclerResults.adapter = RecyclerAdapter()
        showLoader(false)
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