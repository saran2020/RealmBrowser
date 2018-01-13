package com.github.saran2020.realmbrowser2

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Saran Sankaran on 11/9/17.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val realmConfig = RealmConfiguration.Builder()
                .name("sample.realm").schemaVersion(1).build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}