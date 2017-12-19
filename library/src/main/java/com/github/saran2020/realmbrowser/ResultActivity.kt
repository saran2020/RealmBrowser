package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import io.realm.*
import java.lang.reflect.Method

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName
    private val LOADER_ID = 100

    lateinit var progressLoading: ProgressBar
    lateinit var textResult: TextView

    companion object {
        public fun startActivity(context: Context, className: String, find: Byte) {

            var intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(Constants.EXTRA_CLASS_NAME, className)
            intent.putExtra(Constants.EXTRA_FIND, find)
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

        val findType = intent.extras.getByte(Constants.EXTRA_FIND, Constants.FIND_NO);

        if (findType == Constants.FIND_FIRST)
            supportLoaderManager.initLoader(LOADER_ID, intent.extras, AsyncFindFirstTaskCallbacks())
                    .forceLoad()
        else if (findType == Constants.FIND_ALL)
            supportLoaderManager.initLoader(LOADER_ID, intent.extras, AsyncFindAllTaskCallbacks())
                    .forceLoad()
    }

    /*
     * inner classes
     * loader callback for findFirst()
     */
    inner class AsyncFindFirstTaskCallbacks : android.support.v4.app.LoaderManager.LoaderCallbacks<String> {
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
                    var result = findResult(it, bundle)

                    returnText = if (result == null || result.size == 0)
                        "Some error occured"
                    else
                        "One item found"
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

        private fun findResult(realm: Realm, bundle: Bundle): List<FieldItem>? {
            var query = getRealmQuery(realm, bundle)
            var findResult = query.findFirst()

            if (findResult == null)
                return null

            val classItem = findResult::class.java

            // TODO: handle NoSuchMethodException, SecurityException in classItem.getMethod()
            var fieldList = mutableListOf<FieldItem>()
            var fieldNames = classItem.getMethod("getFieldNames")
                    .invoke(findResult) as List<String>
            var methods = classItem.methods

            for (fieldName in fieldNames) {

                var method: Method? = null

                for (methodInstance in methods) {
                    if (methodInstance.name.startsWith("get")
                            && methodInstance.name.contains(fieldName, true)) {

                        method = methodInstance
                        break;
                    }
                }

                if (method == null) return mutableListOf<FieldItem>()

                val type = method.returnType
                val name = fieldName
                val data = method?.invoke(findResult)

                val fieldItem = FieldItem(type, name, data)
                fieldList.add(fieldItem)
            }

            return fieldList;
        }

        private fun getRealmQuery(realm: Realm, bundle: Bundle): RealmQuery<RealmModel> {

            var fullClassName = bundle.getString(Constants.EXTRA_CLASS_NAME)
            var className = Class.forName(fullClassName) as Class<RealmModel>
            var query = realm.where(className)

            return query
        }
    }


    /*
     * loader callback for findAll()
     */
    inner class AsyncFindAllTaskCallbacks : android.support.v4.app.LoaderManager.LoaderCallbacks<String> {
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

            //TODO: Show loader is common duplicates can be removed

            if (show) {
                progressLoading.visibility = View.VISIBLE
                textResult.visibility = View.INVISIBLE
            } else {
                progressLoading.visibility = View.INVISIBLE
                textResult.visibility = View.VISIBLE
            }
        }
    }

    // result item
    class FieldItem(dataType: Class<*>, val fieldName: String, val value: Any?) {

        private var type: Byte

        init {
            type = when {
                dataType.isAssignableFrom(Boolean::class.java) -> Constants.TYPE_BOOLEAN
                dataType.isAssignableFrom(Byte::class.java) -> Constants.TYPE_BYTE
                dataType.isAssignableFrom(Char::class.java) -> Constants.TYPE_CHAR
                dataType.isAssignableFrom(Short::class.java) -> Constants.TYPE_SHORT
                dataType.isAssignableFrom(Int::class.java) -> Constants.TYPE_INT
                dataType.isAssignableFrom(Long::class.java) -> Constants.TYPE_LONG
                dataType.isAssignableFrom(Float::class.java) -> Constants.TYPE_FLOAT
                dataType.isAssignableFrom(Double::class.java) -> Constants.TYPE_DOUBLE
                dataType.isAssignableFrom(String::class.java) -> Constants.TYPE_STRING
                dataType.isAssignableFrom(RealmList::class.java) -> Constants.TYPE_REALM_LIST
                dataType.superclass == RealmObject::class.java -> Constants.TYPE_REALM_OBJECT
                else -> Constants.NO_DATA_TYPE
            }
        }
    }
}