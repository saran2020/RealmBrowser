package com.github.saran2020.realmbrowser2

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by its me on 19-Dec-17.
 */
open class RealmListItem(@PrimaryKey var _id: Long, var data: String) : RealmObject() {

    // Empty constructor
    constructor() : this(0, "Text")
}