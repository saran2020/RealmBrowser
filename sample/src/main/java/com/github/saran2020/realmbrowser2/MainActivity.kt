package com.github.saran2020.realmbrowser2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.github.saran2020.realmbrowser2.model.Senator
import io.realm.Realm
import java.io.InputStream
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivitySampleApp"
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textStatus = findViewById<TextView>(R.id.text_status)

        realm = Realm.getDefaultInstance()

        val result = realm.where(Senator::class.java).findAll().size

        if (result > 0) {
            textStatus.text = "Completed.."
            Log.d(TAG, "Completed..")
        } else {
            val inputStream = resources.openRawResource(R.raw.data_source)
            FeedDataTask(textStatus, inputStream, TAG).execute()
        }
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    class FeedDataTask(textStatus: TextView, val inputStream: InputStream, val TAG: String) : AsyncTask<Unit, Unit, Boolean>() {

        val textView: WeakReference<TextView> = WeakReference(textStatus)

        override fun doInBackground(vararg params: Unit?): Boolean {
            Realm.getDefaultInstance().use {

                it.executeTransaction { realm -> realm.createAllFromJson(Senator::class.java, inputStream) }
            }

            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            val textView = this.textView.get()
            textView?.text = "Completed.."
        }
    }
}
