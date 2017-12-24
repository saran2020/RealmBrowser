package com.github.saran2020.realmbrowser2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.saran2020.realmbrowser2.model.Senator
import io.realm.Realm
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivitySampleApp"
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        val result = realm.where(Senator::class.java).findAll().size

        if (result > 0) {
            Log.d(TAG, "Completed..")
        } else {
            val inputStream = resources.openRawResource(R.raw.data_source)
            FeedDataTask(TAG, inputStream).execute()
        }
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    class FeedDataTask(val TAG: String, val inputStream: InputStream) : AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg params: Unit?) {
            Realm.getDefaultInstance().use {

                it.executeTransaction { realm -> realm.createAllFromJson(Senator::class.java, inputStream) }
            }

            Log.d(TAG, "Completed..")
        }
    }
}
