package hsu.icesimon.taipeimetro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.RemoteException
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import hsu.icesimon.taipeimetro.App
import hsu.icesimon.taipeimetro.R
import hsu.icesimon.taipeimetro.models.MetroRouteObj
import hsu.icesimon.taipeimetro.models.MetroStationObj
import hsu.icesimon.taipeimetro.utils.LocalizationUtil
import hsu.icesimon.taipeimetro.utils.Log
import hsu.icesimon.taipeimetro.utils.PreferenceManagerUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Simon Hsu on 20/9/19.
 */

class MainActivity : AppCompatActivity() {

    var referrerClient: InstallReferrerClient? = null
    private var refer_from: TextView? = null
    val ZHTW = "zh_TW"
    val ENUS = "en_us"
    private var currentlocale: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun attachBaseContext(newBase: Context?) {
        //获取我们存储的语言环境 比如 "en","zh",等等
        currentlocale = PreferenceManagerUtil.getLocale(App.getContext())

        super.attachBaseContext(newBase?.let { LocalizationUtil.attachBaseContext(it, currentlocale) })
    }

    private fun initView() {
        val fragmentManager = supportFragmentManager
        currentlocale = PreferenceManagerUtil.getLocale(this@MainActivity)
        Log.d("Oncreate currentlocale: $currentlocale")
//
        if (currentlocale == "") {
            locale = Locale.getDefault().country
            Log.d("Oncreate locale: " + locale)
            if (locale == "TW") {
                PreferenceManagerUtil.saveLocale(this@MainActivity, ZHTW)
            } else {
                PreferenceManagerUtil.saveLocale(this@MainActivity, ENUS)
            }
        } else {
            locale = currentlocale
        }
        refer_from = findViewById(R.id.refer_from)

        fragmentManager.beginTransaction()
                .replace(R.id.container, MetroMapFragment.Companion.newInstance(1))
                .commit()
        loadData("1").execute()
        loadData("2").execute()
        setupPlayStoreConnection()
        dynamicLink
    }

    private fun readMetroRouteFileOptimized() {
        Log.d("Start read Metro Route : " + System.currentTimeMillis())
        var stream: InputStream? = null
        var isr: InputStreamReader? = null
        var input: BufferedReader? = null
        rawData.clear()
        try {
            stream = assets.open("TaipeiMetro.txt")

            isr = InputStreamReader(stream)
            input = BufferedReader(isr)

            while (true) {
                val line = input.readLine() ?: break
                rawData.add(line)
            }

        } catch (e: IOException) {
            e.message
            // Handle exceptions here
        } finally {
            try {
                isr?.close()
                stream?.close()
                input?.close()
            } catch (e2: Exception) {
                e2.message
            }
        }
        Log.d("rawData: " + rawData.size.toString())
        Log.d("End read Metro Route : " + System.currentTimeMillis())
    }

    private fun readMetroStationInfoFile() {
        var tContents = ""
        try {
            val stream = assets.open("MetroStationInfo.txt")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            tContents = String(buffer)
        } catch (e: IOException) {
            // Handle exceptions here
        }
        val gson = Gson()
        val readJson = gson.fromJson<Array<MetroStationObj>>(tContents, Array<MetroStationObj>::class.java)
        mrtStationInfo = ArrayList(Arrays.asList(*readJson))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        Log.d("OncreateOptionMenu : $currentlocale")
        if (currentlocale == "zh_TW") {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.en)
        } else {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.ch)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.location) {
            val fragment = supportFragmentManager.findFragmentById(R.id.container) as MetroMapFragment?
            fragment?.currentLocation
            return true
        } else if (id == R.id.switchLanguage) {
            Log.d("click on Switch Language before : " + currentlocale)
            if (currentlocale == "zh_TW") {
                LocalizationUtil.changeAppLanguage(this@MainActivity, "en_US")
                currentlocale = "en_US"
            } else {
                LocalizationUtil.changeAppLanguage(this@MainActivity, "zh_TW")
                currentlocale = "zh_TW"
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupPlayStoreConnection() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        Log.d("InstallReferrerClient.InstallReferrerResponse.OK")
                        if (null != referrerClient) {
                            referData
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED ->                         // API not available on the current Play Store app
                        Log.d("InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED")
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> Log.d("InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE")
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("InstallReferrerClient. onInstallReferrerServiceDisconnected")
            }
        })
    }

    private val referData: Unit
        private get() {
            var response: ReferrerDetails? = null
            try {
                response = referrerClient!!.installReferrer
                response.installReferrer
                response.referrerClickTimestampSeconds
                response.installBeginTimestampSeconds
                Log.d("getInstallReferrer = " + response.installReferrer)
                Log.d("getReferrerClickTimestampSeconds = " + response.referrerClickTimestampSeconds)
                Log.d("getInstallBeginTimestampSeconds = " + response.installBeginTimestampSeconds)
                referrerClient!!.endConnection()
                refer_from!!.visibility = View.VISIBLE
                refer_from!!.text = """
                From:${response.installReferrer}
                ClickT:${response.referrerClickTimestampSeconds}
                BeginT:${response.installBeginTimestampSeconds}
                """.trimIndent()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }// Handle invite

    private val dynamicLink: Unit
        private get() {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this) { pendingDynamicLinkData ->
                        // Get deep link from result (may be null if no link is found)
                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }
                        if (deepLink != null) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    deepLink.toString(), Snackbar.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, TAG + " deeplink " + deepLink.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG + "  getDynamicLink: no link found")
//                            Toast.makeText(applicationContext, TAG + " getDynamicLink: no link found", Toast.LENGTH_SHORT).show();

                            Snackbar.make(findViewById(android.R.id.content),
                                    "No Link Found!", Snackbar.LENGTH_LONG).show()
                        }
                        // [END_EXCLUDE]
                    }
                    .addOnFailureListener(this) { e -> Log.w(TAG + "  getDynamicLink:onFailure", e) }
                    .addOnCompleteListener { task ->
                        Log.d("task Successful : " + task.isSuccessful)
                        Log.d("task Complete : " + task.isComplete)
                        if (!task.isSuccessful) {
                            // Handle error
                            // ...
                        }
                    }
        }

    internal inner class loadData(var type: String) : AsyncTask<String?, Void?, String?>() {
        override fun onPostExecute(result: String?) {}
        override fun doInBackground(vararg params: String?): String? {
            if (type == "1") {
                readMetroStationInfoFile()
            } else {
                readMetroRouteFileOptimized()
            }
            return null
        }
    }

    companion object {
        var metroRouteObjs = ArrayList<MetroRouteObj>()
        var mrtStationInfo = ArrayList<MetroStationObj>()
        var rawData = ArrayList<String>()
        var locale: String? = null
        var ROUTEPLAN = "routeplan"
        var ROUTETIME = "routetime"
        var ROUTETICKETS = "routetickets"
        var ROUTESTART = "routestart"
        var ROUTEEND = "routeend"
        var CURRENTSTATION = "currentstation"
        private const val TAG = "MetroMain"
    }
}