package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 3/29/15.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.google.android.material.snackbar.Snackbar;



@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class MetroMapFragment extends Fragment {
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
    private Util mUtils;

    private static final int REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS = 101;

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
        if (!locale.equals("zh_TW")) {
            myLocale = new Locale(locale);
        } else {
            myLocale = Locale.TRADITIONAL_CHINESE;
        }
        mUtils = new Util();

        Resources res = getActivity().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.d("metroFragment locale: "+locale);

        mTitle = getResources().getString(R.string.app_name);
        Log.d("mTitle:  "+mTitle);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
                if (arrow.getRotation() == 180) {
                    arrow.setRotation(0);

                }
                else {
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

                getActivity().startActivityForResult(intent, Activity.RESULT_OK);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        startStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeCalcualte();
                Intent intent = new Intent(getActivity(), StationDetailActivity.class);
                intent.putExtra(MainActivity.CURRENTSTATION, realTimeObj.getStartStnId());
                getActivity().startActivityForResult(intent, Activity.RESULT_OK);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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

    private void setup() {
        myWebView.requestFocus();
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new JavaScriptInterface(), "JSInterface");
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl("file:///android_asset/www/index.html");
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
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
                        if (!startStn.equals("null")) {
                            guideBar.setVisibility(View.VISIBLE);
                            startStation.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                            String parsedStartStn = mUtils.findStationName(startStn, locale);
                            if (!locale.equals("zh_TW")) {
                                if (parsedStartStn.length() > 11) {
                                    startStation.setTextSize(15);
                                } else {
                                    startStation.setTextSize(20);
                                }
                            }
                            else {
                                if (parsedStartStn.length() > 6) {
                                    startStation.setTextSize(15);
                                } else {
                                    startStation.setTextSize(20);
                                }
                            }
                            startStation.setText(parsedStartStn);
                        }
                        else {
                            guideBar.setVisibility(View.GONE);
                            startStation.setVisibility(View.INVISIBLE);
                            arrow.setVisibility(View.INVISIBLE);
                        }

                        if (!endStn.equals("null")) {
                            realTimeCalcualte();
                            endStation.setVisibility(View.VISIBLE);
                            String parsedStartStn = mUtils.findStationName(endStn,locale);

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
                            for (int i = 0 ; i < routeArray.length ; i++) {
                                String info = routeArray[i];
                                if (info.contains("轉乘")) {
                                    transfers++;
                                }
                            }
                            if (locale.equals("zh_TW")) {

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
        int startStnId = 0;
        int endStnId = 0;
        try {
            String startStntw = selectPair.getString("StartChs");
            String endStnzh_TW = selectPair.getString("EndChs");

            for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
                if (MainActivity.mrtStationInfo.get(i).getNametw().equals(startStntw)) {
                    startStnId = MainActivity.mrtStationInfo.get(i).getId();
                    break;
                }
            }

            for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
                if (MainActivity.mrtStationInfo.get(i).getNametw().equals(endStnzh_TW)) {
                    endStnId = MainActivity.mrtStationInfo.get(i).getId();
                    break;
                }
            }

            Gson gson = new Gson();
            for (int i = 0; i < MainActivity.rawData.size(); i++) {
                // target string = "endStnId":7,"startStnId":9
                String target = "\"endStnId\":"+endStnId+",\"startStnId\":"+startStnId;
                String data = MainActivity.rawData.get(i);
                if (data.contains(target)) {
                    Log.d("target: "+target);
                    Log.d("data: "+data);
                    MetroRouteObj obj = gson.fromJson(data, MetroRouteObj.class);
                    routeGuide = obj.getTransferInfo();
                    timeCost = obj.getTimeCost();
                    tickets = obj.getTickets();
                    break;
                }
//                MetroRouteObj obj = MainActivity.metroRouteObjs.get(i);
//                if (obj.getStartStnId() ==  startStnId && obj.getEndStnId() == endStnId) {
//
//                }
            }
            Log.d("routeGuide: "+routeGuide);
            Log.d("timeCost: "+timeCost);
            Log.d("tickets: "+tickets);


//            for (int i = 0; i < MainActivity.metroRouteObjs.size(); i++) {
//                MetroRouteObj obj = MainActivity.metroRouteObjs.get(i);
//                if (obj.getStartStnId() ==  startStnId && obj.getEndStnId() == endStnId) {
//                    routeGuide = obj.getTransferInfo();
//                    timeCost = obj.getTimeCost();
//                    tickets = obj.getTickets();
//                    break;
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        realTimeObj.setTimeCost(timeCost);
        realTimeObj.setRouteGuide(routeGuide);
        realTimeObj.setTickets(tickets);
        realTimeObj.setStartStnId(startStnId);
        realTimeObj.setEndStnId(endStnId);

    }

    public void getCurrentLocation() {
        Log.d("start of getLocation: "+System.currentTimeMillis());

        if (hasLocationPermission()) {
            gps = new GPSTracker(getActivity());
            // Check if GPS enabled
            if(gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                double shortestDistance = 1000000;
                int shortestID = 0;
                int shortestIndex = 0;

                for (int i = 0; i < MainActivity.mrtStationInfo.size() ; i++)
                {
                    double mLat = Double.parseDouble(MainActivity.mrtStationInfo.get(i).getLat());
                    double mLon = Double.parseDouble(MainActivity.mrtStationInfo.get(i).getLon());

                    double distance = gps2m(latitude,longitude, mLat, mLon);
                    if (distance < shortestDistance)
                    {
                        shortestID = MainActivity.mrtStationInfo.get(i).getId();
                        shortestIndex = i;
                        shortestDistance = distance;
                    }
                    hashMap.put(MainActivity.mrtStationInfo.get(i).getId(),(int)(distance));
                }
                Log.d("shortestID : " + shortestID + " shortestIndex :" + shortestIndex + " shortestDistance :" + shortestDistance);
                Log.d("end of getLocation: "+System.currentTimeMillis());
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.nearestStation)+ "\n" + MainActivity.mrtStationInfo.get(shortestIndex).getNametw() + " " + MainActivity.mrtStationInfo.get(shortestIndex).getNameen(), Toast.LENGTH_LONG).show();
                myWebView.loadUrl("javascript:markNearStation('" + MainActivity.mrtStationInfo.get(shortestIndex).getCustomid() + "')");
            } else {
                // Can't get location.
                // GPS or netWork is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(getView(), getString(R.string.permissionRequestExplanation), Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestAccessFineLocationPermission();
                            }
                        }).show();
            } else {
                requestAccessFineLocationPermission();
            }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // your code here, you can use newConfig.locale if you need to check the language
        // or just re-set all the labels to desired string resource
    }


    private boolean hasLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d("permission result : "+result);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("onRequestPermissionsResult requestCode: "+requestCode+" ");

        switch (requestCode) {

            case REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(getView(),
                                getString(R.string.permissionEnableHint),
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.permissionEnableSolution), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getActivity().getBaseContext().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
    private void requestAccessFineLocationPermission() {
        // requests ASSESS_FINE_LOCATION permission
        requestPermissions(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CODE_ASK_ASSESS_FINE_LOCATION_PERMISSIONS);
    }
}
