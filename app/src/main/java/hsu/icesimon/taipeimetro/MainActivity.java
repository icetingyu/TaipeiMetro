package hsu.icesimon.taipeimetro;


/**
 * Created by Simon Hsu on 3/29/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public static ArrayList<MetroInfoObj> allMetroInfoObjs = new ArrayList<MetroInfoObj>();
    public static ArrayList<MetroStationObj> allMetroStationObjs = new ArrayList<MetroStationObj>();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private CharSequence mTitle;
    public static String locale;
    public static String ROUTEPLAN = "routeplan";
    public static String ROUTETIME = "routetime";
    public static String ROUTETICKETS = "routetickets";
    public static String ROUTESTART = "routestart";
    public static String ROUTEEND = "routeend";
    private SharedPreferences mSP;

    public static String CURRENTSTATION = "currentstation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mSP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String currentlocale = mSP.getString("locale","");
        Log.d("Oncreate currentlocale: "+currentlocale);
        if (currentlocale.equals("")) {
            locale = Locale.getDefault().getCountry();
            Log.d("Oncreate locale: "+locale);

            if (locale.equals("TW")) {
                mSP.edit().putString("locale","zh_TW").commit();
            } else {
                mSP.edit().putString("locale","en_US").commit();
            }
        } else {
            locale = currentlocale;
        }
        locale = mSP.getString("locale","");
        Locale myLocale = null;
        if (!locale.equals("zh_TW")) {
            myLocale = new Locale(locale);
        } else {
            myLocale = Locale.TRADITIONAL_CHINESE;
        }
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        updateLanguage(myLocale);
        Log.d("Oncreate locale : "+locale);

        fragmentManager.beginTransaction()
                .replace(R.id.container, MetroMapFragment.newInstance(1))
                .commit();

        new loadData("1").execute();
        new loadData("2").execute();
    }

    private void readFile(String inFile)
    {
        String tContents = "";

        try {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        Gson gson = new Gson();
        MetroInfoObj[] readJson  = gson.fromJson(tContents, MetroInfoObj[].class);
        allMetroInfoObjs = new ArrayList<MetroInfoObj>(Arrays.asList(readJson));
    }

    private void readMetroStationInfoFile()
    {
        String tContents = "";

        try {
            InputStream stream = getAssets().open("MetroStationInfo.txt");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        Gson gson = new Gson();
        MetroStationObj[] readJson  = gson.fromJson(tContents, MetroStationObj[].class);
        allMetroStationObjs = new ArrayList<MetroStationObj>(Arrays.asList(readJson));
    }

    class loadData extends AsyncTask<String, Void, String> {
        String type;

        public loadData(String type) {
            this.type = type;
        }

        protected String doInBackground(String... urls) {
            if (type.equals("1"))
            {
                readMetroStationInfoFile();
            }
            else  {
                readFile("TaipeiMetro_full.txt");
            }
            return null;
        }

        protected void onPostExecute(String result) {
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Log.d("OncreateOptionMenu : "+mSP.getString("locale",""));
        if (mSP.getString("locale","").equals("zh_TW"))
        {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.en);
        }
        else {
            menu.findItem(R.id.switchLanguage).setIcon(R.drawable.ch);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.location) {
            MetroMapFragment fragment = (MetroMapFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.getCurrentLocation();
            return true;
        }
        else if (id == R.id.switchLanguage) {
            Log.d("click on Switch Language before : "+mSP.getString("locale",""));
            if (mSP.getString("locale","").equals("zh_TW"))
            {
                item.setIcon(R.drawable.ch);
                Locale myLocale = new Locale("en_US");
                mSP.edit().putString("locale","en_US").commit();
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
            }
            else {
                item.setIcon(R.drawable.en);
                mSP.edit().putString("locale","zh_TW").commit();

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = Locale.TRADITIONAL_CHINESE;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // your code here, you can use newConfig.locale if you need to check the language
        // or just re-set all the labels to desired string resource
    }

    private void updateLanguage(Locale locale) {
        try {
            Object objIActMag, objActMagNative;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            config.locale = locale;
            Class[] clzParams = { Configuration.class };
            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod(
                    "updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIActMag, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
