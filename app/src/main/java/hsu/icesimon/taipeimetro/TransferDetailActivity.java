package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 15/3/28.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TransferDetailActivity extends Activity {
    private SwipeRefreshLayout laySwipe;
    ListView listView;
    private String routes;
    private String time;

    List<RowItem> rowItems;
    private TextView routePlanTime;
    private TextView singleRidePrice;
    private TextView easyCardPrice;
    private TextView concessionairePrice;

    private ProgressBar progressBar;
    private float swipeStartX;
    private float swipeStartY;
    private LayoutInflater mInflater;
    private ImageButton backBtn;
    private SharedPreferences mSP;
    private int startStnId;
    private int endStnId;
    private String locale;
    private Util mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_guide_layout);
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mSP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        locale = mSP.getString("locale", "");
        listView = (ListView) findViewById(R.id.list);
        rowItems = new ArrayList<RowItem>();
        routePlanTime = (TextView) findViewById(R.id.routePlanTime);
        singleRidePrice = (TextView) findViewById(R.id.singleRidePrice);
        easyCardPrice = (TextView) findViewById(R.id.easyCardPrice);
        concessionairePrice = (TextView) findViewById(R.id.concessionairePrice);
        backBtn = (ImageButton) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        Intent intent = getIntent();
        mUtils = new Util();
        routes = intent.getStringExtra(MainActivity.ROUTEPLAN);
        time = intent.getStringExtra(MainActivity.ROUTETIME).replace("約","").replace(" 分鐘", "");
        startStnId = intent.getIntExtra(MainActivity.ROUTESTART, 0);
        endStnId = intent.getIntExtra(MainActivity.ROUTEEND,0);

        String temptickets = intent.getStringExtra(MainActivity.ROUTETICKETS);
        Log.d("routes: " + routes);
        try {
            JSONArray ticketArray = new JSONArray(temptickets);
            singleRidePrice.setText(getString(R.string.priceTag)+" "+ticketArray.get(0).toString().replace("元",""));
            easyCardPrice.setText(getString(R.string.priceTag)+" "+ticketArray.get(1).toString().replace("元", ""));
            concessionairePrice.setText(getString(R.string.priceTag)+" "+ticketArray.get(2).toString().replace("元", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        routePlanTime.setText(getString(R.string.timecost)+time+getString(R.string.timeunit));

        String[] routeData = routes.split("=>");
        String itemLine = "";
        String startPreLine = "";

        for (int i = 0 ; i < routeData.length ; i++) {
            String info = routeData[i];
            String currentLine = "";
            String transferLine = "";
            String displayText = "";
            String middleText = "";
            String mDisplayText = "";

            if (info.contains("搭乘")) {
                int index = info.indexOf("搭乘");
                String tempLine  = info.substring(index + 2, index + 3);
                if (info.contains("新北投")) {
                    itemLine = "2";
                } else if (info.contains("小碧潭")) {
                    itemLine = "3";
                } else {
                    if (!tempLine.equals("1") && !tempLine.equals("2") && !tempLine.equals("3") && !tempLine.equals("4") && !tempLine.equals("5"))
                    {

                    } else {
                        itemLine = info.substring(index+2,index+3);
                    }
                }
                startPreLine = itemLine;
                // Log.d("itemLine : " + itemLine);
                String[] split = info.split("（");
                if (split.length > 0) {
                    String transferStationTW = split[0].split("搭乘")[0].trim();
                    if (transferStationTW.indexOf("車站")!= -1) {
                        Log.d("displayText before change : "+displayText);
                        displayText = transferStationTW;
                    }
                    else {
                        displayText = transferStationTW.replace("站","");
                    }
                    String[] stations = mUtils.findStationNameArrayById(startStnId);
                    middleText = mUtils.findTransferDirection(split[1].replace("）", "").trim(),locale);
                    Log.d("搭乘MiddleText  :"+middleText);
                    if (stations == null) {
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = mUtils.findStationName(startStnId,locale) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        } else {
                            mDisplayText = mUtils.findStationName(startStnId,locale) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        }
                    }
                    else {
                        for (int k = 0 ; k < stations.length ; k++) {
                            String eachOtherStation = stations[k];
                            Log.d("eachOtherStation  :"+eachOtherStation);

                            if (eachOtherStation.substring(0,1).equals(startPreLine))
                            {
                                if (!locale.equals("zh_TW")) {
                                    mDisplayText =  eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText =  eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) ;//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                }
//                                RowItem itemDetail = new RowItem(1,mDisplayText, startPreLine,"");
//
//                                rowItems.add(itemDetail);

                                break;
                            }
                        }
                    }
                }
                else {

                }
                RowItem itemDetail = new RowItem(0,mDisplayText, itemLine, startStnId+"");
                RowItem itemMiddle = new RowItem(1,middleText, itemLine, "");

                rowItems.add(itemDetail);
                rowItems.add(itemMiddle);
            }

            else if (info.contains("轉乘"))
            {
                int index = info.indexOf("轉乘");
                String tempLine  = info.substring(index + 2, index + 3);
                if (!tempLine.equals("1") && !tempLine.equals("2") && !tempLine.equals("3") && !tempLine.equals("4") && !tempLine.equals("5"))
                {

                }
                else
                {
                    itemLine = info.substring(index+2,index+3);
                }
                Log.d("轉乘 itemLine  :"+itemLine);

                String[] split = info.split("（");
                if (split.length > 0)
                {
                    middleText = (split[1].replace("）","").trim());
                    Log.d("轉乘MiddleText  :"+middleText);

                    String transferStationTW = split[0].split("轉乘")[0].trim();
                    if (transferStationTW.indexOf("車站")!= -1)
                    {
                        Log.d("displayText before change : "+displayText);

                        displayText = transferStationTW;
                    }
                    else {
                        displayText = transferStationTW.replace("站","");
                    }
                    Log.d("displayText before change : "+displayText);

                    // Check if the station is intersection
                    String[] stations = mUtils.findStationNameArray(displayText);
                    int oriStationID = mUtils.findStationIdByName(displayText);
                    if (stations == null) {
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = mUtils.findStationName(displayText,locale) + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        } else {
                            mDisplayText = mUtils.findStationName(displayText,locale) + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        }
                        if (middleText.equals("往小碧潭"))
                        {
                            itemLine = 3+"";
                        }
                        else if  (middleText.equals("往新北投"))
                        {
                            itemLine = 2+"";
                        }
                        middleText = mUtils.findTransferDirection(split[1].replace("）","").trim(),locale);

                        RowItem itemDetail = new RowItem(0,mDisplayText, itemLine, oriStationID+"");
                        RowItem itemMiddle = new RowItem(1,middleText, itemLine, "");

                        rowItems.add(itemDetail);
                        rowItems.add(itemMiddle);
                    }
                    else
                    {
                        Log.d("startPreLine  :"+startPreLine);
                        // add previous station stop
                        for (int k = 0 ; k < stations.length ; k++)
                        {
                            String eachOtherStation = stations[k];
                            Log.d("eachOtherStation  :"+eachOtherStation);
                            if (eachOtherStation.substring(0,1).equals(startPreLine))
                            {
                                if (!locale.equals("zh_TW")) {
                                    mDisplayText =  eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText =  eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) ;//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                                }
                                RowItem itemDetail = new RowItem(3,mDisplayText, startPreLine, oriStationID+"");

                                rowItems.add(itemDetail);

                                break;
                            }
                        }
                        startPreLine = tempLine;

                        // add current transfer station
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = mUtils.findStationName(displayText,locale);// + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                        } else {
                            mDisplayText = mUtils.findStationName(displayText,locale);// + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                        }
                        if (middleText.equals("往小碧潭"))
                        {
                            itemLine = 3+"";
                        }
                        else if  (middleText.equals("往新北投"))
                        {
                            itemLine = 2+"";
                        }
                        Log.d("displayText after : " + mDisplayText);
                        RowItem itemDetail = new RowItem(2,mDisplayText, itemLine, oriStationID+"");

                        rowItems.add(itemDetail);



                        for (int k = 0 ; k < stations.length ; k++)
                        {
                            String eachOtherStation = stations[k];
                            Log.d("eachOtherStation  :"+eachOtherStation);

                            if (eachOtherStation.substring(0,1).equals(itemLine)) {
                                String transferStringEn = "";
                                String transferStringTw = "";

                                if (!itemLine.equals("1") && !itemLine.equals("2") &&!itemLine.equals("3") &&!itemLine.equals("4") &&!itemLine.equals("5")) {
                                    transferStringEn = "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                                    transferStringTw = "\n"  + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                                }

                                if (!locale.equals("zh_TW")) {
                                    mDisplayText = eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) + transferStringEn;
                                } else {
                                    mDisplayText = eachOtherStation+ " "+mUtils.findSimplyStationName(displayText,locale) + transferStringTw;
                                }
                                Log.d("sfdsdfsdfd middleText : "+middleText);

                                if (middleText.equals("往小碧潭")) {
                                    itemLine = 3+"";
                                }
                                else if (middleText.equals("往新北投")) {
                                    itemLine = 2+"";
                                } else {
                                    itemLine = startPreLine;
                                }
                                Log.d("sfdsdfsdfd : "+itemLine);
                                middleText = mUtils.findTransferDirection(split[1].replace("）","").trim(),locale);

                                itemDetail = new RowItem(0,mDisplayText, itemLine, oriStationID+"");

                                middleText = mUtils.findTransferDirection(split[1].replace("）","").trim(),locale);
                                RowItem itemMiddle = new RowItem(1,middleText, itemLine , "");
                                Log.d("final itemMiddle text : "+middleText);
                                rowItems.add(itemDetail);
                                rowItems.add(itemMiddle);
                                break;
                            }
                        }
                    }
                }
                else {

                }

            }
            else {
                String[] stations = mUtils.findStationNameArrayById(endStnId);

                if (stations == null) {
                    if (!locale.equals("zh_TW")) {
                        if (!itemLine.equals("（")) {
                            mDisplayText = mUtils.findStationName(endStnId,locale) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        } else {
                            mDisplayText = mUtils.findStationName(endStnId,locale);
                        }
                    } else {
                        if (!itemLine.equals("（")) {
                            mDisplayText = mUtils.findStationName(endStnId,locale) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine),locale) + ")";
                        } else {
                            mDisplayText = mUtils.findStationName(endStnId,locale);
                        }
                    }

                }
                else {
                    for (int k = 0 ; k < stations.length ; k++) {
                        String eachOtherStation = stations[k];
                        Log.d("eachOtherStation  :"+eachOtherStation);

                        if (eachOtherStation.substring(0,1).equals(startPreLine)) {
                            if (!locale.equals("zh_TW")) {
                                mDisplayText = eachOtherStation + " " + mUtils.findSimplyStationNameById(endStnId,locale) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                            } else {
                                mDisplayText = eachOtherStation + " " + mUtils.findSimplyStationNameById(endStnId,locale);//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + mUtils.findLineName(Integer.parseInt(itemLine)) + ")";
                            }
                            break;
                        }
                    }
                }
                Log.d("End station :"+mDisplayText+ " RowTYpe 3");

                RowItem itemDetail = new RowItem(3,mDisplayText, itemLine, endStnId+"");
                rowItems.add(itemDetail);
            }
        }
        CustomBaseAdapter adapter = new CustomBaseAdapter(TransferDetailActivity.this, rowItems);
        listView.setAdapter(adapter);

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
            // 什麼都不用寫
        } else {
            // 什麼都不用寫
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            swipeStartX = ev.getX();
            swipeStartY = ev.getY();
        }
        else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (ev.getX() - swipeStartX > 200)
            {
                finishActivity();
                return true;
            }
        }
        else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (ev.getX() - swipeStartX > 200)
            {
                finishActivity();
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    private void finishActivity() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}