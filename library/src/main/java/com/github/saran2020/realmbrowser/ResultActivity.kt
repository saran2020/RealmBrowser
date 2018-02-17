package com.github.saran2020.realmbrowser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.github.saran2020.realmbrowser.data.FieldsTaskCompleteCallback
import com.github.saran2020.realmbrowser.data.GetFields
import com.github.saran2020.realmbrowser.data.model.ClassItem
import com.github.saran2020.realmbrowser.data.model.DisplayResult
import com.github.saran2020.realmbrowser.data.model.NativeListType
import io.realm.RealmFieldType
import io.realm.internal.Property

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName

    private lateinit var progressLoading: ProgressBar
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var linearResultHeader: LinearLayout
    private lateinit var recyclerResults: RecyclerView

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

    private val callback: FieldsTaskCompleteCallback = object : FieldsTaskCompleteCallback {


        override fun onFetchComplete(result: DisplayResult) {

            showLoader(false)

            if (result.type == RESULT_TYPE_EMPTY.toInt()) {
                // TODO: Handle
            } else {

                linearResultHeader.removeAllViews()

                // TODO: instead of using RESULT_TYPE_OBJECT declared in library, use the one provided in RealmFieldType
                if (result.type == RESULT_TYPE_OBJECT.toInt()
                        || result.type == RESULT_TYPE_REALM_RESULT.toInt()
                        || result.type == RealmFieldType.LIST.nativeValue) {

                    // result will be list of class item
                    val fields = result.result as List<ClassItem>

                    populateHeader(this@ResultActivity, linearResultHeader, fields[0], resources.getInteger(R.integer.ITEMS_PER_SCREEN_OBJECT))

                    recyclerResults.layoutManager =
                            LinearLayoutManager(this@ResultActivity, LinearLayoutManager.VERTICAL, false)
                    recyclerResults.adapter = RecyclerAdapter(this@ResultActivity, fields)

                } else {

                    val fields = (result.result as NativeListType).fieldValue

                    recyclerResults.layoutManager =
                            LinearLayoutManager(this@ResultActivity, LinearLayoutManager.VERTICAL, false)

                    // Property.TYPE_ARRAY is the offset for list of primitive list eg if int = 1 list of int is Property.TYPE_ARRAY + VALUE_OF_INT
                    recyclerResults.adapter = RecyclerNativeAdapter(this@ResultActivity, fields, Property.TYPE_ARRAY - result.type)
                }
            }
        }
    }
}