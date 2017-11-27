package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 15/3/28.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
//import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class StationDetailActivity extends Activity {
    private LayoutInflater mInflater;
    private ImageButton backBtn;
    private int currentStnId;
    private String locale;
    private TextView stationNameTW;
    private TextView stationNameEN;
    private TextView otherNumbers;
    private TextView additionalInfo;
    private TextView errorTextView;
    private MetroStationObj currentMetroStation;
    private WebView mapView;
    private Handler javascriptRunningHandler = new Handler();
    private SharedPreferences mSP;
    private ProgressDialog progressBar;
    private Util utils = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_guide_layout);
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mSP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        locale = mSP.getString("locale", "");
//        Log.d("locale in StationDetail : "+locale);
        mapView = (WebView) findViewById(R.id.mapView);
        stationNameTW = (TextView) findViewById(R.id.stationNameTW);
        stationNameEN = (TextView) findViewById(R.id.stationNameEN);
        otherNumbers = (TextView) findViewById(R.id.otherNumbers);
        additionalInfo = (TextView) findViewById(R.id.additionalInfo);
        errorTextView = (TextView) findViewById(R.id.error);
        progressBar = new ProgressDialog(StationDetailActivity.this);

        setup();
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        errorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup();
            }
        });

        Intent intent = getIntent();
        currentStnId = intent.getIntExtra(MainActivity.CURRENTSTATION, 0);

        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getId() == currentStnId) {
                currentMetroStation = MainActivity.allMetroStationObjs.get(i);
                break;
            }
        }

        String[] stationNames = findStationNameArrayById(currentStnId);
        if (stationNames == null)
        {
            stationNameTW.setText(currentMetroStation.getCustomid()+" "+currentMetroStation.getNametw());
        }
        else {
            String addOtherStationNames = "";
            for (int i = 0; i < stationNames.length; i++)
            {
                String stationOtherName = stationNames[i];
                if (stationOtherName.startsWith("1"))
                {
                    stationOtherName = "<font color=\"#c38c32\">"+ stationOtherName + "</font>";
                }
                else if (stationOtherName.startsWith("2"))
                {
                    stationOtherName = "<font color=\"#e2002e\">"+ stationOtherName + "</font>";
                }
                else if (stationOtherName.startsWith("3"))
                {
                    stationOtherName = "<font color=\"#01865b\">"+ stationOtherName + "</font>";
                }
                else if (stationOtherName.startsWith("4"))
                {
                    stationOtherName = "<font color=\"#f6b51d\">"+ stationOtherName + "</font>";
                }
                else if (stationOtherName.startsWith("5"))
                {
                    stationOtherName = "<font color=\"#0070ba\">"+ stationOtherName + "</font>";
                }
                addOtherStationNames += stationOtherName;

                if ( i < stationNames.length-1)
                {
                    addOtherStationNames += "<font color=\"#777777\">"+ " / " + "</font>";
                }
            }
            otherNumbers.setText(Html.fromHtml(addOtherStationNames));
        }
        stationNameTW.setText(currentMetroStation.getCustomid()+" "+currentMetroStation.getNametw());
        stationNameEN.setText(currentMetroStation.getNameen());
