package com.github.saran2020.realmbrowser

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by its me on 29-Dec-17.
 *
 * A result item will contain the type and value of the field.
 */
class FieldItem(dataType: Class<*>, var fieldName: String, val value: Any?) {

    var type: Byte = when {
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

    init {

        if (type == Constants.NO_DATA_TYPE &&
                dataType.isArray &&
                dataType.componentType == Byte::class.java)
            type == Constants.TYPE_BYTE_ARRAY
    }
}