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
                Log.d(TAG, "Completed..")

            }
        }
    }
}
