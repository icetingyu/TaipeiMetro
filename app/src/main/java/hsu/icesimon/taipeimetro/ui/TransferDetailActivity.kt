package hsu.icesimon.taipeimetro.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hsu.icesimon.taipeimetro.R
import hsu.icesimon.taipeimetro.adapter.CustomBaseAdapter
import hsu.icesimon.taipeimetro.models.RowItem
import hsu.icesimon.taipeimetro.ui.*
import hsu.icesimon.taipeimetro.utils.Log
import hsu.icesimon.taipeimetro.utils.Util
import org.json.JSONArray
import org.json.JSONException
import java.util.*


/**
 * Created by Simon Hsu on 20/9/19.
 */

class TransferDetailActivity : Activity() {
    var listView: ListView? = null
    var rowItems: MutableList<RowItem>? = null
    private val laySwipe: SwipeRefreshLayout? = null
    private var routes: String? = null
    private var time: String? = null
    private var routePlanTime: TextView? = null
    private var singleRidePrice: TextView? = null
    private var easyCardPrice: TextView? = null
    private var concessionairePrice: TextView? = null
    private val progressBar: ProgressBar? = null
    private var swipeStartX: Float = 0f
    private var swipeStartY: Float = 0f
    private var mInflater: LayoutInflater? = null
    private var backBtn: ImageButton? = null
    private var mSP: SharedPreferences? = null
    private var startStnId: Int = 0
    private var endStnId: Int = 0
    private var locale: String? = null
    private var mUtils: Util? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transfer_guide_layout)
        mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        mSP = PreferenceManager.getDefaultSharedPreferences(baseContext)
        locale = mSP?.getString("locale", "")
        listView = findViewById<View>(R.id.list) as ListView?
        rowItems = ArrayList()
        routePlanTime = findViewById<View>(R.id.routePlanTime) as TextView?
        singleRidePrice = findViewById<View>(R.id.singleRidePrice) as TextView?
        easyCardPrice = findViewById<View>(R.id.easyCardPrice) as TextView?
        concessionairePrice = findViewById<View>(R.id.concessionairePrice) as TextView?
        backBtn = findViewById<View>(R.id.backBtn) as ImageButton?
        backBtn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finishActivity()
            }
        })
        val intent: Intent = intent
        mUtils = Util()
        routes = intent.getStringExtra(MainActivity.Companion.ROUTEPLAN)
        time = intent.getStringExtra(MainActivity.Companion.ROUTETIME)?.replace("約", "")?.replace(" 分鐘", "")
        startStnId = intent.getIntExtra(MainActivity.Companion.ROUTESTART, 0)
        endStnId = intent.getIntExtra(MainActivity.Companion.ROUTEEND, 0)
        val temptickets: String? = intent.getStringExtra(MainActivity.Companion.ROUTETICKETS)
        Log.d("routes: " + routes)
        try {
            val ticketArray: JSONArray = JSONArray(temptickets)
            singleRidePrice!!.text = getString(R.string.priceTag) + " " + ticketArray.get(0).toString().replace("元", "")
            easyCardPrice!!.text = getString(R.string.priceTag) + " " + ticketArray.get(1).toString().replace("元", "")
            concessionairePrice!!.text = getString(R.string.priceTag) + " " + ticketArray.get(2).toString().replace("元", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        routePlanTime!!.text = getString(R.string.timecost) + time + getString(R.string.timeunit)
        val routeData: Array<String> = routes?.split("=>")!!.toTypedArray()
        var itemLine: String = ""
        var startPreLine: String = ""
        for (i in routeData.indices) {
            val info: String = routeData.get(i)
            val currentLine: String = ""
            val transferLine: String = ""
            var displayText: String = ""
            var middleText: String = ""
            var mDisplayText: String = ""
            if (info.contains("搭乘")) {
                val index: Int = info.indexOf("搭乘")
                val tempLine: String = info.substring(index + 2, index + 3)
                if (info.contains("新北投")) {
                    itemLine = "2"
                } else if (info.contains("小碧潭")) {
                    itemLine = "3"
                } else {
                    if (!(tempLine == "1") && !(tempLine == "2") && !(tempLine == "3") && !(tempLine == "4") && !(tempLine == "5")) {
                    } else {
                        itemLine = info.substring(index + 2, index + 3)
                    }
                }
                startPreLine = itemLine
                // Log.d("itemLine : " + itemLine);
                val split: Array<String> = info.split("（").toTypedArray()
                if (split.size > 0) {
                    val transferStationTW: String = split.get(0).split("搭乘").toTypedArray().get(0).trim({ it <= ' ' })
                    if (transferStationTW.indexOf("車站") != -1) {
                        Log.d("displayText before change : " + displayText)
                        displayText = transferStationTW
                    } else {
                        displayText = transferStationTW.replace("站", "")
                    }
                    val stations: Array<String>? = mUtils!!.findStationNameArrayById(startStnId)
                    middleText = mUtils!!.findTransferDirection(split.get(1).replace("）", "").trim({ it <= ' ' }), locale)
                    Log.d("搭乘MiddleText  :" + middleText)
                    if (stations == null) {
                        if (!(locale == "zh_TW")) {
                            mDisplayText = mUtils!!.findStationName(startStnId, locale) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        } else {
                            mDisplayText = mUtils!!.findStationName(startStnId, locale) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        }
                    } else {
                        for (k in stations.indices) {
                            val eachOtherStation: String? = stations.get(k)
                            Log.d("eachOtherStation  :" + eachOtherStation)
                            if ((eachOtherStation?.substring(0, 1) == startPreLine)) {
                                if (!(locale == "zh_TW")) {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) //+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) //+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                }
                                //                                RowItem itemDetail = new RowItem(1,mDisplayText, startPreLine,"");
//
//                                rowItems.add(itemDetail);
                                break
                            }
                        }
                    }
                } else {
                }
                val itemDetail: RowItem = RowItem(0, mDisplayText, itemLine, startStnId.toString() + "")
                val itemMiddle: RowItem = RowItem(1, middleText, itemLine, "")
                rowItems?.add(itemDetail)
                rowItems?.add(itemMiddle)
            } else if (info.contains("轉乘")) {
                val index: Int = info.indexOf("轉乘")
                val tempLine: String = info.substring(index + 2, index + 3)
                if (!(tempLine == "1") && !(tempLine == "2") && !(tempLine == "3") && !(tempLine == "4") && !(tempLine == "5")) {
                } else {
                    itemLine = info.substring(index + 2, index + 3)
                }
                Log.d("轉乘 itemLine  :" + itemLine)
                val split: Array<String> = info.split("（").toTypedArray()
                if (split.size > 0) {
                    middleText = (split.get(1).replace("）", "").trim({ it <= ' ' }))
                    Log.d("轉乘MiddleText  :" + middleText)
                    val transferStationTW: String = split.get(0).split("轉乘").toTypedArray().get(0).trim({ it <= ' ' })
                    if (transferStationTW.indexOf("車站") != -1) {
                        Log.d("displayText before change : " + displayText)
                        displayText = transferStationTW
                    } else {
                        displayText = transferStationTW.replace("站", "")
                    }
                    Log.d("displayText before change : " + displayText)

                    // Check if the station is intersection
                    val stations: Array<String>? = mUtils!!.findStationNameArray(displayText)
                    val oriStationID: Int = mUtils!!.findStationIdByName(displayText)
                    if (stations == null) {
                        if (!(locale == "zh_TW")) {
                            mDisplayText = mUtils!!.findStationName(displayText, locale) + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        } else {
                            mDisplayText = mUtils!!.findStationName(displayText, locale) + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        }
                        if ((middleText == "往小碧潭")) {
                            itemLine = 3.toString() + ""
                        } else if ((middleText == "往新北投")) {
                            itemLine = 2.toString() + ""
                        }
                        middleText = mUtils!!.findTransferDirection(split.get(1).replace("）", "").trim({ it <= ' ' }), locale)
                        val itemDetail: RowItem = RowItem(0, mDisplayText, itemLine, oriStationID.toString() + "")
                        val itemMiddle: RowItem = RowItem(1, middleText, itemLine, "")
                        rowItems?.add(itemDetail)
                        rowItems?.add(itemMiddle)
                    } else {
                        Log.d("startPreLine  :" + startPreLine)
                        // add previous station stop
                        for (k in stations.indices) {
                            val eachOtherStation: String? = stations.get(k)
                            Log.d("eachOtherStation  :" + eachOtherStation)
                            if ((eachOtherStation?.substring(0, 1) == startPreLine)) {
                                if (!(locale == "zh_TW")) {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) //+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) //+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                }
                                val itemDetail: RowItem = RowItem(3, mDisplayText, startPreLine, oriStationID.toString() + "")
                                rowItems?.add(itemDetail)
                                break
                            }
                        }
                        startPreLine = tempLine

                        // add current transfer station
                        if (!(locale == "zh_TW")) {
                            mDisplayText = mUtils!!.findStationName(displayText, locale) // + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                        } else {
                            mDisplayText = mUtils!!.findStationName(displayText, locale) // + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                        }
                        if ((middleText == "往小碧潭")) {
                            itemLine = 3.toString() + ""
                        } else if ((middleText == "往新北投")) {
                            itemLine = 2.toString() + ""
                        }
                        Log.d("displayText after : " + mDisplayText)
                        var itemDetail: RowItem = RowItem(2, mDisplayText, itemLine, oriStationID.toString() + "")
                        (rowItems as ArrayList<RowItem>).add(itemDetail)
                        for (k in stations.indices) {
                            val eachOtherStation: String? = stations.get(k)
                            Log.d("eachOtherStation  :" + eachOtherStation)
                            if ((eachOtherStation?.substring(0, 1) == itemLine)) {
                                var transferStringEn: String = ""
                                var transferStringTw: String = ""
                                if (!(itemLine == "1") && !(itemLine == "2") && !(itemLine == "3") && !(itemLine == "4") && !(itemLine == "5")) {
                                    transferStringEn = "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                                    transferStringTw = "\n" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                                }
                                if (!(locale == "zh_TW")) {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) + transferStringEn
                                } else {
                                    mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationName(displayText, locale) + transferStringTw
                                }
                                Log.d("sfdsdfsdfd middleText : " + middleText)
                                if ((middleText == "往小碧潭")) {
                                    itemLine = 3.toString() + ""
                                } else if ((middleText == "往新北投")) {
                                    itemLine = 2.toString() + ""
                                } else {
                                    itemLine = startPreLine
                                }
                                Log.d("sfdsdfsdfd : " + itemLine)
                                middleText = mUtils!!.findTransferDirection(split.get(1).replace("）", "").trim({ it <= ' ' }), locale)
                                itemDetail = RowItem(0, mDisplayText, itemLine, oriStationID.toString() + "")
                                middleText = mUtils!!.findTransferDirection(split.get(1).replace("）", "").trim({ it <= ' ' }), locale)
                                val itemMiddle: RowItem = RowItem(1, middleText, itemLine, "")
                                Log.d("final itemMiddle text : " + middleText)
                                rowItems?.add(itemDetail)
                                rowItems?.add(itemMiddle)
                                break
                            }
                        }
                    }
                } else {
                }
            } else {
                val stations: Array<String>? = mUtils!!.findStationNameArrayById(endStnId)
                Log.d("EndStations : " + stations.toString())
                if (stations == null) {
                    if (!(locale == "zh_TW")) {
                        if (!(itemLine == "（")) {
                            mDisplayText = mUtils!!.findStationName(endStnId, locale) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        } else {
                            mDisplayText = mUtils!!.findStationName(endStnId, locale)
                        }
                    } else {
                        if (!(itemLine == "（")) {
                            mDisplayText = mUtils!!.findStationName(endStnId, locale) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + mUtils!!.findLineName(itemLine.toInt(), locale) + ")"
                        } else {
                            mDisplayText = mUtils!!.findStationName(endStnId, locale)
                        }
                    }
                } else {
                    for (k in stations.indices) {
                        val eachOtherStation: String? = stations.get(k)
                        Log.d("eachOtherStation  :" + eachOtherStation)
                        if ((eachOtherStation?.substring(0, 1) == startPreLine)) {
                            if (!(locale == "zh_TW")) {
                                mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationNameById(endStnId, locale) //+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                            } else {
                                mDisplayText = eachOtherStation + " " + mUtils!!.findSimplyStationNameById(endStnId, locale) //+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                            }
                            break
                        }
                    }
                }
                Log.d("End station :" + mDisplayText + " RowTYpe 3")
                val itemDetail: RowItem = RowItem(3, mDisplayText, itemLine, endStnId.toString() + "")
                rowItems?.add(itemDetail)
            }
        }
        val adapter: CustomBaseAdapter = CustomBaseAdapter(this@TransferDetailActivity, rowItems as ArrayList<RowItem>)
        listView!!.adapter = adapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 什麼都不用寫
        } else {
            // 什麼都不用寫
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            swipeStartX = ev.x
            swipeStartY = ev.y
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            if (ev.x - swipeStartX > 200) {
                finishActivity()
                return true
            }
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (ev.x - swipeStartX > 200) {
                finishActivity()
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
    }
}