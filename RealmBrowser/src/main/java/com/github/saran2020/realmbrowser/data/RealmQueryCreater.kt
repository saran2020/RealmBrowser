package com.github.saran2020.realmbrowser.data

import android.os.Bundle
import com.github.saran2020.realmbrowser.EXTRA_CLASS_NAME
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

/**
 * Created by Saran Sankaran on 1/15/18.
 */
class RealmQueryCreater(private val realm: Realm, private val bundle: Bundle) {

    fun getQuery(): RealmQuery<out RealmModel> {
        val query = getBaseQuery()
        return query
    }

    private fun getBaseQuery(): RealmQuery<out RealmModel> {

        val selectedClass = bundle.getString(EXTRA_CLASS_NAME)
        val selectedClassInstance = realm.configuration.realmObjectClasses.single {
            it.simpleName.equals(selectedClass, true)
        }

        val query = realm.where(selectedClassInstance)
        return query as RealmQuery<out RealmModel>
    }
}