package com.github.saran2020.realmbrowser

import android.os.Bundle
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmQuery
import java.lang.reflect.Method
import java.util.*

/**
 * Created by its me on 29-Dec-17.
 */
class GetFields() {


    var realm: Realm = Realm.getDefaultInstance()
    lateinit var bundle: Bundle

    fun from(bundle: Bundle): GetFields {
        this.bundle = bundle
        return this
    }

    fun findFirst(): List<FieldItem?> {

        val realmQuery = getRealmQuery()
        val resultValue = realmQuery.findFirst() as RealmObject

        return findFieldsOfInstance(resultValue)
    }

    fun findAll(): List<List<FieldItem>> {
        val realmQuery = getRealmQuery()
        val realmResult = realmQuery.findAll() as RealmList<in RealmObject>

        return Collections.emptyList()
    }

    //TODO: Handle class cast exception of casting to Class<RealmObject>
    private fun getRealmQuery(): RealmQuery<*> {
        val fullClassName = bundle.getString(Constants.EXTRA_CLASS_NAME)
        val className = Class.forName(fullClassName) as Class<RealmObject>
        val query = realm.where(className)

        return query
    }

    /**
     * Find all getter methods for declared fields. This getter methods
     * will be then invoked to find the values
     */
    private fun findGettersOfFields(objectInstance: RealmObject): Map<String, Method> {

        val resultClass = objectInstance::class.java
        val fieldNames = resultClass.getMethod("getFieldNames")
                .invoke(objectInstance) as List<String>
        val methods = resultClass.methods

        return methods.filter {

            for (name in fieldNames) {

                // getters and is for boolean
                val nameGet = "get$name"
                val nameIs = "is$name"

                if (it.name.equals(nameGet, true) || it.name.equals(nameIs, true)) {
                    return@filter true
                }
            }

            return@filter false
        }.mapIndexed { index, method -> fieldNames[index] to method }.toMap()
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
            : List<FieldItem?> {


        val fieldItems = arrayOfNulls<FieldItem>(fieldGetters.size)

        var getterLoopIndex = 0
        for (getter in fieldGetters) {

            val type = getter.value.returnType
            val name = getter.key
            var data = getter.value.invoke(resultInstance)

            // If the field is an instance of Realm object, recursively find items inside that.
            if (type.superclass == RealmObject::class.java) {

                val getterOfField = findGettersOfFields(data as RealmObject)
                data = findFieldsOfInstance(data, getterOfField)

            } else if (type.isAssignableFrom(RealmList::class.java)) {

                val objects = data as RealmList<*>
                val listItems = arrayOfNulls<List<FieldItem?>>(objects.size)

                objects.forEachIndexed { index, any ->

                    // check if the list of normal type (String, Boolean) or RealmObject
                    if (any::class.java.superclass == RealmObject::class.java) {
                        listItems[index] = findFieldsOfInstance(any as RealmObject)
                    } else {
                        listItems[index] = (Collections.singletonList(FieldItem(any::class.java, "value $index", any)))
                    }
                }

                data = listItems
            }

            val fieldItem = FieldItem(type, name, data)
            fieldItems[getterLoopIndex] = fieldItem
            getterLoopIndex++
        }

        return fieldItems.toList()
    }
}