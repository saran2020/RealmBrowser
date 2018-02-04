package com.github.saran2020.realmbrowser.data.model

/**
 * Whenever any model contains a field type of Object(Instance of RealmObject or RealmModel) instead
 * of storing the exact value, we store this value instead. This will help us in handling the click on
 * this item in table view
 *
 * ctor
 * @param displayText The text that will be displayed in table view
 * @param parentPrimaryKeyFieldName Stores the parents primary key field name. This will be used
 * to data when the object is clicked in table view and we have to display the data.
 * @param parentPrimaryKeyFieldName Primary key type of parent
 * @param parentPrimaryKeyValue Value of primary key of parent, this will be used to display the
 * data when this item is clicked in table view
 *
 * Created by its me on 13-Jan-18.
 */
data class ObjectType private constructor(val displayText: String) {

    lateinit var objectInfo: ObjectInfo

    constructor(displayText: String,
                parentClassName: String,
                parentPrimaryKeyFieldName: String,
                parentPrimaryKeyType: Int,
                parentPrimaryKeyValue: Any?,
                fieldName: String,
                fieldGetterName: String) : this(displayText) {
        objectInfo = ObjectInfo(parentClassName, parentPrimaryKeyFieldName, parentPrimaryKeyType, parentPrimaryKeyValue, fieldName, fieldGetterName)
    }
}