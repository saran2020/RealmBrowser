package com.github.saran2020.realmbrowser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import io.realm.Realm
import java.util.*

/**
 * Created by Saran Sankaran on 11/9/17.
 */
class MainActivity : AppCompatActivity() {

    // const
    private val TAG = MainActivity::class.java.simpleName

    // global var
    lateinit var appPackageName: String
    lateinit var realm: Realm
    private lateinit var recyclerQueryAdapter: QueryAdapter
    private lateinit var recyclerFindAdapter: FindAdapter

    //views
    private lateinit var editClassName: EditText
    private lateinit var textPackageName: TextView
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

        appPackageName = applicationContext.packageName

        // views
        editClassName = findViewById(R.id.editClassName_library)
        textPackageName = findViewById(R.id.packageName_library)
        imageAddQuery = findViewById(R.id.queryAdd_library)
        recyclerQueryContent = findViewById(R.id.queryRecycler_library)
        recyclerFindContent = findViewById(R.id.findRecycler_library)
        textQuery = findViewById(R.id.query_library)
        buttonFetch = findViewById(R.id.buttonFetch_library)

        editClassName.addTextChangedListener(textChangeListener)
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
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    // member functions
    private fun getFindItems(): List<String> {
        return listOf(
                Constants.FIND_ALL_STRING,
                Constants.FIND_FIRST_STRING)
    }

    // listeners
    private var textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s?.isEmpty() == true) textPackageName.text = ""
            textPackageName.text = String.format("%s.%s", appPackageName, s)
        }
    }

    private val onAddQueryClickListener = View.OnClickListener {
        Toast.makeText(this@MainActivity, "Add Clicked", Toast.LENGTH_SHORT).show()
    }

    private var onFetchClickListener = View.OnClickListener {

        var fullClassName = textPackageName.text.toString()
        if (fullClassName.isEmpty()) return@OnClickListener

        var find = if (this@MainActivity.recyclerFindAdapter.selectedItem != Constants.NO_ITEM_SELECTED)
            this@MainActivity.recyclerFindAdapter.selectedItem
        else
            return@OnClickListener

        ResultActivity.startActivity(this, fullClassName, find)
    }

    // inner class
    class QueryItem(var type: Byte, var queryList: LinkedList<String>, var queryValues: LinkedList<String>)
}