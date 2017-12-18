package com.github.saran2020.realmbrowser2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.realm.Realm

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivitySampleApp"
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()
        var result = realm.where(MyRealmTestObject::class.java).findAll()

        if (result.size == 0)
            FeedDataTask(TAG).execute()

        Log.d(TAG, "Size: ${result.size}")
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    class FeedDataTask(val TAG: String) : AsyncTask<Unit, Unit, Unit>() {

        private val DATA_SIZE = 100000

        override fun doInBackground(vararg params: Unit?) {
            Realm.getDefaultInstance().use {

                var list: ArrayList<MyRealmTestObject> = ArrayList<MyRealmTestObject>(DATA_SIZE)

                (0..(DATA_SIZE - 1)).mapTo(list) { MyRealmTestObject(it.toLong(), "Test String $it") }

                it.executeTransaction { realm -> realm.insert(list) }

                Log.d(TAG, "Completed..")

            }
        }
    }
}
