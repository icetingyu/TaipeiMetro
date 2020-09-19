package hsu.icesimon.taipeimetro.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.RemoteException
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import hsu.icesimon.taipeimetro.utils.*
import hsu.icesimon.taipeimetro.R
import hsu.icesimon.taipeimetro.models.*
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

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private val mTitle: CharSequence? = null
    private var mSP: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager = supportFragmentManager
        mSP = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val currentlocale = mSP?.getString("locale", "")
        Log.d("Oncreate currentlocale: $currentlocale")

        if (currentlocale == "") {
            locale = Locale.getDefault().country
            Log.d("Oncreate locale: " + locale)
            if (locale == "TW") {
                mSP?.edit()?.putString("locale", "zh_TW")?.commit()
            } else {
                mSP?.edit()?.putString("locale", "en_US")?.commit()
            }
        } else {
            locale = currentlocale
        }

        locale = mSP?.getString("locale", "")
        var myLocale: Locale? = null
        myLocale = if (locale != "zh_TW") {
            Locale(locale)
        } else {
            Locale.TRADITIONAL_CHINESE
        }
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
//        updateLanguage(myLocale)
        Log.d("Oncreate locale : " + locale)
        refer_from = findViewById(R.id.refer_from)

        fragmentManager.beginTransaction()
                .replace(R.id.container, MetroMapFragment.Companion.newInstance(1))
                .commit()
        loadData("1").execute()
        loadData("2").execute()
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"))
                .setDomainUriPrefix("https://taipeimetro.page.link") // Open links with this app on Android
                .setAndroidParameters(AndroidParameters.Builder("hsu.icesimon.taipeimetro").build()) // Open links with com.example.ios on iOS
                .setIosParameters(IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink()
        val dynamicLinkUri = dynamicLink.uri
        Log.d("dynamicLinkUri$dynamicLinkUri")


//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String uid = user.getUid();
//        String link = "https://mygame.example.com/?invitedby=" + uid;
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse(link))
//                .setDomainUriPrefix("https://example.page.link")
//                .setAndroidParameters(
//                        new DynamicLink.AndroidParameters.Builder("hsu.icesimon.taipeimetro")
//                                .setMinimumVersion(125)
//                                .build())
//                .setIosParameters(
//                        new DynamicLink.IosParameters.Builder("com.example.ios")
//                                .setAppStoreId("id1140000003")
//                                .setMinimumVersion("1.0.1")
//                                .build())
//                .buildShortDynamicLink()
//                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
//                    @Override
//                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
//                        Uri mInvitationUrl = shortDynamicLink.getShortLink();
//                        Log.d("mInvitationUrl" + mInvitationUrl.toString());
//
//                    }
//                });


//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        // Get deep link from result (may be null if no link is found)
//                        Uri deepLink = null;
//                        if (pendingDynamicLinkData != null) {
//                            deepLink = pendingDynamicLinkData.getLink();
//                        }
//                        Log.d("DeepLink"+ deepLink.toString());
//                        String referrerUid = deepLink.getQueryParameter("invitedby");
//                        Log.d("DeepLink invitedby "+ referrerUid);
//
//                        // Handle the deep link. For example, open the linked
//                        // content, or apply promotional credit to the user's
//                        // account.
//                        // ...
//
//                        // ...
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("getDynamicLink:onFailure", e);
//                    }
//                });
        setupPlayStoreConnection()
    }

    private fun readMetroRouteFile() {
        var tContents = ""
        try {
            val stream = assets.open("TaipeiMetro_ori.txt")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            tContents = String(buffer)
        } catch (e: IOException) {
            // Handle exceptions here
        }
        val gson = Gson()
        val readJson = gson.fromJson<Array<MetroRouteObj>>(tContents, Array<MetroRouteObj>::class.java)
        metroRouteObjs = ArrayList(Arrays.asList(*readJson))
    }

    private fun readMetroRouteFileNew() {
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
        Log.d("OncreateOptionMenu : " + mSP!!.getString("locale", ""))
        if (mSP!!.getString("locale", "") == "zh_TW") {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.en)
        } else {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.ch)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.location) {
            val fragment = supportFragmentManager.findFragmentById(R.id.container) as MetroMapFragment?
            fragment?.currentLocation
            return true
        } else if (id == R.id.switchLanguage) {
            Log.d("click on Switch Language before : " + mSP!!.getString("locale", ""))
            if (mSP!!.getString("locale", "") == "zh_TW") {
                item.setIcon(R.drawable.ch)
                val myLocale = Locale("en_US")
                mSP!!.edit().putString("locale", "en_US").commit()
                val res = resources
                val dm = res.displayMetrics
                val conf = res.configuration
                conf.locale = myLocale
                res.updateConfiguration(conf, dm)
                val refresh = Intent(this, MainActivity::class.java)
                startActivity(refresh)
            } else {
                item.setIcon(R.drawable.en)
                mSP!!.edit().putString("locale", "zh_TW").commit()
                val res = resources
                val dm = res.displayMetrics
                val conf = res.configuration
                conf.locale = Locale.TRADITIONAL_CHINESE
                res.updateConfiguration(conf, dm)
                val refresh = Intent(this, MainActivity::class.java)
                startActivity(refresh)
            }
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

    // ...
// Handle error
    // ...
    // Get deep link from result (may be null if no link is found)
    // [END_EXCLUDE]
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
                            Log.d(TAG + " deeplink " + deepLink.toString())
                        } else {
                            Log.d(TAG + "  getDynamicLink: no link found")
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
                readMetroRouteFileNew()
                //                readMetroRouteFile();
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