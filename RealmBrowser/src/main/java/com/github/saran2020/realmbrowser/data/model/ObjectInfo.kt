package com.github.saran2020.realmbrowser.data.model

/**
 * Created by Saran Sankaran on 1/15/18.
 */
data class ObjectInfo(val parentClassName: String,
                      val parentPrimaryKeyFieldName: String,
                      val parentPrimaryKeyType: Int,
                      val parentPrimaryKeyValue: Any?,
                      val fieldName: String,
                      val fieldGetterName: String,
                      val fieldType: Int)