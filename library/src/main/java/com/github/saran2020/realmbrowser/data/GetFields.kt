package com.github.saran2020.realmbrowser.data

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.github.saran2020.realmbrowser.*
import com.github.saran2020.realmbrowser.Exception.GetterMethodNotFoundException
import com.github.saran2020.realmbrowser.Exception.PrimaryKeyItemNotFoundException
import com.github.saran2020.realmbrowser.Exception.SchemaNotFoundException
import com.github.saran2020.realmbrowser.data.model.*
import io.realm.*
import java.lang.reflect.Method

/**
 * Created by its me on 29-Dec-17.
 */
class GetFields {

    var callback: FieldsTaskCompleteCallback? = null
    lateinit var bundle: Bundle

    fun from(bundle: Bundle): GetFields {
        this.bundle = bundle
        return this
    }

    fun find(callback: FieldsTaskCompleteCallback) {
        this.callback = callback
        FetchFieldsTask(bundle, this.callback).execute()
    }

    class FetchFieldsTask(private val bundle: Bundle, private val callback: FieldsTaskCompleteCallback?) : AsyncTask<Void, Void, DisplayResult>() {

        lateinit var realm: Realm

        override fun doInBackground(vararg params: Void?): DisplayResult {

            realm = Realm.getDefaultInstance()

            val realmQuery = RealmQueryCreater(realm, bundle).getQuery()
            val queryResult = when (bundle.getByte(EXTRA_FIND)) {
                FIND_FIRST -> findFirst(realmQuery)
                FIND_ALL -> QueryResult(RESULT_TYPE_REALM_RESULT, realmQuery.findAll())
                else -> return DisplayResult(NO_DATA_TYPE.toInt(), null)
            }

            val result =
                    when (queryResult.resultType) {
                        RESULT_TYPE_OBJECT -> {
                            val classItem = findFields(queryResult.result as RealmObject)
                            DisplayResult(RESULT_TYPE_OBJECT.toInt(), arrayListOf(classItem))
                        }
                        RESULT_TYPE_REALM_RESULT -> {
                            findFields(queryResult.result as RealmResults<in RealmObject>)
                        }
                        RESULT_TYPE_LIST -> {
                            findFields(queryResult.result as RealmList<*>,
                                    bundle.getString(EXTRA_CLASS_NAME),
                                    bundle.getString(EXTRA_KEY_FIELD_NAME))
                        }
                        else -> DisplayResult(RESULT_TYPE_EMPTY.toInt(), null)
                    }

            return result
        }

        private fun findFirst(query: RealmQuery<*>): QueryResult {


            if (bundle.containsKey(EXTRA_PARENT_PRIMARY_KEY_VALUE)) {

                val primaryKeyFieldType = bundle.getInt(EXTRA_PARENT_PRIMARY_KEY_TYPE)
                val primaryKeyFieldName = bundle.getString(EXTRA_PARENT_PRIMARY_KEY_NAME)
                val primaryKeyValue = bundle.get(EXTRA_PARENT_PRIMARY_KEY_VALUE)

                when (primaryKeyFieldType) {
                    RealmFieldType.BOOLEAN.nativeValue -> query.equalTo(primaryKeyFieldName, primaryKeyValue as Boolean)
                    RealmFieldType.FLOAT.nativeValue -> query.equalTo(primaryKeyFieldName, primaryKeyValue as Float)
                    RealmFieldType.DOUBLE.nativeValue -> query.equalTo(primaryKeyFieldName, primaryKeyValue as Double)

                    RealmFieldType.STRING.nativeValue -> {
                        query.equalTo(primaryKeyFieldName, primaryKeyValue as String)
                    }

                    RealmFieldType.INTEGER.nativeValue -> {
                        when (primaryKeyValue) {
                            is Long -> query.equalTo(primaryKeyFieldName, primaryKeyValue)
                            is Int -> query.equalTo(primaryKeyFieldName, primaryKeyValue)
                            is Short -> query.equalTo(primaryKeyFieldName, primaryKeyValue)
                            is Byte -> query.equalTo(primaryKeyFieldName, primaryKeyValue)
                            else -> ERROR_TEXT
                        }
                    }
                }
            }

            val result = QueryResult(RESULT_TYPE_OBJECT, query.findFirst())

            if (bundle.containsKey(EXTRA_KEY_FIELD_GETTER_NAME) && result.result != null) {

                // TODO: handle
                if (result.result == null) {
                    return result
                }

                val getterMethodName = bundle.getString(EXTRA_KEY_FIELD_GETTER_NAME)
                val getterMethod = result.result!!::class.java.getMethod(getterMethodName)
                result.result = getterMethod.invoke(result.result)

                if (getterMethod.returnType.isAssignableFrom(RealmList::class.java)) {
                    result.resultType = RESULT_TYPE_LIST
                } else if (getterMethod.returnType.superclass == RealmObject::class.java) {
                    result.resultType = RESULT_TYPE_OBJECT
                }

            }

            return result
        }

        private fun findFields(realmInstance: RealmObject): ClassItem {
            val getters = findGetters(realmInstance)
            return findFieldsUsingGetter(realmInstance, getters)
        }

