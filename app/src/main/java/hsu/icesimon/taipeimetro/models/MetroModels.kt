package hsu.icesimon.taipeimetro.models

import java.util.*

/**
 * Created by Simon Hsu on 20/9/19.
 */

data class MetroRouteObj(
        var startStnId: Int = 0,
        var endStnId: Int = 0,
        var startStn: String? = null,
        var endStn: String? = null,
        var tickets: ArrayList<String>? = null,
        var timeCost: String? = null,
        var transferInfo: String? = null
)

data class MetroStationObj(
        val id: Int = 0,
        var customid: String,
        var nametw: String,
        var nameen: String,
        var zip: Int = 0,
        var addresstw: String? = null,
        var lat: String? = null,
        var otherStations: String? = null,
        var landmarksTW: String = "",
        var landmarksEN: String = "",
        var lon: String? = null,
        var officiallinenametw: String? = null,
        var officiallinenameen: String? = null,
        var customlineid: String? = null
)

data class RealTimeCalculateObj(
        var timeCost: String? = null,
        var routeGuide: String? = null,
        var tickets: ArrayList<String> = ArrayList(),
        var startStnId: Int = 0,
        var endStnId: Int = 0
)

data class RowItem(
        var type: Int,
        var desc: String,
        var line: String,
        var stationID: String
)