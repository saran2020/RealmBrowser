package com.github.saran2020.realmbrowser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Saran Sankaran on 11/9/17.
 */

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getDefaultInstance();

        try {
            Class classTest = Class.forName("com.github.saran2020.realmbrowser.MyRealmTestObject");
            RealmResults<?> test = realm.where(classTest).findAll();
            Log.d("Yes Tag", "Size: " + test.size());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
