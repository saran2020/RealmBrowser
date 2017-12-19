package com.github.saran2020.realmbrowser2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.realm.Realm
import io.realm.RealmList

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
                var listId: Long = 0
                var singleItemId = 0

                for (i in 0 until DATA_SIZE) {
                    var listItems: RealmList<RealmListItem> = RealmList()

                    for (id in listId until (listId + 5)) {
                        listItems.add(RealmListItem(id, "List item with Id $listId"))
                    }

                    var singleItem = RealmSingleItem(singleItemId.toLong(), "Single item with Id $singleItemId")

                    list.add(MyRealmTestObject(i.toLong(), "MyRealmTestObject with id $i", listItems, singleItem))

                    singleItemId++
                    listId += 5
                }

                it.executeTransaction { realm -> realm.insert(list) }

                Log.d(TAG, "Completed..")

            }
        }
    }
}
