package com.github.saran2020.realmbrowser2.model

import io.realm.RealmObject

/**
 * Created by its me on 24-Dec-17.
 */
class Person(private val bioguideid: String,
             private val birthday: String,
             private val cspanid: Int,
             private val firstname: String,
             private val gender: String,
             private val gender_label: String,
             private val lastname: String,
             private val link: String,
             private val middlename: String,
             private val name: String,
             private val namemod: String,
             private val nickname: String,
             private val osid: String,
             private val pvsid: String,
             private val sortname: String,
             private val twitterid: String,
             private val youtubeid: String) : RealmObject()