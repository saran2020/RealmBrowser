package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import com.github.saran2020.realmbrowser.model.ObjectType

/**
 * Helper class to store activity related stuff
 * Created by its me on 14-Jan-18.
 */
fun startResultActivity(context: Context, className: String, find: Byte) {
    val intent = Intent(context, ResultActivity::class.java)
    intent.putExtra(EXTRA_CLASS_NAME, className)
    intent.putExtra(EXTRA_FIND, find)
    context.startActivity(intent)
}

fun startResultActivity(context: Context, data: ObjectType) {
    val intent = Intent(context, ResultActivity::class.java)
    context.startActivity(intent)
}