package com.github.saran2020.realmbrowser

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import io.realm.Realm

/**
 * Created by Saran Sankaran on 11/9/17.
 */
class MainActivity : AppCompatActivity() {

    // const
    private val TAG = MainActivity::class.java.simpleName

    // global var
    lateinit var realm: Realm
    private lateinit var recyclerQueryAdapter: QueryAdapter
    private lateinit var recyclerFindAdapter: FindAdapter

    //views
    private lateinit var editClassName: EditText
    private lateinit var imageAddQuery: ImageView
    private lateinit var recyclerQueryContent: RecyclerView
    private lateinit var recyclerFindContent: RecyclerView
    private lateinit var textQuery: TextView
    private lateinit var buttonFetch: Button

    // override methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_library)

        realm = Realm.getDefaultInstance()

        // views
        editClassName = findViewById(R.id.editClassName_library)
        imageAddQuery = findViewById(R.id.queryAdd_library)
        recyclerQueryContent = findViewById(R.id.queryRecycler_library)
        recyclerFindContent = findViewById(R.id.findRecycler_library)
        textQuery = findViewById(R.id.query_library)
        buttonFetch = findViewById(R.id.buttonFetch_library)

        imageAddQuery.setOnClickListener(onAddQueryClickListener)
        buttonFetch.setOnClickListener(onFetchClickListener)

        // query recycler
        recyclerQueryAdapter = QueryAdapter()
        recyclerQueryContent.layoutManager = FlowLayoutManager()
        recyclerQueryContent.adapter = recyclerQueryAdapter

        // find recycler
        recyclerFindAdapter = FindAdapter(getFindItems())
        recyclerFindContent.layoutManager = FlowLayoutManager()
        recyclerFindContent.adapter = recyclerFindAdapter

        editClassName.setOnClickListener {
            showModelPicker()
        }
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    // member functions
    private fun getFindItems(): List<String> {
        return listOf(
                FIND_ALL_STRING,
                FIND_FIRST_STRING)
    }

    private fun showModelPicker() {

        val classes = realm.configuration.realmObjectClasses
                .map { it.simpleName as CharSequence }
                .toTypedArray()


        val listener = DialogInterface.OnClickListener { _, which ->
            editClassName.setText(classes[which])
        }

        AlertDialog.Builder(this)
                .setTitle("Pick a Model")
                .setItems(classes, listener).show()
    }

    private val onAddQueryClickListener = View.OnClickListener {
        Toast.makeText(this@MainActivity, "Add Clicked", Toast.LENGTH_SHORT).show()
    }

    private var onFetchClickListener = View.OnClickListener {

        val className = editClassName.text.toString()
        if (className.isEmpty()) return@OnClickListener

        when (this@MainActivity.recyclerFindAdapter.selectedItem) {
            NO_ITEM_SELECTED -> this@MainActivity.recyclerFindAdapter.selectedItem
            FIND_ALL -> startResultActivity(this, className, FIND_ALL)
            FIND_FIRST -> startResultActivity(this, className, FIND_FIRST)
        }
    }
}