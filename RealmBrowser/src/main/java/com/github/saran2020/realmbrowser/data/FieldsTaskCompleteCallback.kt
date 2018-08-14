package com.github.saran2020.realmbrowser.data

import com.github.saran2020.realmbrowser.data.model.DisplayResult

/**
 * Created by Saran Sankaran on 1/11/18.
 */
interface FieldsTaskCompleteCallback {
    fun onFetchComplete(fields: DisplayResult) {}
}