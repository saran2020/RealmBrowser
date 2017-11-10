package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader
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
    private val LOADER_ID = 100

    lateinit var progressLoading: ProgressBar
    lateinit var textResult: TextView

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

        // init Loader
        supportLoaderManager.initLoader(LOADER_ID, intent.extras, AsyncTaskLoaderCallbacks()).forceLoad()
    }


    /*
     * inner classes
     * loader callback for the task
     */
    inner class AsyncTaskLoaderCallbacks : android.support.v4.app.LoaderManager.LoaderCallbacks<String> {
        override fun onCreateLoader(id: Int, args: Bundle?): android.support.v4.content.Loader<String> {

            showLoader(true)
            return FetchDataTask(this@ResultActivity, intent.extras)
        }

        override fun onLoadFinished(loader: android.support.v4.content.Loader<String>?, data: String?) {

            showLoader(false)
            textResult.text = data
        }

        override fun onLoaderReset(loader: android.support.v4.content.Loader<String>?) {}

        /**
         * Show Loader or not
         */
        private fun showLoader(show: Boolean) {

            if (show) {
                progressLoading.visibility = View.VISIBLE
                textResult.visibility = View.INVISIBLE
            } else {
                progressLoading.visibility = View.INVISIBLE
                textResult.visibility = View.VISIBLE
            }
        }
    }

    // AsyncTask
    class FetchDataTask(context: Context, var bundle: Bundle) : AsyncTaskLoader<String>(context) {

        override fun loadInBackground(): String {
            var returnText = "An error  occurred"

            try {
                Realm.getDefaultInstance().use {
                    var query = getRealmQuery(bundle, it)
                    var result = query.findAll()
                    returnText = "${result.size} items found"
                }
            } catch (e: ClassNotFoundException) {

                //TODO: class not found (Show appropriate message)
                returnText = "Class not Found"
                e.printStackTrace()
            } catch (e: NullPointerException) {

                //TODO: Is not Realm Model class (Show appropriate message)
                returnText = "Not an instance of RealmObject"
                e.printStackTrace()
            }

            return returnText
        }

        private fun getRealmQuery(bundle: Bundle, realm: Realm): RealmQuery<RealmModel> {

            var fullClassName = bundle.getString(Constants.EXTRA_CLASS_NAME)
            var className = Class.forName(fullClassName) as Class<RealmModel>
            var query = realm.where(className)

            return query
        }
    }
}