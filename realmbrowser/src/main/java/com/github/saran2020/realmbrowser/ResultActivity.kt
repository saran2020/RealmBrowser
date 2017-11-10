package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName

    lateinit var progressLoading: ProgressBar
    lateinit var textResult: TextView

    private var task: FetchDataTask? = null


    companion object {
        public fun startActivity(context: Context, className: String) {

            var intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(Constants.EXTRA_CLASS_NAME, className)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_library)

        progressLoading = findViewById(R.id.progress_loading)
        textResult = findViewById(R.id.text_result)
    }

    override fun onStart() {
        super.onStart()
        task = FetchDataTask(intent.extras)
        task?.execute()
    }

    override fun onDestroy() {
        super.onDestroy()

        task?.cancel(true)
    }

    inner class FetchDataTask(var bundle: Bundle) : AsyncTask<Unit, Unit, String>() {


        override fun onPreExecute() {
            super.onPreExecute()
            progressLoading.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Unit?): String? {

            var returnText = "An error  occurred"

            try {
                Realm.getDefaultInstance().use {
                    var query = getRealmQuery(bundle, it)
                    var result = query.findAll()
                    returnText = "${result.size} items found"
                }
            } catch (e: ClassNotFoundException) {

                //TODO: class not found (Show appropriate message)
                e.printStackTrace()
            } catch (e: NullPointerException) {

                //TODO: Is not Realm Model class (Show appropriate message)
                e.printStackTrace()
            }

            return if (!isCancelled)
                returnText
            else
                null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null)
                return

            progressLoading.visibility = View.INVISIBLE
            textResult.text = result
        }

        private fun getRealmQuery(bundle: Bundle, realm: Realm): RealmQuery<RealmModel> {

            var fullClassName = bundle.getString(Constants.EXTRA_CLASS_NAME)
            var className = Class.forName(fullClassName) as Class<RealmModel>
            var query = realm.where(className)

            return query
        }
    }
}