package com.github.saran2020.realmbrowser

import com.github.saran2020.realmbrowser.model.ClassItem
import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * Created by Saran Sankaran on 1/11/18.
 */
abstract class FieldsTaskCompleteCallback {

    open fun onFetchComplete(fields: List<ClassItem>?) {
    }
}