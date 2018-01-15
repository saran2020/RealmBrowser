package com.github.saran2020.realmbrowser.data.model

import java.util.*

/**
 * Created by its me on 02-Jan-18.
 */
data class QueryItem(var type: Byte,
                     var queryList: LinkedList<String>,
                     var queryValues: LinkedList<String>)