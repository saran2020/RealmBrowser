package com.github.saran2020.realmbrowser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmModel

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
            Toast.makeText(this, "Received Click", Toast.LENGTH_SHORT).show()
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

        try {

            var fullClassName = packageName.append(className).toString()
            var classTest = Class.forName(fullClassName) as Class<RealmModel>
            var test = realm.where<RealmModel>(classTest).findAll()
            Log.d(TAG, "Size: " + test.size)
        } catch (e: ClassNotFoundException) {

            //TODO: class not found (Show appropriate message)
            e.printStackTrace()
        } catch (e: NullPointerException) {

            //TODO: Is not Realm Model class (Show appropriate message)
            e.printStackTrace()
        }

    }
}