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
import kotlin.collections.ArrayList

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
        addItemsToLayout(fieldsList, 0)
        showLoader(false)
    }

    private fun addItemsToLayout(fields: List<FieldItem>, rowNum: Int) {

        var row = rowNum

        val fieldNameSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
        val fieldValueSpec = GridLayout.spec(GridLayout.UNDEFINED, 3)

        for (field in fields) {

            val type = field.type

            if (field.type == Constants.TYPE_REALM_LIST) {

                val fieldItems = field.value as List<List<FieldItem>>

                for (fieldItem in fieldItems) addItemsToLayout(fieldItem, row)
                continue

            } else if (field.type == Constants.TYPE_REALM_OBJECT) {
                addItemsToLayout(field.value as List<FieldItem>, row)
                continue
            }

            val fieldValue: String = when (type) {
                Constants.TYPE_BOOLEAN -> (field.value as Boolean).toString()
                Constants.TYPE_BYTE -> (field.value as Byte).toString()
                Constants.TYPE_CHAR -> (field.value as Char).toString()
                Constants.TYPE_SHORT -> (field.value as Short).toString()
                Constants.TYPE_INT -> (field.value as Int).toString()
                Constants.TYPE_LONG -> (field.value as Long).toString()
                Constants.TYPE_FLOAT -> (field.value as Float).toString()
                Constants.TYPE_DOUBLE -> (field.value as Double).toString()
                Constants.TYPE_STRING -> (field.value as String)
                else -> "Some error occurred"
            }

            val rowNameSpec = GridLayout.spec(row, 1)
            val rowValueSpec = GridLayout.spec(row, 1)

            val fieldNameParms = GridLayout.LayoutParams(rowNameSpec, fieldNameSpec)
            val fieldValueParms = GridLayout.LayoutParams(rowValueSpec, fieldValueSpec)

            var textViewFieldName = TextView(this@ResultActivity, null, R.style.LabelStyle)
            textViewFieldName.text = field.fieldName
//            textViewFieldName.layoutParams = ViewGroup.LayoutParams(100, 100)

            var textViewFieldValue = TextView(this@ResultActivity, null, R.style.ValueStyle)
            textViewFieldValue.text = fieldValue

            girdLayout.addView(textViewFieldName, fieldNameParms)
            girdLayout.addView(textViewFieldValue, fieldValueParms)

            row++
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

    private fun getDataFromObject(findResult: RealmModel?): ArrayList<FieldItem> {

        if (findResult == null) return ArrayList<FieldItem>()

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

            if (method == null) return ArrayList<FieldItem>()

            val type = method.returnType
            val name = fieldName
            var data = method.invoke(findResult)

            // If the field is an instance of Realm model, recursively find items inside that.
            if (type.superclass == RealmObject::class.java) {

                data = getDataFromObject(data as RealmModel)
            } else if (type.isAssignableFrom(RealmList::class.java)) {

                val objects = data as RealmList<RealmModel>
                val itemsList: MutableList<List<FieldItem>> = arrayListOf()

                for (singleObject in objects) {

                    val itemList = getDataFromObject(singleObject as RealmModel)
                    itemsList.add(itemList)
                }

                data = itemsList
            }

            val fieldItem = FieldItem(type, name, data)
            fieldList.add(fieldItem)
        }

        return ArrayList(fieldList)
    }

    // result item
    class FieldItem(dataType: Class<*>, var fieldName: String, val value: Any?) {

        var type = when {
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