package com.github.saran2020.realmbrowser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm

/**
 * Created by Saran Sankaran on 11/9/17.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var realm = Realm.getDefaultInstance()

    }
}