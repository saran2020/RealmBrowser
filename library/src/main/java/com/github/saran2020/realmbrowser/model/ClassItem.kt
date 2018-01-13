package com.github.saran2020.realmbrowser.model

/**
 * This class represents a realm model class and Primary key item is not included in the fields list
 *
 * Created by Saran Sankaran on 1/13/18.
 */
class ClassItem() {

    lateinit var primaryKey: FieldItem
    lateinit var fieldsList: List<FieldItem>

    constructor(primaryKey: FieldItem, fieldsList: List<FieldItem>) : this() {
        this.primaryKey = primaryKey
        this.fieldsList = fieldsList
    }
}