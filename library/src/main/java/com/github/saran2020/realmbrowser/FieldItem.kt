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
        dataType.isAssignableFrom(Boolean::class.javaObjectType) -> TYPE_BOOLEAN
        dataType.isAssignableFrom(Boolean::class.java) -> TYPE_BOOLEAN

        dataType.isAssignableFrom(Byte::class.javaObjectType) -> TYPE_BYTE
        dataType.isAssignableFrom(Byte::class.java) -> TYPE_BYTE

        dataType.isAssignableFrom(Char::class.javaObjectType) -> TYPE_CHAR
        dataType.isAssignableFrom(Char::class.java) -> TYPE_CHAR

        dataType.isAssignableFrom(Short::class.javaObjectType) -> TYPE_SHORT
        dataType.isAssignableFrom(Short::class.java) -> TYPE_SHORT

        dataType.isAssignableFrom(Int::class.javaObjectType) -> TYPE_INT
        dataType.isAssignableFrom(Int::class.java) -> TYPE_INT

        dataType.isAssignableFrom(Long::class.javaObjectType) -> TYPE_LONG
        dataType.isAssignableFrom(Long::class.java) -> TYPE_LONG

        dataType.isAssignableFrom(Float::class.javaObjectType) -> TYPE_FLOAT
        dataType.isAssignableFrom(Float::class.java) -> TYPE_FLOAT

        dataType.isAssignableFrom(Double::class.javaObjectType) -> TYPE_DOUBLE
        dataType.isAssignableFrom(Double::class.java) -> TYPE_DOUBLE

        dataType.isAssignableFrom(String::class.java) -> TYPE_STRING

        dataType.isAssignableFrom(RealmList::class.java) -> TYPE_REALM_LIST

        dataType.superclass == RealmObject::class.java -> TYPE_REALM_OBJECT

        else -> NO_DATA_TYPE
    }

    init {

        if (type == NO_DATA_TYPE &&
                dataType.isArray &&
                dataType.componentType == Byte::class.java)
            type == TYPE_BYTE_ARRAY
    }
}