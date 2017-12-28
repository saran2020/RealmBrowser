package com.github.saran2020.realmbrowser

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class Constants {

    companion object {

        val NO_ITEM_SELECTED: Byte = -1

        // Extra keys
        val EXTRA_CLASS_NAME = "EXTRA_CLASS_NAME"
        val EXTRA_FIND = "EXTRA_FIND"

        // Query Types
        val QUERY_EQUAL_TO: Byte = 50

        // Query Type String
        val QUERY_EQUAL_TO_STRING = "equalTo()"

        // Find Type
        val FIND_NO: Byte = -1
        val FIND_ALL: Byte = 1
        val FIND_FIRST: Byte = 2

        // Find Type String
        val FIND_ALL_STRING = "findAll()"
        val FIND_FIRST_STRING = "findFirst()"

        // data types
        val NO_DATA_TYPE: Byte = 0
        val TYPE_BOOLEAN: Byte = -1
        val TYPE_BYTE: Byte = -2
        val TYPE_CHAR: Byte = -3
        val TYPE_SHORT: Byte = -4
        val TYPE_INT: Byte = -5
        val TYPE_LONG: Byte = -6
        val TYPE_FLOAT: Byte = -7
        val TYPE_DOUBLE: Byte = -8
        val TYPE_STRING: Byte = -9
        val TYPE_BYTE_ARRAY: Byte = -10
        val TYPE_REALM_LIST: Byte = -11
        val TYPE_REALM_OBJECT: Byte = -12

        val GETTER_PREFIX = "get"

    }
}