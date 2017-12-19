package com.github.saran2020.realmbrowser2

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Saran Sankaran on 11/9/17.
 */
open class MyRealmTestObject(@PrimaryKey var _id: Long, var data: String, var list: RealmList<RealmListItem>, var item: RealmSingleItem?) : RealmObject() {

    // Empty constructor
    constructor() : this(0, "Text", RealmList(), RealmSingleItem(0, "Text"))
}