package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import java.lang.reflect.Method
import java.util.*

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class ResultActivity : AppCompatActivity() {

    private val TAG = ResultActivity::class.java.simpleName
    private val LOADER_ID = 100

    lateinit var progressLoading: ProgressBar
    lateinit var girdLayout: GridLayout

    companion object {
        public fun startActivity(context: Context, className: String, find: Byte) {

            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(Constants.EXTRA_CLASS_NAME, className)
            intent.putExtra(Constants.EXTRA_FIND, find)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_library)

        progressLoading = findViewById(R.id.progress_loading)
        girdLayout = findViewById(R.id.grid_layout)
    }

    override fun onStart() {
        super.onStart()

        showLoader(true)
        val fieldsList = fetchFields()
        addItemsToLayout(fieldsList)
        showLoader(false)
    }

    private fun addItemsToLayout(datas: List<FieldItem>) {

        val fieldNameSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
        val fieldValueSpec = GridLayout.spec(GridLayout.UNDEFINED, 3)

        for ((row, data) in datas.withIndex()) {

            if (data.getType() == Constants.TYPE_REALM_OBJECT) addItemsToLayout(data.value as List<FieldItem>)

            val rowNameSpec = GridLayout.spec(row, 1)
            val rowValueSpec = GridLayout.spec(row, 1)

            val fieldNameParms = GridLayout.LayoutParams(rowNameSpec, fieldNameSpec)
            val fieldValueParms = GridLayout.LayoutParams(rowValueSpec, fieldValueSpec)

            var textViewFieldName = TextView(this@ResultActivity, null, R.style.LabelStyle)
            textViewFieldName.text = data.fieldName
//            textViewFieldName.layoutParams = ViewGroup.LayoutParams(100, 100)

            var textViewFieldValue = TextView(this@ResultActivity, null, R.style.ValueStyle)
            textViewFieldValue.text = data.getValue()

            girdLayout.addView(textViewFieldName, fieldNameParms)
            girdLayout.addView(textViewFieldValue, fieldValueParms)

        }
    }

    /**
     * Show loading icon
     */
    private fun showLoader(show: Boolean) {

        if (show) {
            progressLoading.visibility = View.VISIBLE
            girdLayout.visibility = View.INVISIBLE
        } else {
            progressLoading.visibility = View.INVISIBLE
            girdLayout.visibility = View.VISIBLE
        }
    }

    private fun fetchFields(): List<FieldItem> {
        var returnItem: List<FieldItem> = Collections.emptyList()

        try {
            Realm.getDefaultInstance().use {
                var result = findResult(it, intent.extras)

                if (result != null) returnItem = result
            }
        } catch (e: ClassNotFoundException) {

            //TODO: class not found (Show appropriate message)
            e.printStackTrace()
        } catch (e: NullPointerException) {

            //TODO: Is not Realm Model class (Show appropriate message)
            e.printStackTrace()
        }

        return returnItem
    }

    private fun findResult(realm: Realm, bundle: Bundle): List<FieldItem>? {

        var fullClassName = bundle.getString(Constants.EXTRA_CLASS_NAME)
        var className = Class.forName(fullClassName) as Class<RealmModel>
        var query = realm.where(className)

        var findResult = query.findFirst()

        if (findResult == null)
            return null

        return getDataFromObject(findResult)
    }

    private fun getDataFromObject(findResult: RealmModel?): MutableList<FieldItem> {

        if (findResult == null) return mutableListOf<FieldItem>()

        val resultObjectClass = findResult::class.java

        // TODO: handle NoSuchMethodException, SecurityException in classItem.getMethod()
        var fieldList = mutableListOf<FieldItem>()
        var fieldNames = resultObjectClass.getMethod("getFieldNames")
                .invoke(findResult) as List<String>
        var methods = resultObjectClass.methods

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
            var data = method.invoke(findResult)

            // If the field is an instance of Realm model, recursively find items inside that.
            if (type.superclass == RealmObject::class.java) {

                data = getDataFromObject(data as RealmModel)
            }

            val fieldItem = FieldItem(type, name, data)
            fieldList.add(fieldItem)
        }

        return fieldList.toMutableList();
    }

    // result item
    class FieldItem(dataType: Class<*>, var fieldName: String, val value: Any?) {

        private var type = when {
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

        fun getType() = type

        public fun getValue(): String {

            return when (type) {
                Constants.TYPE_BOOLEAN -> (value as Boolean).toString()
                Constants.TYPE_BYTE -> (value as Byte).toString()
                Constants.TYPE_CHAR -> (value as Char).toString()
                Constants.TYPE_SHORT -> (value as Short).toString()
                Constants.TYPE_INT -> (value as Int).toString()
                Constants.TYPE_LONG -> (value as Long).toString()
                Constants.TYPE_FLOAT -> (value as Float).toString()
                Constants.TYPE_DOUBLE -> (value as Double).toString()
                Constants.TYPE_STRING -> (value as String)
                Constants.TYPE_REALM_LIST -> (value as RealmList<*>).toString()
                Constants.TYPE_REALM_OBJECT -> (value as List<FieldItem>).toString()
                else -> "Some error occurred"
            }
        }
    }
}