package com.github.saran2020.realmbrowser2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.github.saran2020.realmbrowser.RealmBrowser
import com.github.saran2020.realmbrowser2.model.Senator
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import io.realm.RealmList
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


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
            FeedDataTask(textStatus, inputStream).execute()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.default_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if (item?.itemId == R.id.open_realm_browser) {
            RealmBrowser.start(this)
        }

        return true
    }

    override fun onDestroy() {

        if (!realm.isClosed)
            realm.close()

        super.onDestroy()
    }

    class FeedDataTask(textStatus: TextView, private val inputStream: InputStream)
        : AsyncTask<Unit, Unit, Boolean>() {

        private val textView: WeakReference<TextView> = WeakReference(textStatus)

        override fun doInBackground(vararg params: Unit?): Boolean {
            val gson = GsonBuilder()
                    .registerTypeAdapter(Date::class.java, DateSerializers())
                    .create()

            val listType = object : TypeToken<RealmList<Senator>>() {
            }.type
            val inputStreamReader = InputStreamReader(inputStream)

            val models = gson.fromJson<RealmList<Senator>>(inputStreamReader, listType)
            Log.d("buggy_bug", models.size.toString())
            Realm.getDefaultInstance().use {
                it.executeTransaction { realm -> realm.insert(models) }
            }

            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            val textView = this.textView.get()
            textView?.text = "Completed.."
        }
    }

    class DateSerializers : JsonDeserializer<Date> {

        private val dateFormatter: DateFormat

        init {
            dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
            val dateString = json?.asJsonPrimitive?.asString
            return dateFormatter.parse(dateString)
        }
    }
}
