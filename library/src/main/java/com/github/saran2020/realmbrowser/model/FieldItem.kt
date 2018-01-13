package com.github.saran2020.realmbrowser.model

import io.realm.RealmFieldType

/**
 * Created by its me on 29-Dec-17.
 *
 * A result item will contain the type and value of the field.
 */
class FieldItem() {

    lateinit var name: String
    lateinit var type: RealmFieldType
    var value: Any? = null

    constructor(fieldName: String, fieldValue: Any?, fieldType: RealmFieldType) : this() {
        this.name = fieldName
        this.value = fieldValue
        this.type = fieldType
    }
}