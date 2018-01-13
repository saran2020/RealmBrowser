package com.github.saran2020.realmbrowser2.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by its me on 24-Dec-17.
 */
open class Extra() : RealmObject() {

    @PrimaryKey
    var id: Int = Int.MIN_VALUE

    lateinit var address: String
    lateinit var contact_form: String
    lateinit var office: String
    var rss_url: String? = null
    var fax: String? = null
}