        private fun findFields(realmResult: RealmResults<in RealmObject>): DisplayResult {

            val getterMethods = findGetters(realmResult[0] as RealmObject)

            val result = arrayListOf<ClassItem>()
            realmResult.forEach {
                result.add(findFieldsUsingGetter(it as RealmObject, getterMethods))
            }

            return DisplayResult(RESULT_TYPE_REALM_RESULT.toInt(), result)
        }

        private fun findFields(realmList: RealmList<*>, className: String, fieldName: String): DisplayResult {

            val fieldType = realm.schema.get(className)?.getFieldType(fieldName)
            val type = fieldType?.nativeValue ?: RESULT_TYPE_EMPTY.toInt()

            val result: Any =
                    if (type == RealmFieldType.LIST.nativeValue) {
                        val classItems = arrayListOf<ClassItem>()
                        realmList.forEach {
                            classItems.add(findFields(it as RealmObject))
                        }
                    } else {
                        realmList.toList()
                    }

            return DisplayResult(type, result)
        }

        /**
         * Find all getter methods for declared fields. This getter methods
         * will be then invoked to find the values
         */
        private fun findGetters(objectInstance: RealmObject): Map<String, Method> {

            val map = mutableMapOf<String, Method>()
            val resultClass = objectInstance::class.java
            val classSchema = getClassSchema(objectInstance)

            // insert other fields
            val fieldNames = classSchema.fieldNames
            for (fieldName in fieldNames) {

                val getterMethod = findGetter(classSchema, resultClass, fieldName) ?:
                        throw GetterMethodNotFoundException("Getter method not found for field $fieldName. " +
                                "Getter method should start with get or is followed by field name with uppercase first char of field name")

                map.put(fieldName, getterMethod)

                Log.d(TAG, "Mapping FieldName = $fieldName methodName = ${getterMethod.name}")
            }

            return map
        }

        private fun getClassSchema(objectInstance: RealmObject): RealmObjectSchema {
            val resultClass = objectInstance::class.java
            val modelName = resultClass.simpleName.removeSuffix("RealmProxy")

            val schema = realm.schema.get(modelName)

            if (schema != null) {
                return schema
            } else {
                throw SchemaNotFoundException("Realm Schema not found for class $modelName")
            }
        }

        private fun findGetter(objectSchema: RealmObjectSchema, resultClass: Class<out RealmObject>, fieldName: String): Method? {
            val fieldNameCamelCase: String = fieldName.elementAt(0).toUpperCase() + fieldName.substring(1)
            val getterMethodName = if (objectSchema.getFieldType(fieldName) == RealmFieldType.BOOLEAN) {
                "is$fieldNameCamelCase"
            } else {
                "get$fieldNameCamelCase"
            }

            return resultClass.getMethod(getterMethodName)
        }

        private fun findFieldsUsingGetter(resultInstance: RealmObject,
                                          fieldGetters: Map<String, Method>): ClassItem {

            val schema = getClassSchema(resultInstance)
            val primaryKeyFieldName = schema.primaryKey

            var primaryKeyItem: FieldItem? = null
            val fieldItems = arrayListOf<FieldItem>()

            // invoke all getters for the instance
            for (getter in fieldGetters) {

                val fieldItem = getFieldItem(resultInstance, schema, getter)

                if (getter.key == primaryKeyFieldName) {
                    primaryKeyItem = fieldItem
                } else {
                    fieldItems.add(fieldItem)
                }
            }

            if (primaryKeyItem == null) {
                // TODO: Improve exception message
                throw PrimaryKeyItemNotFoundException("Field primary key not found")
            }

            return ClassItem(primaryKeyItem, fieldItems)
        }

        private fun getFieldItem(resultInstance: RealmObject, schema: RealmObjectSchema,
                                 getter: Map.Entry<String, Method>): FieldItem {

            val fieldName = getter.key
            val fieldType = schema.getFieldType(fieldName).nativeValue
            var data = getter.value.invoke(resultInstance)

            Log.d("Tag", "field name = $fieldName fieldtype = ${fieldType}")

            if (data != null) {

                if (fieldType == RealmFieldType.OBJECT.nativeValue) {

                    val parentPrimaryKeyFieldName = schema.primaryKey
                    val parentPrimaryKeyFieldType = schema.getFieldType(parentPrimaryKeyFieldName)
                    val parentPrimaryKeyFieldValue = findGetter(schema, resultInstance::class.java, parentPrimaryKeyFieldName)?.invoke(resultInstance)

                    data = ObjectType(
                            data.javaClass.simpleName.removeSuffix("RealmProxy"),
                            schema.className,
                            parentPrimaryKeyFieldName,
                            parentPrimaryKeyFieldType.nativeValue,
                            parentPrimaryKeyFieldValue,
                            getter.key,
                            getter.value.name)
                }
            }

            return FieldItem(getter.key, fieldType, data)
        }

        override fun onPostExecute(result: DisplayResult) {
            super.onPostExecute(result)

            if (callback == null)
                return

            callback.onFetchComplete(result)
        }
    }
}