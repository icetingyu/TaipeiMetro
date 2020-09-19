package hsu.icesimon.taipeimetro.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import hsu.icesimon.taipeimetro.utils.GPSTracker
import hsu.icesimon.taipeimetro.R
import hsu.icesimon.taipeimetro.utils.*
import hsu.icesimon.taipeimetro.models.*

import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.asin

/**
 * Created by Simon Hsu on 20/9/19.
 */

@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
class MetroMapFragment() : Fragment() {
    private val EARTH_RADIUS = 6378137.0
    var actionBar: ActionBar? = null
    var gps: GPSTracker? = null
    private var myWebView: WebView? = null
    private val javascriptRunningHandler = Handler()
    private var startStation: Button? = null
    private var endStation: Button? = null
    private var arrow: ImageButton? = null
    private var guideBtn: ImageButton? = null
    private var currentRoute: String? = null
    private var currentRouteDistance: String? = null
    private var selectPair: JSONObject? = null
    private var briefBar: RelativeLayout? = null
    private var divider: RelativeLayout? = null
    private var guideBar: LinearLayout? = null
    private var briefText: TextView? = null
    private var realTimeObj: RealTimeCalculateObj? = null
    private var locale: String? = null
    private var splashscreen: RelativeLayout? = null
    private var mTitle: String? = null
    private var mSP: SharedPreferences? = null
    private val locale2: String? = null
    private var mUtils: Util? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        mSP = PreferenceManager.getDefaultSharedPreferences(activity?.baseContext)
        locale = mSP?.getString("locale", "")
        val locale2 = Locale.getDefault().country
        Log.d("locale2: $locale2")
        var myLocale: Locale? = null
        if (locale != "zh_TW") {
            myLocale = Locale(locale)
        } else {
            myLocale = Locale.TRADITIONAL_CHINESE
        }
        mUtils = Util()
        val res = activity?.resources
        val dm = res?.displayMetrics
        val conf = res?.configuration
        conf?.locale = myLocale
        res?.updateConfiguration(conf, dm)
        Log.d("metroFragment locale: $locale")
        mTitle = resources.getString(R.string.app_name)
        Log.d("mTitle:  $mTitle")
        actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.navigationMode = ActionBar.NAVIGATION_MODE_STANDARD
        actionBar?.setTitle(mTitle)
        actionBar?.hide()
        myWebView = rootView.findViewById<View>(R.id.webview) as WebView
        realTimeObj = RealTimeCalculateObj()
        startStation = rootView.findViewById<View>(R.id.startStation) as Button
        endStation = rootView.findViewById<View>(R.id.endStation) as Button
        briefBar = rootView.findViewById<View>(R.id.briefBar) as RelativeLayout
        briefBar?.isClickable = false
        briefBar?.setOnTouchListener { v, event -> true }
        divider = rootView.findViewById<View>(R.id.divider) as RelativeLayout
        guideBar = rootView.findViewById<View>(R.id.guideBar) as LinearLayout
        briefText = rootView.findViewById<View>(R.id.briefText) as TextView
        splashscreen = rootView.findViewById<View>(R.id.splashscreen) as RelativeLayout
        arrow = rootView.findViewById<View>(R.id.arrow) as ImageButton
        guideBtn = rootView.findViewById<View>(R.id.guideBtn) as ImageButton
        arrow!!.setOnClickListener {
            if (arrow!!.rotation == 180f) {
                arrow!!.rotation = 0f
            } else {
                arrow!!.rotation = 180f
            }
        }
        guideBtn!!.setOnClickListener { //                realTimeCalcualte();
            val gson = Gson()
            // Log.d("current Locale :"+ Locale.getDefault().getCountry());
            val intent = Intent(activity, TransferDetailActivity::class.java)
            intent.putExtra(MainActivity.Companion.ROUTEPLAN, realTimeObj?.routeGuide)
            intent.putExtra(MainActivity.Companion.ROUTETIME, realTimeObj?.timeCost)
            intent.putExtra(MainActivity.Companion.ROUTETICKETS, gson.toJson(realTimeObj?.tickets))
            intent.putExtra(MainActivity.Companion.ROUTESTART, realTimeObj?.startStnId)
            intent.putExtra(MainActivity.Companion.ROUTEEND, realTimeObj?.endStnId)
            activity?.startActivityForResult(intent, Activity.RESULT_OK)
            activity?.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
        }
        startStation!!.setOnClickListener {
            realTimeCalcualte()
            val intent = Intent(activity, StationDetailActivity::class.java)
            intent.putExtra(MainActivity.Companion.CURRENTSTATION, realTimeObj?.startStnId)
            activity?.startActivityForResult(intent, Activity.RESULT_OK)
            activity?.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
        }
        endStation!!.setOnClickListener {
            val intent = Intent(activity, StationDetailActivity::class.java)
            intent.putExtra(MainActivity.Companion.CURRENTSTATION, realTimeObj?.endStnId)
            (activity as MainActivity?)!!.startActivityForResult(intent, Activity.RESULT_OK)
            (activity as MainActivity?)!!.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
        }
        setup()
        return rootView
    }

    private fun setup() {
        myWebView!!.requestFocus()
        myWebView!!.settings.javaScriptEnabled = true
        myWebView!!.settings.builtInZoomControls = true
        myWebView!!.settings.displayZoomControls = false
        myWebView!!.settings.useWideViewPort = true
        myWebView!!.settings.domStorageEnabled = true
        myWebView!!.addJavascriptInterface(JavaScriptInterface(), "JSInterface")
        myWebView!!.webViewClient = MyWebViewClient()
        myWebView!!.loadUrl("file:///android_asset/www/index.html")
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    fun stop() {
        /*
         * Need to set webView visibility to GONE
         * Or you will get the error Receiver not registered: android.widget.ZoomButtonsController
         * When you destroy the webview but the zoomButtonController hasn't been showed up.
         */
        myWebView!!.visibility = View.GONE
        myWebView!!.destroy()
    }

    private fun realTimeCalcualte() {
        var timeCost: String = ""
        var routeGuide: String = ""
        var tickets: ArrayList<String> = ArrayList()
        var startStnId = 0
        var endStnId = 0
        try {
            val startStntw = selectPair!!.getString("StartChs")
            val endStnzh_TW = selectPair!!.getString("EndChs")
            for (i in MainActivity.Companion.mrtStationInfo?.indices) {
                if ((MainActivity.Companion.mrtStationInfo[i].nametw == startStntw)) {
                    startStnId = MainActivity.Companion.mrtStationInfo[i].id
                    break
                }
            }
            for (i in MainActivity.Companion.mrtStationInfo.indices) {
                if ((MainActivity.Companion.mrtStationInfo[i].nametw == endStnzh_TW)) {
                    endStnId = MainActivity.Companion.mrtStationInfo[i].id
                    break
                }
            }
            val gson = Gson()
            for (i in MainActivity.Companion.rawData.indices) {
                // target string = "endStnId":7,"startStnId":9
                val target = "\"endStnId\":$endStnId,\"startStnId\":$startStnId"
                var data: String = MainActivity.Companion.rawData[i]
                if (data.contains(target)) {
                    Log.d("target: $target")
                    Log.d("data: $data")
                    val obj = gson.fromJson<MetroRouteObj>(data, MetroRouteObj::class.java)
                    routeGuide = obj?.transferInfo.toString()
                    timeCost = obj?.timeCost.toString()
                    tickets = obj?.tickets!!
                    break
                }
            }
            Log.d("routeGuide: $routeGuide")
            Log.d("timeCost: $timeCost")
            Log.d("tickets: $tickets")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        realTimeObj?.timeCost = timeCost
        realTimeObj?.routeGuide = (routeGuide)
        realTimeObj?.tickets = tickets!!
        realTimeObj?.startStnId = (startStnId)
        realTimeObj?.endStnId = (endStnId)
    }// Can't get location.

    // GPS or netWork is not enabled.
    // Ask user to enable GPS/network in settings.
    // Check if GPS enabled
    val currentLocation: Unit
        get() {
            Log.d("start of getLocation: " + System.currentTimeMillis())
            if (hasLocationPermission()) {
                gps = GPSTracker(activity)
                // Check if GPS enabled
                if (gps!!.canGetLocation()) {
                    val latitude = gps!!.getLatitude()
                    val longitude = gps!!.getLongitude()
                    val hashMap = HashMap<Int, Int>()
                    var shortestDistance = 1000000.0
                    var shortestID = 0
                    var shortestIndex = 0
                    for (i in MainActivity.Companion.mrtStationInfo.indices) {
                        val mLat: Double? = MainActivity.Companion.mrtStationInfo[i].lat?.toDouble()
                        val mLon: Double? = MainActivity.Companion.mrtStationInfo[i].lon?.toDouble()
                        val distance = gps2m(latitude, longitude, mLat, mLon)
                        if (distance < shortestDistance) {
                            shortestID = MainActivity.Companion.mrtStationInfo[i].id
                            shortestIndex = i
                            shortestDistance = distance
                        }
                        hashMap[MainActivity.Companion.mrtStationInfo[i].id] = (distance).toInt()
                    }
                    Log.d("shortestID : $shortestID shortestIndex :$shortestIndex shortestDistance :$shortestDistance")
                    Log.d("end of getLocation: " + System.currentTimeMillis())
                    Toast.makeText(activity, getString(R.string.nearestStation) + "\n" + MainActivity.Companion.mrtStationInfo.get(shortestIndex).nametw + " " + MainActivity.Companion.mrtStationInfo.get(shortestIndex).nameen, Toast.LENGTH_LONG).show()
                    myWebView!!.loadUrl("javascript:markNearStation('" + MainActivity.Companion.mrtStationInfo.get(shortestIndex).customid + "')")
                } else {
                    // Can't get location.
                    // GPS or netWork is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps!!.showSettingsAlert()
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale((activity)!!,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make((view)!!, getString(R.string.permissionRequestExplanation), Snackbar.LENGTH_LONG)
                            .setAction("OK") { requestAccessFineLocationPermission() }.show()
                } else {
                    requestAccessFineLocationPermission()
                }
            }
        }

    private fun gps2m(lat_a: Double, lng_a: Double, lat_b: Double?, lng_b: Double?): Double {
        val radLat1 = (lat_a * Math.PI / 180.0)
        val radLat2 = (lat_b!! * Math.PI / 180.0)
        val a = radLat1 - radLat2
        val b = (lng_a - lng_b!!) * Math.PI / 180.0
        var s = 2 * asin(Math.sqrt((Math.pow(Math.sin(a / 2), 2.0)
                + (Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2.0)))))
        s = s * EARTH_RADIUS
        s = Math.round(s * 10000) / 10000.toDouble()
        return s
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // your code here, you can use newConfig.locale if you need to check the language
        // or just re-set all the labels to desired string resource
    }

    private fun hasLocationPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        Log.d("permission result : $result")
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d("onRequestPermissionsResult requestCode: $requestCode ")
        when (requestCode) {
            REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentLocation
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((activity)!!,
                                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make((view)!!,
                                getString(R.string.permissionEnableHint),
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.permissionEnableSolution)) {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", activity?.baseContext?.packageName, null))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }.show()
                    }
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestAccessFineLocationPermission() {
        // requests ASSESS_FINE_LOCATION permission
        requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
        ), REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS)
    }

    private inner class MyWebViewClient() : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            actionBar!!.setDisplayShowTitleEnabled(true)
            actionBar!!.show()
            myWebView!!.postDelayed({ myWebView!!.scrollTo(1000, 1000) }, 150)
            splashscreen!!.visibility = View.GONE
            super.onPageFinished(view, url)
            // do your stuff here
        }
    }

    /*
     * 2014/03/31 By Simon Hsu
     */
    // bind this function into another thread.
    inner class JavaScriptInterface() {
        @JavascriptInterface
        fun runOnAndroidJavaScript(str: String?) {
            javascriptRunningHandler.post {
                var startStn: String? = null
                var endStn: String? = null
                try {
                    selectPair = JSONObject(str)
                    startStn = selectPair!!.getString("StartChs")
                    endStn = selectPair!!.getString("EndChs")
                    if (startStn != "null") {
                        guideBar!!.visibility = View.VISIBLE
                        startStation!!.visibility = View.VISIBLE
                        arrow!!.visibility = View.VISIBLE
                        val parsedStartStn = mUtils!!.findStationName(startStn, locale)
                        if (locale != "zh_TW") {
                            if (parsedStartStn!!.length > 11) {
                                startStation!!.textSize = 15f
                            } else {
                                startStation!!.textSize = 20f
                            }
                        } else {
                            if (parsedStartStn!!.length > 6) {
                                startStation!!.textSize = 15f
                            } else {
                                startStation!!.textSize = 20f
                            }
                        }
                        startStation!!.text = parsedStartStn
                    } else {
                        guideBar!!.visibility = View.GONE
                        startStation!!.visibility = View.INVISIBLE
                        arrow!!.visibility = View.INVISIBLE
                    }
                    if (endStn != "null") {
                        realTimeCalcualte()
                        endStation!!.visibility = View.VISIBLE
                        val parsedStartStn = mUtils!!.findStationName(endStn, locale)
                        if (locale != "zh_TW") {
                            if (parsedStartStn!!.length > 10) {
                                endStation!!.textSize = 15f
                            } else {
                                endStation!!.textSize = 20f
                            }
                        } else {
                            if (parsedStartStn!!.length > 6) {
                                endStation!!.textSize = 15f
                            } else {
                                endStation!!.textSize = 20f
                            }
                        }
                        endStation!!.text = parsedStartStn
                        guideBtn!!.visibility = View.VISIBLE
                        briefBar!!.visibility = View.VISIBLE
                        divider!!.visibility = View.VISIBLE
                        val rawRouteGuide: String = realTimeObj?.routeGuide.toString()
                        val routeArray: Array<String> = rawRouteGuide.split("=>").toTypedArray()
                        val routestations = routeArray.size - 1
                        var transfers = 0
                        for (i in routeArray.indices) {
                            val info = routeArray[i]
                            if (info.contains("轉乘")){
                                transfers++
                            }
                        }
                        if ((locale == "zh_TW")) {
                        }
                        var timesDesc = getString(R.string.times)
                        if (transfers <= 1) {
                            timesDesc = timesDesc.replace("times", "time")
                        }
                        val finalText =  // getString(R.string.from) + "<font color=\"#0000FF\">" +findStationName(startStn) +"</font>"+ getString(R.string.to) + "<font color=\"#0000FF\">"+findStationName(endStn) +"</font><br>" +
                                (getString(R.string.needTransfer) + "<font color=\"#0000FF\">" + transfers + "</font>" + timesDesc + "<font color=\"#0000FF\">" + getString(R.string.about) + realTimeObj?.timeCost?.replace("約 ", "")?.replace(" 分鐘", "") + getString(R.string.timeunit) + "</font>" +
                                        getString(R.string.arrive) + "<br>" + getString(R.string.singleRide) + ": " + "<font color=\"#0000FF\">" + getString(R.string.priceTag) + realTimeObj?.tickets?.get(0)?.replace("元", "") + "</font>")
                        briefText!!.text = Html.fromHtml(finalText)
                    } else {
                        arrow!!.rotation = 0f
                        endStation!!.visibility = View.INVISIBLE
                        guideBtn!!.visibility = View.INVISIBLE
                        briefBar!!.visibility = View.GONE
                        divider!!.visibility = View.GONE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        @JavascriptInterface
        fun showAllRoutes(str: String?) {
            javascriptRunningHandler.post(Runnable
            // put the function you'd like to run here.
            { // Log.d("showAllRoutes : "+str);
                currentRoute = str
            })
        }

        @JavascriptInterface
        fun getAllDistance(str: String?) {
            javascriptRunningHandler.post(object : Runnable {
                // put the function you'd like to run here.
                override fun run() {
                    // Log.d("showAllDistances : " + str);
                    currentRouteDistance = str
                }
            })
        }

        @JavascriptInterface
        fun alertMessage(str: String?) {
            javascriptRunningHandler.post(object : Runnable {
                // put the function you'd like to run here.
                override fun run() {
                    // Log.d("Alert Message : " + str);
                }
            })
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"
        private val REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS = 101

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): MetroMapFragment {
            val fragment = MetroMapFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}