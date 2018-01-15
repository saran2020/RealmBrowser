package com.github.saran2020.realmbrowser.data.model

import io.realm.RealmFieldType

/**
 * Created by Saran Sankaran on 1/15/18.
 */
data class ObjectInfo(val parentClassName: String,
                      val parentPrimaryKeyFieldName: String,
                      val parentPrimaryKeyType: RealmFieldType,
                      val parentPrimaryKeyValue: Any?,
                      val fieldGetterName: String)