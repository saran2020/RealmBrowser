package com.github.saran2020.realmbrowser

import com.github.saran2020.realmbrowser.model.FieldItem

/**
 * Created by Saran Sankaran on 1/11/18.
 */
abstract class FieldsTaskCompleteCallback {

    open fun onMultipleFetchComplete(fields: List<List<FieldItem>>?) {
    }

    open fun onSingleFetchComplete(fields: List<FieldItem>?) {
    }
}