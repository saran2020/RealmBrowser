package com.github.saran2020.realmbrowser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import io.realm.Realm

/**
 * Created by Saran Sankaran on 11/9/17.
 */
class MainActivity : AppCompatActivity() {

    // const
    private val TAG = MainActivity::class.java.simpleName

    // global var
    lateinit var realm: Realm
    lateinit var buttonFetch: Button
    lateinit var editPackageName: EditText
    lateinit var editClassName: EditText

    // override methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_library)

        realm = Realm.getDefaultInstance()

        // views
        buttonFetch = findViewById(R.id.buttonFetch)
        editPackageName = findViewById(R.id.editPackageName)
        editClassName = findViewById(R.id.editClassName)

        buttonFetch.setOnClickListener {
            onFetchClick()
        }

    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }


    // Member methods
    private fun onFetchClick() {

        var packageName = editPackageName.text
        var className = editClassName.text

        var fullClassName = packageName.append(className).toString()

        ResultActivity.startActivity(this, fullClassName)

    }
}