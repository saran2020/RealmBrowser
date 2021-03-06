package com.github.saran2020.realmbrowser2

import android.app.Application
import com.github.saran2020.realmbrowser.RealmBrowser
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Saran Sankaran on 11/9/17.
 */
@Suppress("unused")
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val realmConfig = RealmConfiguration.Builder()
                .name("sample.realm")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)

        RealmBrowser.showStartNotif(this)
    }
}
