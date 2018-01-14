package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import com.github.saran2020.realmbrowser.model.ObjectType
import io.realm.RealmFieldType

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
    intent.putExtra(EXTRA_PARENT_CLASS_NAME, data.parentClassName)
    intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_NAME, data.parentPrimaryKeyFieldName)
    intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyType)

    when (data.parentPrimaryKeyType) {
        RealmFieldType.BOOLEAN -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue as Boolean)
        RealmFieldType.FLOAT -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue as Float)
        RealmFieldType.DOUBLE -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue as Double)

        RealmFieldType.STRING -> {
            intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue as String)
        }

        RealmFieldType.INTEGER -> {
            when (data.parentPrimaryKeyValue) {
                is Long -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue)
                is Int -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue)
                is Short -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue)
                is Byte -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, data.parentPrimaryKeyValue)
                else -> ERROR_TEXT
            }
        }
    }

    context.startActivity(intent)
}