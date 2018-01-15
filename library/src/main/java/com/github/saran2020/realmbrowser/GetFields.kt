package com.github.saran2020.realmbrowser

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.github.saran2020.realmbrowser.Exception.GetterMethodNotFoundException
import com.github.saran2020.realmbrowser.Exception.PrimaryKeyItemNotFoundException
import com.github.saran2020.realmbrowser.Exception.SchemaNotFoundException
import com.github.saran2020.realmbrowser.model.ClassItem
import com.github.saran2020.realmbrowser.model.FieldItem
import com.github.saran2020.realmbrowser.model.ObjectType
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

            val query = getRealmQuery()

            return when (bundle.getByte(EXTRA_FIND)) {
                FIND_FIRST -> {
                    val fieldItems = findFirst(query)
                    arrayListOf(fieldItems)
                }
                FIND_ALL -> findAll(query)
                else -> emptyList()
            }
        }

        //TODO: Handle class cast exception of casting to Class<RealmObject>
        private fun getRealmQuery(): RealmQuery<*> {

            val selectedClass = bundle.getString(EXTRA_CLASS_NAME)
            val selectedClassInstance = realm.configuration.realmObjectClasses.single {
                it.simpleName.equals(selectedClass, true)
            }

            val query = realm.where(selectedClassInstance)

            return query
        }


        private fun findFirst(query: RealmQuery<*>): ClassItem {

            val resultValue = query.findFirst() as RealmObject
            return findFieldsOfInstance(resultValue)
        }


        private fun findAll(query: RealmQuery<*>): ArrayList<ClassItem> {

            val realmResult: RealmResults<in RealmObject> = query.findAll() as RealmResults<in RealmObject>
            val getterMethods = findGetters(realmResult[0] as RealmObject)

            val result = arrayListOf<ClassItem>()
            realmResult.forEach {
                result.add(findFieldsOfInstance(it as RealmObject, getterMethods))
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

        private fun findFieldsOfInstance(resultInstance: RealmObject,
                                         fieldGetters: Map<String, Method> = findGetters(resultInstance))
                : ClassItem {

            val schema = getClassSchema(resultInstance)
            val primaryKeyFieldName = schema.primaryKey

            var primaryKeyItem: FieldItem? = null
            val fieldItems = arrayListOf<FieldItem>()

            // invoke all getters for the instance
            for (getter in fieldGetters) {

                val fieldItem = getFieldItem(resultInstance, schema, getter);

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

            if (data != null) {

                if (fieldType == RealmFieldType.OBJECT) {

                    val parentPrimaryKeyFieldName = schema.primaryKey
                    val parentPrimaryKeyFieldType = schema.getFieldType(parentPrimaryKeyFieldName)
                    val parentPrimaryKeyFieldValue = findGetter(schema, resultInstance::class.java, parentPrimaryKeyFieldName)?.invoke(resultInstance)
                    data = ObjectType(data.javaClass.simpleName.removeSuffix("RealmProxy"),
                            schema.className,
                            parentPrimaryKeyFieldName,
                            parentPrimaryKeyFieldType,
                            parentPrimaryKeyFieldValue)
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