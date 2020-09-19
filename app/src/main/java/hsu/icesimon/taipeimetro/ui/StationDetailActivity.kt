package hsu.icesimon.taipeimetro.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import hsu.icesimon.taipeimetro.R
import hsu.icesimon.taipeimetro.models.MetroStationObj
import hsu.icesimon.taipeimetro.ui.*
import hsu.icesimon.taipeimetro.utils.Log
import hsu.icesimon.taipeimetro.utils.Util
import kotlinx.android.synthetic.main.station_guide_layout.*

/**
 * Created by Simon Hsu on 20/9/19.
 */

class StationDetailActivity constructor() : Activity() {
    private var mInflater: LayoutInflater? = null
    private var currentStnId: Int = 0
    private var locale: String? = null
    private var currentMetroStation: MetroStationObj? = null
    private val javascriptRunningHandler: Handler = Handler()
    private var mSP: SharedPreferences? = null
    private var progressBar: ProgressDialog? = null
    private val utils: Util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station_guide_layout)
        mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        mSP = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
        locale = mSP?.getString("locale", "")
        progressBar = ProgressDialog(this@StationDetailActivity)
        setup()
//        backBtn = findViewById<View>(R.id.backBtn) as ImageButton?
        backBtn.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                finishActivity()
            }
        })
        error!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                setup()
            }
        })
        val intent: Intent = getIntent()
        currentStnId = intent.getIntExtra(MainActivity.Companion.CURRENTSTATION, 0)
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].id == currentStnId) {
                currentMetroStation = MainActivity.Companion.mrtStationInfo.get(i)
                break
            }
        }
        val stationNames: Array<String>? = findStationNameArrayById(currentStnId)
        if (stationNames == null) {
            stationNameTW.setText(currentMetroStation?.customid + " " + currentMetroStation?.nametw)
        } else {
            var addOtherStationNames: String? = ""
            for (i in stationNames.indices) {
                var stationOtherName: String = stationNames.get(i)
                if (stationOtherName.startsWith("1")) {
                    stationOtherName = "<font color=\"#c38c32\">" + stationOtherName + "</font>"
                } else if (stationOtherName.startsWith("2")) {
                    stationOtherName = "<font color=\"#e2002e\">" + stationOtherName + "</font>"
                } else if (stationOtherName.startsWith("3")) {
                    stationOtherName = "<font color=\"#01865b\">" + stationOtherName + "</font>"
                } else if (stationOtherName.startsWith("4")) {
                    stationOtherName = "<font color=\"#f6b51d\">" + stationOtherName + "</font>"
                } else if (stationOtherName.startsWith("5")) {
                    stationOtherName = "<font color=\"#0070ba\">" + stationOtherName + "</font>"
                }
                addOtherStationNames += stationOtherName
                if (i < stationNames.size - 1) {
                    addOtherStationNames += "<font color=\"#777777\">" + " / " + "</font>"
                }
            }
            otherNumbers!!.setText(Html.fromHtml(addOtherStationNames))
        }
        stationNameTW?.setText(currentMetroStation?.customid + " " + currentMetroStation?.nametw)
        stationNameEN?.setText(currentMetroStation?.nameen)
        //        Log.d("currentMetroStation.getAdditonalInfo():  "+currentMetroStation.getLandmarksEN());
        var additionalOutput: String? = ""
        var additionalInfoData: Array<String>? = null
        var additionalInfoData2: Array<String>? = null
        if (currentMetroStation?.landmarksEN != null) {
            if (!(locale == "zh_TW")) {
                additionalInfoData = currentMetroStation?.landmarksEN?.split((","))?.toTypedArray()
                if (additionalInfoData?.size!! > 0) {
                    for (j in additionalInfoData?.indices!!) {
                        additionalOutput += additionalInfoData[j]
                        if (j < additionalInfoData.size - 1) {
                            additionalOutput += "\n\n"
                        }
                    }
                }
            } else {
                additionalInfoData = currentMetroStation?.landmarksTW?.split((","))?.toTypedArray()
                additionalInfoData2 = currentMetroStation?.landmarksEN?.split((","))?.toTypedArray()
                if (additionalInfoData?.size!! > 0) {
                    for (j in additionalInfoData?.indices!!) {
                        additionalOutput += additionalInfoData?.get(j).toString() + "\n" + additionalInfoData2?.get(j)
                        if (j < additionalInfoData.size - 1) {
                            additionalOutput += "\n\n"
                        }
                    }
                }
            }
        }
        additionalInfo!!.setText(additionalOutput)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }

    public override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
    }

    private fun setup() {
        mapView!!.requestFocus()
        mapView!!.getSettings().setJavaScriptEnabled(true)
        mapView!!.getSettings().setBuiltInZoomControls(true)
        mapView!!.getSettings().setDisplayZoomControls(false)
        mapView!!.getSettings().setUseWideViewPort(true)
        mapView!!.getSettings().setDomStorageEnabled(true)
        mapView!!.addJavascriptInterface(JavaScriptInterface(), "JSInterface")
        mapView!!.setWebViewClient(MyWebViewClient())
        if (utils.isOnline(this)) {
            Log.d("isOnline")
            mapView!!.loadUrl("file:///android_asset/www/googlemap.html")
            progressBar!!.setMessage("Please wait Loading...")
            progressBar!!.show()
            error!!.setVisibility(View.GONE)
        } else {
            Log.d("isOffline")
            error!!.setVisibility(View.VISIBLE)
        }
    }

    private fun findStationNameArrayById(id: Int): Array<String>? {
        var stationNameArray: Array<String>? = null
        for (i in MainActivity.Companion.mrtStationInfo.indices) {
            if (MainActivity.Companion.mrtStationInfo[i].id == id) {
                val customId: String? = MainActivity.Companion.mrtStationInfo[i].customid
                if (customId?.startsWith("T")!!) {
                    stationNameArray = MainActivity.Companion.mrtStationInfo[i].otherStations?.split(",")?.toTypedArray()
                }
                break
            }
        }
        return stationNameArray
    }

    private inner class MyWebViewClient constructor() : WebViewClient() {
        public override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (!progressBar!!.isShowing()) {
                progressBar!!.show()
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        public override fun onPageFinished(view: WebView, url: String) {
            val lat: String? = currentMetroStation?.lat
            val lon: String? = currentMetroStation?.lon
            Log.d("lat, lon = " + lat + ", " + lon)
            mapView!!.loadUrl("javascript:initialize(" + lat + " , " + lon + ")")
            if (progressBar!!.isShowing()) {
                progressBar!!.dismiss()
            }
            super.onPageFinished(view, url)
        }

        public override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            // Handle the error
            Toast.makeText(this@StationDetailActivity, description, Toast.LENGTH_SHORT).show()
            Log.d("onReceivedError : " + description)
        } //        @TargetApi(android.os.Build.VERSION_CODES.M)
        //        @Override
        //        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
        //            // Redirect to deprecated method, so you can use it in all SDK
        //            int errorCode = rerr.getErrorCode();
        //            String errorMessage = rerr.getDescription().toString();
        //            Toast.makeText(StationDetailActivity.this, errorCode+" / "+errorMessage, Toast.LENGTH_SHORT).show();
        //            Log.d("onReceivedErrorM : "+errorCode+ " > "+errorMessage);
        //            onReceivedError(view, errorCode, errorMessage, req.getUrl().toString());
        //        }
    }

    // bind this function into another thread.
    inner class JavaScriptInterface constructor() {
        @JavascriptInterface
        fun change(str: String?) {
            javascriptRunningHandler.post(object : Runnable {
                // put the function you'd like to run here.
                public override fun run() {}
            })
        }
    }
}