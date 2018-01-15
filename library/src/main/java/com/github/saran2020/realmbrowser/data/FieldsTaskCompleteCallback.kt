package com.github.saran2020.realmbrowser.data

import com.github.saran2020.realmbrowser.data.model.ClassItem

/**
 * Created by Saran Sankaran on 1/11/18.
 */
abstract class FieldsTaskCompleteCallback {

    open fun onFetchComplete(fields: List<ClassItem>?) {
    }
}