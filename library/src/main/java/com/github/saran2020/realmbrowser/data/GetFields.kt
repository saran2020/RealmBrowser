package com.github.saran2020.realmbrowser.data

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.github.saran2020.realmbrowser.Exception.GetterMethodNotFoundException
import com.github.saran2020.realmbrowser.Exception.PrimaryKeyItemNotFoundException
import com.github.saran2020.realmbrowser.Exception.SchemaNotFoundException
import com.github.saran2020.realmbrowser.RESULT_TYPE_LIST
import com.github.saran2020.realmbrowser.RESULT_TYPE_OBJECT
import com.github.saran2020.realmbrowser.RESULT_TYPE_REALM_RESULT
import com.github.saran2020.realmbrowser.TAG
import com.github.saran2020.realmbrowser.data.model.ClassItem
import com.github.saran2020.realmbrowser.data.model.FieldItem
import com.github.saran2020.realmbrowser.data.model.ObjectType
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

    class FetchFieldsTask(private val bundle: Bundle, private val callback: FieldsTaskCompleteCallback?) : AsyncTask<Void, Void, List<ClassItem>>() {

        lateinit var realm: Realm

        override fun doInBackground(vararg params: Void?): List<ClassItem> {

            realm = Realm.getDefaultInstance()

            val queryResult = RealmQueryCreater(realm, bundle).getResult()

            if (queryResult.result == null) {
                return emptyList()
            }

            return when (queryResult.resultType) {
                RESULT_TYPE_OBJECT -> {
                    val fieldItems = findFieldsFromInstance(queryResult.result as RealmObject)
                    arrayListOf(fieldItems)
                }
                RESULT_TYPE_REALM_RESULT -> {
                    findFieldsFromRealmResult(queryResult.result as RealmResults<in RealmObject>)
                }
                RESULT_TYPE_LIST -> {
                    findFieldsFromRealmList(queryResult.result as RealmList<in RealmObject>)
                }
                else -> emptyList()
            }

//            val query = getRealmQuery()
//
//            return when (bundle.getByte(EXTRA_FIND)) {
//                FIND_FIRST -> {
//                    val fieldItems = findFirst(query)
//                    arrayListOf(fieldItems)
//                }
//                FIND_ALL -> findAll(query)
//                else -> emptyList()
//            }
        }

        private fun findFieldsFromRealmList(realmResults: RealmList<in RealmObject>): List<ClassItem> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        //        //TODO: Handle class cast exception of casting to Class<RealmObject>
//        private fun getRealmQuery(): RealmQuery<*> {
//
//            val selectedClass = bundle.getString(EXTRA_CLASS_NAME)
//            val selectedClassInstance = realm.configuration.realmObjectClasses.single {
//                it.simpleName.equals(selectedClass, true)
//            }
//
//            val query = realm.where(selectedClassInstance)
//
//            return query
//        }
//
//
//        private fun findFirst(query: RealmQuery<*>): ClassItem {
//
//            val resultValue = query.findFirst() as RealmObject
//            return findFieldsFromInstance(resultValue)
//        }
//
//
        private fun findFieldsFromRealmResult(realmResult: RealmResults<in RealmObject>): ArrayList<ClassItem> {

            val getterMethods = findGetters(realmResult[0] as RealmObject)

            val result = arrayListOf<ClassItem>()
            realmResult.forEach {
                result.add(findFieldsFromInstance(it as RealmObject, getterMethods))
            }

            return result
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

                val getterMethod = findGetter(classSchema, resultClass, fieldName)

                if (getterMethod == null) {
                    throw GetterMethodNotFoundException("Getter method not found for field $fieldName. " +
                            "Getter method should start with get or is followed by field name with uppercase first char of field name")
                }

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

        private fun findFieldsFromInstance(resultInstance: RealmObject,
                                           fieldGetters: Map<String, Method> = findGetters(resultInstance))
                : ClassItem {

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
            val fieldType = schema.getFieldType(fieldName)
            var data = getter.value.invoke(resultInstance)

            Log.d("Tag", "field name = $fieldName fieldtype name = ${fieldType.name} nativeValue = ${fieldType.nativeValue}")

            if (data != null) {

                if (fieldType == RealmFieldType.OBJECT) {

                    val parentPrimaryKeyFieldName = schema.primaryKey
                    val parentPrimaryKeyFieldType = schema.getFieldType(parentPrimaryKeyFieldName)
                    val parentPrimaryKeyFieldValue = findGetter(schema, resultInstance::class.java, parentPrimaryKeyFieldName)?.invoke(resultInstance)

                    data = ObjectType(
                            data.javaClass.simpleName.removeSuffix("RealmProxy"),
                            schema.className,
                            parentPrimaryKeyFieldName,
                            parentPrimaryKeyFieldType.nativeValue,
                            parentPrimaryKeyFieldValue,
                            getter.value.name)
                }
            }

            return FieldItem(getter.key, fieldType, data)
        }

        override fun onPostExecute(result: List<ClassItem>?) {
            super.onPostExecute(result)

            if (callback == null)
                return

            callback.onFetchComplete(result)
        }
    }
}