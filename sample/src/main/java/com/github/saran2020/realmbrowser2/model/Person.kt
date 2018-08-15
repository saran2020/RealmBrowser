package com.github.saran2020.realmbrowser2.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

/**
 * Created by Saran on 24-Dec-17.
 */
open class Person() : RealmObject() {

    @PrimaryKey
    var id: Int = Int.MIN_VALUE

    lateinit var bioguideid: String
    lateinit var birthday: Date
    var cspanid: Int = 0
    lateinit var firstname: String
    lateinit var gender: String
    lateinit var gender_label: String
    lateinit var lastname: String
    lateinit var link: String
    lateinit var middlename: String
    lateinit var name: String
    lateinit var namemod: String
    lateinit var nickname: String
    lateinit var osid: String
    lateinit var pvsid: String
    lateinit var sortname: String
    lateinit var twitterid: String
    lateinit var youtubeid: String
}