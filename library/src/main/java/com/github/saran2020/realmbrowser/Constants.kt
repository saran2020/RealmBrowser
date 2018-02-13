@file:JvmName("Constants")

package com.github.saran2020.realmbrowser

/**
 * Created by Saran Sankaran on 11/10/17.
 */

const val NO_ITEM_SELECTED: Byte = -1

// Extra keys
const val EXTRA_CLASS_NAME = "EXTRA_CLASS_NAME"
const val EXTRA_FIND = "EXTRA_FIND"

const val EXTRA_PARENT_PRIMARY_KEY_NAME = "EXTRA_PARENT_PRIMARY_KEY_NAME"
const val EXTRA_PARENT_PRIMARY_KEY_TYPE = "EXTRA_PARENT_PRIMARY_KEY_TYPE"
const val EXTRA_PARENT_PRIMARY_KEY_VALUE = "EXTRA_PARENT_PRIMARY_KEY_VALUE"
const val EXTRA_KEY_FIELD_GETTER_NAME = "EXTRA_KEY_FIELD_GETTER_NAME"
const val EXTRA_KEY_FIELD_NAME = "EXTRA_KEY_FIELD_NAME"
const val EXTRA_KEY_FIELD_TYPE = "EXTRA_KEY_FIELD_TYPE"
const val EXTRA_PARENT_PRIMARY_KEY_GETTER_NAME = "EXTRA_PARENT_PRIMARY_KEY_GETTER_NAME"

// Query Types
const val QUERY_EQUAL_TO: Byte = 50

// Query Type String
const val QUERY_EQUAL_TO_STRING = "equalTo()"

// Find Type
const val FIND_NO: Byte = -1
const val FIND_ALL: Byte = 1
const val FIND_FIRST: Byte = 2

// Find Type String
const val FIND_ALL_STRING = "findAll()"
const val FIND_FIRST_STRING = "findFirst()"

// data types
const val NO_DATA_TYPE: Byte = 0
const val TYPE_BOOLEAN: Byte = -1
const val TYPE_BYTE: Byte = -2
const val TYPE_CHAR: Byte = -3
const val TYPE_SHORT: Byte = -4
const val TYPE_INT: Byte = -5
const val TYPE_LONG: Byte = -6
const val TYPE_FLOAT: Byte = -7
const val TYPE_DOUBLE: Byte = -8
const val TYPE_STRING: Byte = -9
const val TYPE_BYTE_ARRAY: Byte = -10
const val TYPE_REALM_LIST: Byte = -11
const val TYPE_REALM_OBJECT: Byte = -12

const val GETTER_PREFIX = "get"
const val BOOLEAN_GETTER_PREFIX = "is"

const val ERROR_TEXT = "Some error occurred"

// result types
const val RESULT_TYPE_EMPTY:Byte = 110
const val RESULT_TYPE_OBJECT:Byte = 111
const val RESULT_TYPE_LIST:Byte = 112
const val RESULT_TYPE_REALM_RESULT:Byte = 113