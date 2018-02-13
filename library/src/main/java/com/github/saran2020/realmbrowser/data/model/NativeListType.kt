package com.github.saran2020.realmbrowser.data.model

import io.realm.RealmList

/**
 * Whenever any model contains a field type of RealmListList(only RealmList of native values,
 * not of RealmObject) instead of storing the exact value, we store this value instead. This
 * will help us in handling the click on this item in table view
 *
 * ctor
 * Created by its me on 14-Feb-18.
 */
data class NativeListType (val fieldName: String, val fieldValue: List<*>)