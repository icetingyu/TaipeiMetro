package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 3/29/15.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class MetroMapFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private WebView myWebView;
    private Handler javascriptRunningHandler = new Handler();
    private Button startStation;
    private Button endStation;
    private ImageButton arrow;
    private ImageButton guideBtn;
    private String currentRoute;
    private String currentRouteDistance;
    private JSONObject selectPair;
    private RelativeLayout briefBar;
    private RelativeLayout divider;
    private LinearLayout guideBar;
    private TextView briefText;
    private RealTimeCalculateObj realTimeObj;
    private String locale;
    private RelativeLayout splashscreen;
    private String mTitle;
    ActionBar actionBar;
    GPSTracker gps;
    private SharedPreferences mSP;
    private String locale2;
    private final double EARTH_RADIUS = 6378137.0;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MetroMapFragment newInstance(int sectionNumber) {
        MetroMapFragment fragment = new MetroMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public MetroMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        locale = mSP.getString("locale", "");
        String locale2 = Locale.getDefault().getCountry();
        Log.d("locale2: "+locale2);
        Locale myLocale = null;
        if (!locale.equals("zh_TW"))
        {
            myLocale = new Locale(locale);
        }
        else {
            myLocale = Locale.TRADITIONAL_CHINESE;
        }
        Resources res = getActivity().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.d("metroFragment locale: "+locale);

        mTitle = getResources().getString(R.string.app_name);
        Log.d("mTitle:  "+mTitle);
        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(mTitle);
        actionBar.hide();
        myWebView = (WebView) rootView.findViewById(R.id.webview);
        realTimeObj = new RealTimeCalculateObj();
        startStation = (Button) rootView.findViewById(R.id.startStation);
        endStation = (Button) rootView.findViewById(R.id.endStation);
        briefBar = (RelativeLayout) rootView.findViewById(R.id.briefBar);
        briefBar.setClickable(false);
        briefBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        divider = (RelativeLayout) rootView.findViewById(R.id.divider);
        guideBar = (LinearLayout) rootView.findViewById(R.id.guideBar);
        briefText = (TextView) rootView.findViewById(R.id.briefText);
        splashscreen = (RelativeLayout) rootView.findViewById(R.id.splashscreen);

        arrow = (ImageButton) rootView.findViewById(R.id.arrow);
        guideBtn = (ImageButton) rootView.findViewById(R.id.guideBtn);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrow.getRotation() == 180)
                {
                    arrow.setRotation(0);

                }
                else
                {
                    arrow.setRotation(180);
                }
            }
        });

        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                realTimeCalcualte();
                Gson gson = new Gson();
                // Log.d("current Locale :"+ Locale.getDefault().getCountry());
                Intent intent = new Intent(getActivity(), TransferDetailActivity.class);
                intent.putExtra(MainActivity.ROUTEPLAN, realTimeObj.getRouteGuide());
                intent.putExtra(MainActivity.ROUTETIME, realTimeObj.getTimeCost());
                intent.putExtra(MainActivity.ROUTETICKETS, gson.toJson(realTimeObj.getTickets()));
                intent.putExtra(MainActivity.ROUTESTART, realTimeObj.getStartStnId());
                intent.putExtra(MainActivity.ROUTEEND, realTimeObj.getEndStnId());

                ((MainActivity)getActivity()).startActivityForResult(intent, Activity.RESULT_OK);
                ((MainActivity)getActivity()).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        startStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeCalcualte();
                Intent intent = new Intent(getActivity(), StationDetailActivity.class);
                intent.putExtra(MainActivity.CURRENTSTATION, realTimeObj.getStartStnId());
                ((MainActivity)getActivity()).startActivityForResult(intent, Activity.RESULT_OK);
                ((MainActivity)getActivity()).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        endStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StationDetailActivity.class);
                intent.putExtra(MainActivity.CURRENTSTATION, realTimeObj.getEndStnId());
                ((MainActivity)getActivity()).startActivityForResult(intent, Activity.RESULT_OK);
                ((MainActivity)getActivity()).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        setup();
        return rootView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//    }

    private void setup() {
        myWebView.requestFocus();
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        //        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new JavaScriptInterface(), "JSInterface");
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl("file:///android_asset/www/index.html");
//        myWebView.setPictureListener(new WebView.PictureListener() {
//
//            @Override
//            public void onNewPicture(WebView view, Picture picture) {
//                myWebView.scrollTo(1000, 1000);
//
//            }
//        });
//        myWebView.getSettings().setDefaultZoom(Z);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Log.d("onPageFinished : " + System.currentTimeMillis());
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.show();
            myWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myWebView.scrollTo(1000, 1000);
                }
            }, 150);
            splashscreen.setVisibility(View.GONE);
            super.onPageFinished(view, url);
            // do your stuff here
        }
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    public void stop() {
        /*
         * Need to set webView visibility to GONE
         * Or you will get the error Receiver not registered: android.widget.ZoomButtonsController
         * When you destroy the webview but the zoomButtonController hasn't been showed up.
         */
        myWebView.setVisibility(View.GONE);
        myWebView.destroy();
    }

    /*
     * 2014/03/31 By Simon Hsu
     */
    // bind this function into another thread.
    public class JavaScriptInterface {
        @JavascriptInterface
        public void runOnAndroidJavaScript(final String str) {
            javascriptRunningHandler.post(new Runnable() {
                // put the function you'd like to run here.
                @Override
                public void run() {
                    String startStn = null;
                    String endStn = null;
                    try {
                        selectPair = new JSONObject(str);
                        startStn = selectPair.getString("StartChs");
                        endStn = selectPair.getString("EndChs");
                        if (!startStn.equals("null"))
                        {
                            guideBar.setVisibility(View.VISIBLE);
                            startStation.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                            String parsedStartStn = findStationName(startStn);
                            if (!locale.equals("zh_TW")) {
                                if (parsedStartStn.length() > 11) {
                                    startStation.setTextSize(15);
                                } else {
                                    startStation.setTextSize(20);
                                }
                            }
                            else {
                                if (parsedStartStn.length() > 6)
                                {
                                    startStation.setTextSize(15);
                                }
                                else
                                {
                                    startStation.setTextSize(20);
                                }
                            }
                            startStation.setText(parsedStartStn);
                        }
                        else
                        {
                            guideBar.setVisibility(View.GONE);
                            startStation.setVisibility(View.INVISIBLE);
                            arrow.setVisibility(View.INVISIBLE);
                        }
                        if (!endStn.equals("null"))
                        {
                            realTimeCalcualte();
                            endStation.setVisibility(View.VISIBLE);
                            String parsedStartStn = findStationName(endStn);

                            if (!locale.equals("zh_TW")) {
                                if (parsedStartStn.length() > 10) {
                                    endStation.setTextSize(15);
                                } else {
                                    endStation.setTextSize(20);
                                }
                            } else {
                                if (parsedStartStn.length() > 6) {
                                    endStation.setTextSize(15);
                                } else {
                                    endStation.setTextSize(20);
                                }
                            }
                            endStation.setText(parsedStartStn);
                            guideBtn.setVisibility(View.VISIBLE);
                            briefBar.setVisibility(View.VISIBLE);
                            divider.setVisibility(View.VISIBLE);
                            String rawRouteGuide = realTimeObj.getRouteGuide();
                            String[] routeArray = rawRouteGuide.split("=>");
                            int routestations = routeArray.length-1;
                            int transfers = 0;
                            for (int i = 0 ; i < routeArray.length ; i++)
                            {
                                String info = routeArray[i];
                                if (info.contains("轉乘")) {
                                    transfers++;
                                }
                            }
                            if (locale.equals("zh_TW"))
                            {

                            }
                            String timesDesc = getString(R.string.times);
                            if (transfers <= 1)
                            {
                                timesDesc = timesDesc.replace("times", "time");
                            }
                            String finalText = // getString(R.string.from) + "<font color=\"#0000FF\">" +findStationName(startStn) +"</font>"+ getString(R.string.to) + "<font color=\"#0000FF\">"+findStationName(endStn) +"</font><br>" +
                                    getString(R.string.needTransfer) +"<font color=\"#0000FF\">"+ transfers + "</font>" + timesDesc + "<font color=\"#0000FF\">" +getString(R.string.about)+ realTimeObj.getTimeCost().replace("約 ","").replace(" 分鐘", "") + getString(R.string.timeunit)+ "</font>" +
                                               getString(R.string.arrive)+"<br>"+getString(R.string.singleRide)+": "+"<font color=\"#0000FF\">"+getString(R.string.priceTag)+realTimeObj.getTickets().get(0).replace("元","")+"</font>";
                            briefText.setText(Html.fromHtml(finalText));
                        }
                        else
                        {
                            arrow.setRotation(0);
                            endStation.setVisibility(View.INVISIBLE);
                            guideBtn.setVisibility(View.INVISIBLE);
                            briefBar.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        @JavascriptInterface
        public void showAllRoutes(final String str) {
            javascriptRunningHandler.post(new Runnable() {
                // put the function you'd like to run here.
                @Override
                public void run() {
                    // Log.d("showAllRoutes : "+str);
                    currentRoute = str;
                }
            });
        }
        @JavascriptInterface
        public void getAllDistance(final String str) {
            javascriptRunningHandler.post(new Runnable() {
                // put the function you'd like to run here.
                @Override
                public void run() {
                    // Log.d("showAllDistances : " + str);
                    currentRouteDistance = str;
                }
            });
        }

        @JavascriptInterface
        public void alertMessage(final String str) {
            javascriptRunningHandler.post(new Runnable() {
                // put the function you'd like to run here.
                @Override
                public void run() {
                    // Log.d("Alert Message : " + str);
                }
            });
        }
    }

    private void realTimeCalcualte() {
        String timeCost = "";
        String routeGuide = "";
        ArrayList<String> tickets = new ArrayList<String>();
        Gson gson = new Gson();
        int startStnId = 0;
        int endStnId = 0;
        try {
            String startStntw = selectPair.getString("StartChs");
            String endStnzh_TW = selectPair.getString("EndChs");

            // Log.d("startStntw : "+startStntw+ ". endStnzh_TW : "+endStnzh_TW);


            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(startStntw))
                {
                    startStnId = MainActivity.allMetroStationObjs.get(i).getId();
                    // Log.d("StartStation: "+gson.toJson(MainActivity.allMetroStationObjs.get(i)));
                    break;
                }
            }
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(endStnzh_TW))
                {
                    endStnId = MainActivity.allMetroStationObjs.get(i).getId();
                    // Log.d("EndStation: "+gson.toJson(MainActivity.allMetroStationObjs.get(i)));
                    break;
                }
            }
            // Log.d(" MainActivity.allMetroInfoObjs: "+ MainActivity.allMetroInfoObjs);
            // Log.d("startStnId: "+startStnId+" . endStnId : "+endStnId);
            for (int i = 0 ; i < MainActivity.allMetroInfoObjs.size(); i++)
            {
                MetroInfoObj obj = MainActivity.allMetroInfoObjs.get(i);
//                        if (obj.getStartStnId() ==  endStnId && obj.getEndStnId() == startStnId)
                if (obj.getStartStnId() ==  startStnId && obj.getEndStnId() == endStnId)
                {
                    routeGuide = obj.getTransferInfo();
                    timeCost = obj.getTimeCost();
                    tickets = obj.getTickets();
//                    // Log.d("routeGuide : "+routeGuide);
//                    // Log.d("timeCost : "+timeCost);
//                    // Log.d("tickets : "+tickets.toString());
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        realTimeObj.setTimeCost(timeCost);
        realTimeObj.setRouteGuide(routeGuide);
        realTimeObj.setTickets(tickets);
        realTimeObj.setStartStnId(startStnId);
        realTimeObj.setEndStnId(endStnId);

    }

    private String findStationName(String stationzh_TW) {
        String stationName = "";
        // Log.d("locale : "+locale);
        if (!locale.equals("zh_TW")) {
           //   Log.d("search in EN");
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationzh_TW))
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNameen();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        else{
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationzh_TW))
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNametw();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        // Log.d("stationName:  " + stationName);
        return stationName;
    }


    public void getCurrentLocation() {
        Log.d("start of getLocation: "+System.currentTimeMillis());

        gps = new GPSTracker(getActivity());

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // Log.d("latitude : " + latitude + " longitude :" + longitude);


            HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
//            Log.d("MainActivity.allMetroStationObjs.size(): " + MainActivity.allMetroStationObjs.size());
            double shortestDistance = 1000000;
            int shortestID = 0;
            int shortestIndex = 0;
            for (int i = 0 ; i < MainActivity.allMetroStationObjs.size() ; i++)
            {
                double mLat = Double.parseDouble(MainActivity.allMetroStationObjs.get(i).getLat());
                double mLon = Double.parseDouble(MainActivity.allMetroStationObjs.get(i).getLon());

                double distance = gps2m(latitude,longitude, mLat, mLon);
                if (distance < shortestDistance)
                {
                    shortestID = MainActivity.allMetroStationObjs.get(i).getId();
                    shortestIndex = i;
                    shortestDistance = distance;
                }
                // Log.d(MainActivity.allMetroStationObjs.get(i).getNametw() +" : "+distance);
                hashMap.put(MainActivity.allMetroStationObjs.get(i).getId(),(int)(distance));
            }
//            Log.d("shortestID : " + shortestID + " shortestIndex :" + shortestIndex + " shortestDistance :" + shortestDistance);
           //  Log.d(MainActivity.allMetroStationObjs.get(shortestIndex).getNametw() + " " + shortestDistance);
            Log.d("end of getLocation: "+System.currentTimeMillis());
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.nearestStation)+ "\n" + MainActivity.allMetroStationObjs.get(shortestIndex).getNametw() + " " + MainActivity.allMetroStationObjs.get(shortestIndex).getNameen(), Toast.LENGTH_LONG).show();
            myWebView.loadUrl("javascript:markNearStation('" + MainActivity.allMetroStationObjs.get(shortestIndex).getCustomid() + "')");


        } else {
            // Can't get location.
            // GPS or netWork is not enabled.
            // Ask user to enable GPS/network in settings.
            // Dynamic Permission require
            gps.showSettingsAlert();
        }
    }


    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.main, menu);
//
//        return super.getActivity().onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.locale) {
//            getCurrentLocation();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // your code here, you can use newConfig.locale if you need to check the language
        // or just re-set all the labels to desired string resource
    }
}
