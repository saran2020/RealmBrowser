package com.github.saran2020.realmbrowser

import android.content.Context
import android.content.Intent
import com.github.saran2020.realmbrowser.data.model.ObjectType
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

    val objectInfo = data.objectInfo

    intent.putExtra(EXTRA_PARENT_CLASS_NAME, objectInfo.parentClassName)
    intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_NAME, objectInfo.parentPrimaryKeyFieldName)
    intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyType)

    when (objectInfo.parentPrimaryKeyType) {
        RealmFieldType.BOOLEAN.nativeValue -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue as Boolean)
        RealmFieldType.FLOAT.nativeValue -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue as Float)
        RealmFieldType.DOUBLE.nativeValue -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue as Double)

        RealmFieldType.STRING.nativeValue -> {
            intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue as String)
        }

        RealmFieldType.INTEGER.nativeValue -> {
            when (objectInfo.parentPrimaryKeyValue) {
                is Long -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue)
                is Int -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue)
                is Short -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue)
                is Byte -> intent.putExtra(EXTRA_PARENT_PRIMARY_KEY_TYPE, objectInfo.parentPrimaryKeyValue)
                else -> ERROR_TEXT
            }
        }
    }

    context.startActivity(intent)
}