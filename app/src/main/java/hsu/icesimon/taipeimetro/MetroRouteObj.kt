package hsu.icesimon.taipeimetro

import java.util.*

/**
 * Created by Simon Hsu on 15/4/19.
 */
class MetroRouteObj {
    var startStnId = 0
    var endStnId = 0
    var startStn: String? = null
    var endStn: String? = null
    var tickets: ArrayList<String>? = null
    var timeCost: String? = null
    var transferInfo: String? = null
}