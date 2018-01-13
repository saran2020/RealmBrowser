package com.github.saran2020.realmbrowser

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.github.saran2020.realmbrowser.model.FieldItem
import io.realm.*
import java.lang.reflect.Method
import java.util.*

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

    class FetchFieldsTask(private val bundle: Bundle, private val callback: FieldsTaskCompleteCallback?) : AsyncTask<Void, Void, List<List<FieldItem>>>() {

        lateinit var realm: Realm

        override fun doInBackground(vararg params: Void?): List<List<FieldItem>> {

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
            val fullClassName = bundle.getString(EXTRA_CLASS_NAME)
            val className = Class.forName(fullClassName) as Class<RealmObject>
            val query = realm.where(className)

            return query
        }


        private fun findFirst(query: RealmQuery<*>): List<FieldItem> {

            val resultValue = query.findFirst() as RealmObject
            return findFieldsOfInstance(resultValue)
        }


        private fun findAll(query: RealmQuery<*>): List<List<FieldItem>> {

            val realmResult: RealmResults<in RealmObject> = query.findAll() as RealmResults<in RealmObject>
            val getterMethods = findGettersOfFields(realmResult[0] as RealmObject)

            val result = arrayListOf<List<FieldItem>>()
            realmResult.forEach {
                result.add(findFieldsOfInstance(it as RealmObject, getterMethods))
            }

            return result
        }

        /**
         * Find all getter methods for declared fields. This getter methods
         * will be then invoked to find the values
         */
        private fun findGettersOfFields(objectInstance: RealmObject): Map<String, Method> {

            val resultClass = objectInstance::class.java
            val modelName = resultClass.simpleName.removeSuffix("RealmProxy")

            // TODO: Handle empty map and show appropriate error
            val classSchema = realm.schema.get(modelName) ?: return emptyMap()

            val fieldNames = classSchema.fieldNames
            val map = mutableMapOf<String, Method>()

            for (fieldName in fieldNames) {

                val fieldNameCamelCase: String = fieldName.elementAt(0).toUpperCase() + fieldName.substring(1)
                val getterMethodName = if (classSchema.getFieldType(fieldName) == RealmFieldType.BOOLEAN) {
                    "is$fieldNameCamelCase"
                } else {
                    "get$fieldNameCamelCase"
                }

                val getterMethod = resultClass.getMethod(getterMethodName)
                map.put(fieldName, getterMethod)

                Log.d(TAG, "Mapping FieldName = $fieldName methodName = ${getterMethod.name}")
            }

            return map
        }

        /**
         * Gets a list of all field items for the given object
         * @param resultInstance The object from which the getters are to be invoked
         * @param fieldGetters Getters method which are to be invoked. Can be sullied
         * as parameter so that the fields are not fetched every time for loops. If not
         * supplied by default will fetch of the given instance
         * @return A List of all field items for the given object
         */
        private fun findFieldsOfInstance(resultInstance: RealmObject,
                                         fieldGetters: Map<String, Method> = findGettersOfFields(resultInstance))
                : List<FieldItem> {

            val fieldItems = arrayListOf<FieldItem>()

            for (getter in fieldGetters) {

                val type = getter.value.returnType
                val name = getter.key
                var data = getter.value.invoke(resultInstance)

                if (data != null) {

                    if (type.superclass == RealmObject::class.java) {

                        // If the field is an instance of Realm object, recursively find items inside that.
                        val getterOfField = findGettersOfFields(data as RealmObject)
                        data = findFieldsOfInstance(data, getterOfField)

                    } else if (type.isAssignableFrom(RealmList::class.java)) {

                        val objects = data as RealmList<*>
                        val listItems = arrayListOf<List<FieldItem>>()

                        objects.forEachIndexed { index, any ->

                            // check if the list of normal type (String, Boolean) or RealmObject
                            if (any::class.java.superclass == RealmObject::class.java) {
                                listItems.add(findFieldsOfInstance(any as RealmObject))
                            } else {
                                listItems.add(Collections.singletonList(FieldItem(any::class.java, "value $index", any)))
                            }
                        }

                        data = listItems
                    }
                }

                val fieldItem = FieldItem(type, name, data)
                fieldItems.add(fieldItem)
            }

            return fieldItems
        }

        override fun onPostExecute(result: List<List<FieldItem>>?) {
            super.onPostExecute(result)

            if (callback == null)
                return

            callback.onFetchComplete(result)
        }
    }
}