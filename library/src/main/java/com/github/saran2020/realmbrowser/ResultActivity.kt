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
import io.realm.RealmObject
import io.realm.RealmQuery
import java.lang.reflect.Modifier

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

                    returnText = if (result == null)
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
            var resultAny = query.findFirst()

            if (resultAny == null)
                return null

            var fieldList = mutableListOf<FieldItem>()
            var methods = resultAny::class.java.methods

            for (method in methods) {

                val isPublic = method.modifiers and Modifier.PUBLIC != 0

                // Only fetch getters
                if (method.name.startsWith(Constants.GETTER_PREFIX, true) &&
                        method.parameterTypes.size == 0 && isPublic) {

                    val type = method.returnType
                    val name = method.name.removePrefix(Constants.GETTER_PREFIX)
                    val data = method.invoke(resultAny)

                    val field = FieldItem(type, name, data)

                    fieldList.add(field)

                }

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

        private val type: Byte = when (dataType) {
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaObjectType -> Constants.BOOLEAN

            Byte::class.javaPrimitiveType,
            Byte::class.javaObjectType -> Constants.BYTE

            Char::class.javaPrimitiveType,
            Char::class.javaObjectType -> Constants.CHAR

            Short::class.javaPrimitiveType,
            Short::class.javaObjectType -> Constants.SHORT

            Int::class.javaPrimitiveType,
            Int::class.javaObjectType -> Constants.INT

            Long::class.javaPrimitiveType,
            Long::class.javaObjectType -> Constants.LONG

            Float::class.javaPrimitiveType,
            Float::class.javaObjectType -> Constants.FLOAT

            Double::class.javaPrimitiveType,
            Double::class.javaObjectType -> Constants.DOUBLE

            String::class.java -> Constants.STRING

            RealmObject::class.javaPrimitiveType,
            RealmObject::class.javaObjectType -> Constants.REALM_OBJECt

            else -> Constants.NO_DATA_TYPE
        }

    }
}