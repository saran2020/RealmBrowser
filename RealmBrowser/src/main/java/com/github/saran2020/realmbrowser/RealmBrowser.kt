package com.github.saran2020.realmbrowser

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

class RealmBrowser {

    companion object {

        const val CHANNEL_ID = "RealmBrowser"
        const val NOTIFCATION_ID = 101

        fun showStartNotif(context: Context) {

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notifBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_drafts_black_24dp)
                    .setContentTitle("RealmBrowser")
                    .setContentText("Click here to view your Realm DB")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)

            val notifManager = NotificationManagerCompat.from(context)
            notifManager.notify(NOTIFCATION_ID, notifBuilder.build())
        }
    }
}