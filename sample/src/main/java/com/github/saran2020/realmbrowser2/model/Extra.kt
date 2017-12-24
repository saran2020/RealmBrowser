package com.github.saran2020.realmbrowser2.model

import io.realm.RealmObject

/**
 * Created by its me on 24-Dec-17.
 */
class Extra(private val address: String,
            private val contact_form: String,
            private val fax: String,
            private val office: String,
            private val rss_url: String) : RealmObject()