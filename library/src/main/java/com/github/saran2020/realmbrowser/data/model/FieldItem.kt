package com.github.saran2020.realmbrowser.data.model

import io.realm.RealmFieldType

/**
 * Created by Saran on 29-Dec-17.
 *
 * A result item will contain the type and value of the field.
 */
data class FieldItem(val name: String, val type: Int, val value: Any?)