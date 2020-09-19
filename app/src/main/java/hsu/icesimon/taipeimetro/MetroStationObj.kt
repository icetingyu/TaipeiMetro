package hsu.icesimon.taipeimetro


/**
 * Created by Simon Hsu on 15/4/19.
 */

data class MetroStationObj (
    val id: Int = 0,
    var customid: String,
    var nametw: String,
    var nameen: String? = null,
    var zip: Int = 0,
    var addresstw: String? = null,
    var lat: String? = null,
    var otherStations: String? = null,
    var landmarksTW: String? = null,
    var landmarksEN: String? = null,
    var lon: String? = null,
    var officiallinenametw: String? = null,
    var officiallinenameen: String? = null,
    var customlineid: String? = null
)


