package com.github.saran2020.realmbrowser

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.realm.Realm

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()
        var result = realm.where(MyRealmTestObject::class.java).findAll()

        if (result.size == 0)
            FeedDataTask().execute()

        Log.d("NO Tag", "Size: ${result.size}")
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    class FeedDataTask() : AsyncTask<Unit, Unit, Unit>() {

        private val DATA_SIZE = 100000

        override fun doInBackground(vararg params: Unit?) {
            Realm.getDefaultInstance().use {

                var list: ArrayList<MyRealmTestObject> = ArrayList<MyRealmTestObject>(DATA_SIZE)

                (0..(DATA_SIZE - 1)).mapTo(list) { MyRealmTestObject(it.toLong(), "Test String $it") }

                it.executeTransaction { realm -> realm.insert(list) }
            }
        }
    }
}
