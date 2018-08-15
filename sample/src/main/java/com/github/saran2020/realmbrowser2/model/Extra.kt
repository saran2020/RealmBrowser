package com.github.saran2020.realmbrowser2.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Saran on 24-Dec-17.
 */
open class Extra() : RealmObject() {

    @PrimaryKey
    var id: Int = Int.MIN_VALUE

    lateinit var address: String
    lateinit var contact_form: String
    lateinit var office: String
    var random_dates: RealmList<Date>? = null
    var rss_url: String? = null
    var fax: String? = null
}

