package hsu.icesimon.taipeimetro.utils

import android.content.Context
import android.net.ConnectivityManager
import hsu.icesimon.taipeimetro.ui.*

/**
 * Created by Simon Hsu on 20/9/19.
 */

class Util {
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun findStationName(stationId: Int, locale: String?): String {
        var stationName = ""
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].id == stationId) {
                stationName = if (locale != "zh_TW") {
                    MainActivity.Companion.mrtStationInfo[i].customid + " " + MainActivity.Companion.mrtStationInfo[i].nameen
                } else {
                    MainActivity.Companion.mrtStationInfo[i].customid + " " + MainActivity.Companion.mrtStationInfo[i].nametw + "\n" + MainActivity.Companion.mrtStationInfo[i].nameen
                }
                break
            }
        }
        return stationName
    }

    fun findStationName(stationTW: String?, locale: String?): String {
        Log.d("input : $stationTW")
        var stationName = ""
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].nametw == stationTW) {
                stationName = if (locale != "zh_TW") {
                    // Log.d("search in EN");
                    MainActivity.Companion.mrtStationInfo[i].customid + " " + MainActivity.Companion.mrtStationInfo[i].nameen
                } else {
                    MainActivity.Companion.mrtStationInfo[i].customid + " " + MainActivity.Companion.mrtStationInfo[i].nametw + "\n" + MainActivity.Companion.mrtStationInfo[i].nameen
                }
                // Log.d("stationName: "+ stationName);
                break
            }
        }
        Log.d("stationName:  $stationName")
        return stationName
    }

    fun findLineName(lineId: Int, locale: String?): String {
        var lineEnglishName = ""
        if (locale != "zh_TW") {
            when (lineId) {
                1 -> lineEnglishName = "Wenhu Line"
                2 -> lineEnglishName = "Tamsui-Xinyi Line"
                3 -> lineEnglishName = "Songshan-Xindian Line"
                4 -> lineEnglishName = "Zhonghe-Xinlu Line"
                5 -> lineEnglishName = "Bannan Line"
            }
        } else {
            when (lineId) {
                1 -> lineEnglishName = "文湖線"
                2 -> lineEnglishName = "淡水信義線"
                3 -> lineEnglishName = "松山新店線"
                4 -> lineEnglishName = "中和新蘆線"
                5 -> lineEnglishName = "板南線"
            }
        }
        return lineEnglishName
    }

    fun findTransferDirection(direction: String, locale: String?): String {
        var transferDirection = ""
        if (direction == "往北投/往淡水") {
            transferDirection = "To Beitou / To Tamsui"
        } else if (direction == "往大安/往象山") {
            transferDirection = "To Daan / To Xiangshan"
        } else if (direction == "往南勢角") {
            transferDirection = "To Daan/ To Xiangshan"
        } else if (direction == "往蘆洲") {
            transferDirection = "To Luzhou"
        } else if (direction == "往迴龍") {
            transferDirection = "To Huilong"
        } else if (direction == "往台電大樓/往新店") {
            transferDirection = "To Taipower Building / To Xindian"
        } else if (direction == "往新店") {
            transferDirection = "To Xindian"
        } else if (direction == "往松山") {
            transferDirection = "To SongShan"
        } else if (direction == "往南港展覽館") {
            transferDirection = "To Taipei Nangang Exhibition Center"
        } else if (direction == "往動物園") {
            transferDirection = "To Taipei Zoo"
        } else if (direction == "往亞東醫院/往永寧") {
            transferDirection = "To Far Eastern Hospital / To Yongning"
        } else if (direction == "往永寧") {
            transferDirection = "To Yongning"
        } else if (direction == "往象山") {
            transferDirection = "To Xiangshan"
        } else if (direction == "往淡水") {
            transferDirection = "To Tamsui"
        } else if (direction == "往新北投") {
            transferDirection = "To Xinbeitou"
        } else if (direction == "往七張") {
            transferDirection = "To Qizhang"
        } else if (direction == "往北投") {
            transferDirection = "To Beitou"
        } else if (direction == "往小碧潭") {
            transferDirection = "To Xiaobitan"
        } else if (direction == "往迴龍/往蘆洲") {
            transferDirection = "To Huilong / Luzhou"
        }
        transferDirection = if (locale != "zh_TW") {
            return transferDirection
        } else {
            """
     $direction
     $transferDirection
     """.trimIndent()
        }
        return transferDirection
    }

    fun findStationNameArray(stationTW: String): Array<String>? {
        var stationNameArray: Array<String>? = null
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].nametw == stationTW) {
                val customId: String? = MainActivity.Companion.mrtStationInfo[i].customid
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.mrtStationInfo.get(i).getOtherStations());
                if (customId?.startsWith("T")!!) {
                    stationNameArray = MainActivity.Companion.mrtStationInfo[i].otherStations?.split(",")?.toTypedArray()
                }
                break
            }
        }
        return stationNameArray
    }

    fun findStationNameArrayById(id: Int): Array<String>? {
        var stationNameArray: Array<String>? = null
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].id == id) {
                val customId: String? = MainActivity.Companion.mrtStationInfo[i].customid
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.mrtStationInfo.get(i).getOtherStations());
                if (customId?.startsWith("T")!!) {
                    stationNameArray = MainActivity.Companion.mrtStationInfo[i].otherStations?.split(",")?.toTypedArray()
                }
                break
            }
        }
        return stationNameArray
    }

    //    private String findStationCustomId(int id) {
    //        String stationNameCustomId = null;
    //        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++)
    //        {
    //            if (MainActivity.mrtStationInfo[i].id == id)
    //            {
    //                stationNameCustomId= MainActivity.mrtStationInfo[i].customid;
    //                break;
    //            }
    //        }
    //        return stationNameCustomId;
    //    }
    fun findSimplyStationName(stationTW: String, locale: String?): String? {
        var stationName = ""
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].nametw == stationTW) {
                stationName = if (locale != "zh_TW") ({
                    MainActivity.Companion.mrtStationInfo[i].nameen
                }).toString() else {
                    MainActivity.Companion.mrtStationInfo[i].nametw + "\n" + MainActivity.Companion.mrtStationInfo[i].nameen
                }
                break
            }
        }
        return stationName
    }

    fun findSimplyStationNameById(id: Int, locale: String?): String? {
        var stationName = ""
        Log.d("locale = "+locale)
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].id == id) {
                Log.d("nameen = "+MainActivity.Companion.mrtStationInfo[i].nameen)
                Log.d("nametw = "+MainActivity.Companion.mrtStationInfo[i].nametw)

                stationName = if (locale != "zh_TW") {
                    MainActivity.Companion.mrtStationInfo[i].nameen
                } else {
                    MainActivity.Companion.mrtStationInfo[i].nametw + "\n" + MainActivity.Companion.mrtStationInfo[i].nameen
                }
                break
            }
        }
        Log.d("stationName:  $stationName")
        return stationName
    }

    fun findStationIdByName(stationTW: String): Int {
        var stationId = 0
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].nametw == stationTW) {
                stationId = MainActivity.Companion.mrtStationInfo[i].id
                // Log.d("stationName: "+ stationName);
                break
            }
        }
        return stationId
    }

    fun disToPriceDefault(dis: Int): Double {
        if (dis <= 5000) {
            return 20.0
        }
        return if (dis <= 23000) {
            25 + Math.floor((dis - 5000) / 3000.toDouble()) * 5
        } else 55 + Math.floor((dis - 23000) / 4000.toDouble()) * 5
    }
}