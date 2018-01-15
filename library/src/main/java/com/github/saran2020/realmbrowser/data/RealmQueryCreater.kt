package com.github.saran2020.realmbrowser.data

import android.os.Bundle
import com.github.saran2020.realmbrowser.*
import com.github.saran2020.realmbrowser.data.model.QueryResult
import io.realm.*

/**
 * Created by Saran Sankaran on 1/15/18.
 */
class RealmQueryCreater(private val realm: Realm, private val bundle: Bundle) {

    fun getResult(): QueryResult {
        val query = getBaseQuery()

        val result = QueryResult()
        val queryResult = when (bundle.getByte(EXTRA_FIND)) {
            FIND_FIRST -> {
                result.resultType = RESULT_TYPE_OBJECT
                findFirst(query)
            }
            FIND_ALL -> {
                result.resultType = RESULT_TYPE_REALM_RESULT
                query.findAll()
            }
            else -> null
        }

        if (bundle.containsKey(EXTRA_PARENT_PRIMARY_KEY_GETTER_NAME) && queryResult != null) {

            val getterMethodName = bundle.getString(EXTRA_PARENT_PRIMARY_KEY_GETTER_NAME)
            val getterMethod = queryResult::class.java.getMethod(getterMethodName)
            result.result = getterMethod.invoke(queryResult)

            if (getterMethod.returnType.isAssignableFrom(RealmList::class.java)) {
                result.resultType = RESULT_TYPE_LIST
            } else if (getterMethod.returnType.superclass == RealmObject::class.java) {
                result.resultType = RESULT_TYPE_OBJECT
            }

        } else {
            result.result = queryResult
        }

        return result
    }

    private fun getBaseQuery(): RealmQuery<*> {

        val selectedClass = bundle.getString(EXTRA_CLASS_NAME)
        val selectedClassInstance = realm.configuration.realmObjectClasses.single {
            it.simpleName.equals(selectedClass, true)
        }

        val query = realm.where(selectedClassInstance)

        return query
    }


    private fun findFirst(query: RealmQuery<*>): Any? {

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
        return query.findFirst()
    }
}