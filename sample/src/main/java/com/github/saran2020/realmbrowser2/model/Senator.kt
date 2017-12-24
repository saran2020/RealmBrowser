package com.github.saran2020.realmbrowser2.model

import io.realm.RealmObject

/**
 * Created by its me on 24-Dec-17.
 */
class Senator(private val caucus: String,
                 private val congress_numbers: List<Int>,
                 private val current: Boolean,
                 private val description: String,
                 private val district: String,
                 private val enddate: String,
                 private val extra: Extra,
                 private val leadership_title: String,
                 private val party: String,
                 private val person: Person,
                 private val phone: String,
                 private val role_type: String,
                 private val role_type_label: String,
                 private val senator_class: String,
                 private val senator_class_label: String,
                 private val senator_rank: String,
                 private val senator_rank_label: String,
                 private val startdate: String,
                 private val state: String,
                 private val title: String,
                 private val title_long: String,
                 private val website: String) : RealmObject()