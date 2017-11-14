package com.github.saran2020.realmbrowser

/**
 * Created by Saran Sankaran on 11/10/17.
 */
class Constants {

    companion object {

        val NO_ITEM_SELECTED:Byte = -1

        // Extra keys
        val EXTRA_CLASS_NAME = "EXTRA_CLASS_NAME"
        val EXTRA_FIND = "EXTRA_FIND"

        // Query Types
        val QUERY_EQUAL_TO: Byte = 50

        // Query Type String
        val QUERY_EQUAL_TO_STRING = "equalTo()"

        // Find Type
        val FIND_ALL: Byte = 1
        val FIND_FIRST: Byte = 2

        // Find Type String
        val FIND_ALL_STRING = "findAll()"
        val FIND_FIRST_STRING = "findFirst()"
    }
}