//        Log.d("currentMetroStation.getAdditonalInfo():  "+currentMetroStation.getLandmarksEN());

        String additionalOutput = "";
        String[] additionalInfoData = null;
        String[] additionalInfoData2 = null;

        if (currentMetroStation.getLandmarksEN() != null) {

            if (!locale.equals("zh_TW")) {
                additionalInfoData = currentMetroStation.getLandmarksEN().split((","));
                if (additionalInfoData.length > 0) {
                    for (int j = 0; j < additionalInfoData.length; j++) {
                        additionalOutput += additionalInfoData[j];

                        if (j < additionalInfoData.length - 1) {
                            additionalOutput += "\n\n";
                        }
                    }
                }
            }
            else {
                additionalInfoData = currentMetroStation.getLandmarksTW().split((","));
                additionalInfoData2 = currentMetroStation.getLandmarksEN().split((","));
                if (additionalInfoData.length > 0) {
                    for (int j = 0; j < additionalInfoData.length; j++) {
                        additionalOutput += additionalInfoData[j]+"\n"+additionalInfoData2[j];

                        if (j < additionalInfoData.length - 1) {
                            additionalOutput += "\n\n";
                        }
                    }

                }
            }

        }
        additionalInfo.setText(additionalOutput);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    private void finishActivity() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    // Not use
    private String findStationName(int stationId) {
        String stationName = "";
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getId() == stationId)
            {
                if (!locale.equals("zh_TW"))
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNameen();
                }
                else
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNametw();
                }
                // Log.d("stationName: "+ stationName);
                break;
            }
        }
        return stationName;
    }

    private String findStationName(String stationTW) {
        String stationName = "";
        if (!locale.equals("zh_TW")) {
            // Log.d("search in EN");
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationTW))
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
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationTW))
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNametw();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        // Log.d("stationName:  "+stationName);
        return stationName;
    }

    private String findLineName(int lineId) {
        String lineEnglishName = "";
        if (!locale.equals("zh_TW"))
        {
            switch (lineId)
            {
                case 1:
                    lineEnglishName = "Wenhu Line";
                    break;
                case 2:
                    lineEnglishName = "Tamsui-Xinyi Line";
                    break;
                case 3:
                    lineEnglishName = "Songshan-Xindian Line";
                    break;
                case 4:
                    lineEnglishName = "Zhonghe-Xinlu Line";
                    break;
                case 5:
                    lineEnglishName = "Bannan Line";
                    break;
            }
        }
        else {
            switch (lineId)
            {
                case 1:
                    lineEnglishName = "文湖線";
                    break;
                case 2:
                    lineEnglishName = "淡水信義線";
                    break;
                case 3:
                    lineEnglishName = "松山新店線";
                    break;
                case 4:
                    lineEnglishName = "中和新蘆線";
                    break;
                case 5:
                    lineEnglishName = "板南線";
                    break;
            }
        }
        return lineEnglishName;
    }

    private String findTransferDirection(String direction) {
        String transferDirection = "";
        if (!locale.equals("zh_TW"))
        {
            if (direction.equals("往北投/往淡水"))
            {
                transferDirection = "To Beitou/ To Tamsui";
            }
            else if (direction.equals("往大安/往象山"))
            {
                transferDirection = "To Daan/ To Xiangshan";
            }
            else if (direction.equals("往南勢角"))
            {
                transferDirection = "To Daan/ To Xiangshan";

            }
            else if (direction.equals("往蘆洲"))
            {
                transferDirection = "To Luzhou";
            }
            else if (direction.equals("往迴龍"))
            {
                transferDirection = "To Huilong";
            }
            else if (direction.equals("往台電大樓/往新店"))
            {
                transferDirection = "To Taipower Building/ To Xindian";
            }
            else if (direction.equals("往新店"))
            {
                transferDirection = "To Xindian";
            }
            else if (direction.equals("往松山"))
            {
                transferDirection = "To SongShan";
            }
            else if (direction.equals("往南港展覽館"))
            {
                transferDirection = "To Taipei Nangang Exhibition Center";
            }
            else if (direction.equals("往動物園"))
            {
                transferDirection = "To Taipei Zoo";
            }
            else if (direction.equals("往亞東醫院/往永寧"))
            {
                transferDirection = "To Far Eastern Hospital/ To Yongning";
            }
            else if (direction.equals("往永寧"))
            {
                transferDirection = "To Yongning";
            }
            else if (direction.equals("往象山"))
            {
                transferDirection = "To Xiangshan";
            }
            else if (direction.equals("往淡水"))
            {
                transferDirection = "To Tamsui";
            }
            else if (direction.equals("往新北投"))
            {
                transferDirection = "To Xinbeitou";
            }
            else if (direction.equals("往七張"))
            {
                transferDirection = "To Qizhang";
            }
            else if (direction.equals("往北投"))
            {
                transferDirection = "To Beitou";
            }
            else if (direction.equals("往小碧潭"))
            {
                transferDirection = "To Xiaobitan";
            }
        }
        else {
            transferDirection = direction;
        }
        return transferDirection;
    }

    private void setup() {
        mapView.requestFocus();
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setBuiltInZoomControls(true);
        mapView.getSettings().setDisplayZoomControls(false);
        mapView.getSettings().setUseWideViewPort(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.addJavascriptInterface(new JavaScriptInterface(), "JSInterface");
        mapView.setWebViewClient(new MyWebViewClient());

        if (utils.isOnline(this)) {
            Log.d("isOnline");
            mapView.loadUrl("file:///android_asset/www/googlemap.html");
            progressBar.setMessage("Please wait Loading...");
            progressBar.show();
            errorTextView.setVisibility(View.GONE);
        } else {
            Log.d("isOffline");

            errorTextView.setVisibility(View.VISIBLE);
        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!progressBar.isShowing()) {
                progressBar.show();
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String lat = currentMetroStation.getLat();
            String lon = currentMetroStation.getLon();
            Log.d("lat, lon = "+lat+", "+lon);
            mapView.loadUrl("javascript:initialize("+lat+" , "+lon+")");
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            super.onPageFinished(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Handle the error
            Toast.makeText(StationDetailActivity.this, description, Toast.LENGTH_SHORT).show();
            Log.d("onReceivedError : "+description);
        }

//        @TargetApi(android.os.Build.VERSION_CODES.M)
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
    public class JavaScriptInterface {

        @JavascriptInterface
        public void change(final String str) {
            javascriptRunningHandler.post(new Runnable() {
                // put the function you'd like to run here.
                @Override
                public void run() {
                }
            });
        }
    }

    private String[] findStationNameArrayById(int id) {
        String[] stationNameArray = null;
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getId() == id)
            {
                String customId = MainActivity.allMetroStationObjs.get(i).getCustomid();

                if (customId.startsWith("T"))
                {
                    stationNameArray = MainActivity.allMetroStationObjs.get(i).getOtherStations().split(",");
                }
                break;
            }
        }
        return stationNameArray;
    }
}