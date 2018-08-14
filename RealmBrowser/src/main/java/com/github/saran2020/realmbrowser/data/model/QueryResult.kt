package com.github.saran2020.realmbrowser.data.model

import com.github.saran2020.realmbrowser.RESULT_TYPE_EMPTY

/**
 * Created by Saran Sankaran on 1/15/18.
 */
class QueryResult() {

    var resultType: Byte = RESULT_TYPE_EMPTY
    var result: Any? = null

    constructor(resultType: Byte, result: Any?) : this() {
        this.resultType = resultType
        this.result = result
    }
}