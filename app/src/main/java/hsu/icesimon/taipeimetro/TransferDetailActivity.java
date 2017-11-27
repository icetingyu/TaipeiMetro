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
        routes = intent.getStringExtra(MainActivity.ROUTEPLAN);
        time = intent.getStringExtra(MainActivity.ROUTETIME).replace("約","").replace(" 分鐘", "");
        startStnId = intent.getIntExtra(MainActivity.ROUTESTART, 0);
        endStnId = intent.getIntExtra(MainActivity.ROUTEEND,0);

        String temptickets = intent.getStringExtra(MainActivity.ROUTETICKETS);
        Log.d("routes: " + routes);
        String ticketText = "";
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

        for (int i = 0 ; i < routeData.length ; i++)
        {
            String info = routeData[i];
            String currentLine = "";
            String transferLine = "";
            String displayText = "";
            String middleText = "";
            String mDisplayText = "";

            if (info.contains("搭乘"))
            {
                int index = info.indexOf("搭乘");
                String tempLine  = info.substring(index + 2, index + 3);
                if (info.contains("新北投"))
                {
                    itemLine = "2";
                }
                else if (info.contains("小碧潭")) {
                    itemLine = "3";
                }
                else {
                    if (!tempLine.equals("1") && !tempLine.equals("2") && !tempLine.equals("3") && !tempLine.equals("4") && !tempLine.equals("5"))
                    {

                    }
                    else
                    {
                        itemLine = info.substring(index+2,index+3);
                    }
                }
                startPreLine = itemLine;
                // Log.d("itemLine : " + itemLine);
                String[] split = info.split("（");
                if (split.length > 0)
                {
                    String transferStationTW = split[0].split("搭乘")[0].trim();
                    if (transferStationTW.indexOf("車站")!= -1)
                    {
                        Log.d("displayText before change : "+displayText);

                        displayText = transferStationTW;
                    }
                    else {
                        displayText = transferStationTW.replace("站","");
                    }
                    String[] stations = findStationNameArrayById(startStnId);
                    middleText = findTransferDirection(split[1].replace("）", "").trim());
                    Log.d("搭乘MiddleText  :"+middleText);
                    if (stations == null) {
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = findStationName(startStnId) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                        } else {
                            mDisplayText = findStationName(startStnId) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
                        }

                    }
                    else {
                        for (int k = 0 ; k < stations.length ; k++)
                        {
                            String eachOtherStation = stations[k];
                            Log.d("eachOtherStation  :"+eachOtherStation);

                            if (eachOtherStation.substring(0,1).equals(startPreLine))
                            {
                                if (!locale.equals("zh_TW")) {
                                    mDisplayText =  eachOtherStation+ " "+findSimplyStationName(displayText) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText =  eachOtherStation+ " "+findSimplyStationName(displayText) ;//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
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
                    String[] stations = findStationNameArray(displayText);
                    int oriStationID = findStationIdByName(displayText);
                    if (stations == null) {
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = findStationName(displayText) + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                        } else {
                            mDisplayText = findStationName(displayText) + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
                        }
                        if (middleText.equals("往小碧潭"))
                        {
                            itemLine = 3+"";
                        }
                        else if  (middleText.equals("往新北投"))
                        {
                            itemLine = 2+"";
                        }
                        middleText = findTransferDirection(split[1].replace("）","").trim());

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
                                    mDisplayText =  eachOtherStation+ " "+findSimplyStationName(displayText) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                                } else {
                                    mDisplayText =  eachOtherStation+ " "+findSimplyStationName(displayText) ;//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
                                }
                                RowItem itemDetail = new RowItem(3,mDisplayText, startPreLine, oriStationID+"");

                                rowItems.add(itemDetail);

                                break;
                            }
                        }
                        startPreLine = tempLine;

                        // add current transfer station
                        if (!locale.equals("zh_TW")) {
                            mDisplayText = findStationName(displayText);// + "\n(" + getString(R.string.transfer) + " Line " + itemLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                        } else {
                            mDisplayText = findStationName(displayText);// + "\n(" + getString(R.string.transfer) + " " + itemLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
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

                            if (eachOtherStation.substring(0,1).equals(itemLine))
                            {
                                String transferStringEn = "";
                                String transferStringTw = "";

                                if (!itemLine.equals("1") && !itemLine.equals("2") &&!itemLine.equals("3") &&!itemLine.equals("4") &&!itemLine.equals("5"))
                                {
                                    transferStringEn = "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                                    transferStringTw = "\n"  + getString(R.string.transfer) + " " + startPreLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
                                }

                                if (!locale.equals("zh_TW")) {

                                    mDisplayText = eachOtherStation+ " "+findSimplyStationName(displayText) + transferStringEn;
                                } else {
                                    mDisplayText = eachOtherStation+ " "+findSimplyStationName(displayText) + transferStringTw;
                                }
                                Log.d("sfdsdfsdfd middleText : "+middleText);

                                if (middleText.equals("往小碧潭"))
                                {
                                    itemLine = 3+"";
                                }
                                else if  (middleText.equals("往新北投"))
                                {
                                    itemLine = 2+"";
                                } else
                                {
                                    itemLine = startPreLine;
                                }
                                Log.d("sfdsdfsdfd : "+itemLine);
                                middleText = findTransferDirection(split[1].replace("）","").trim());

                                itemDetail = new RowItem(0,mDisplayText, itemLine, oriStationID+"");

                                middleText = findTransferDirection(split[1].replace("）","").trim());
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
                displayText = findStationName(endStnId);
//                Log.d("End station :"+displayText);
                String[] stations = findStationNameArrayById(endStnId);

                if (stations == null) {
                    if (!locale.equals("zh_TW")) {
                        if (!itemLine.equals("（"))
                        {
                            mDisplayText = findStationName(endStnId) + "\n(" + getString(R.string.take) + " Line " + itemLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                        }
                        else
                        {
                            mDisplayText = findStationName(endStnId);
                        }
                    } else {
                        if (!itemLine.equals("（")) {
                            mDisplayText = findStationName(endStnId) + "\n(" + getString(R.string.take) + " " + itemLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
                        }
                        else
                        {
                            mDisplayText = findStationName(endStnId);
                        }
                    }

                }
                else {
                    for (int k = 0 ; k < stations.length ; k++)
                    {
                        String eachOtherStation = stations[k];
                        Log.d("eachOtherStation  :"+eachOtherStation);

                        if (eachOtherStation.substring(0,1).equals(startPreLine))
                        {
                            if (!locale.equals("zh_TW")) {
                                mDisplayText = eachOtherStation + " " + findSimplyStationNameById(endStnId) ;//+ "\n(" + getString(R.string.transfer) + " Line " + startPreLine + " " + findLineName(Integer.parseInt(itemLine)) + ")";
                            } else {
                                mDisplayText = eachOtherStation + " " + findSimplyStationNameById(endStnId);//+ "\n(" + getString(R.string.transfer) + " " + startPreLine + " 號" + findLineName(Integer.parseInt(itemLine)) + ")";
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

    private double disToPriceDefault(int dis) {
        if (dis <= 5000)
        {
            return 20;
        }
        if (dis <= 23000)
        {
            return 25 + Math.floor((dis - 5000) / 3000) * 5;
        }
        return 55 + Math.floor((dis - 23000) / 4000) * 5;
    }

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
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNametw()  +"\n" +MainActivity.allMetroStationObjs.get(i).getNameen();;
                }
                // Log.d("stationName: "+ stationName);
                break;
            }
        }
        return stationName;
    }

    private String findStationName(String stationTW) {
        Log.d("input : "+stationTW);
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
                    stationName = MainActivity.allMetroStationObjs.get(i).getCustomid()+" "+MainActivity.allMetroStationObjs.get(i).getNametw() + "\n" + MainActivity.allMetroStationObjs.get(i).getNameen();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        Log.d("stationName:  "+stationName);
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
        if (direction.equals("往北投/往淡水"))
        {
            transferDirection = "To Beitou / To Tamsui";
        }
        else if (direction.equals("往大安/往象山"))
        {
            transferDirection = "To Daan / To Xiangshan";
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
            transferDirection = "To Taipower Building / To Xindian";
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
            transferDirection = "To Far Eastern Hospital / To Yongning";
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
        else if (direction.equals("往迴龍/往蘆洲"))
        {
            transferDirection = "To Huilong / Luzhou";
        }
        if (!locale.equals("zh_TW"))
        {
            return transferDirection;
        }
        else {
            transferDirection = direction + "\n" + transferDirection;
        }
        return transferDirection;
    }

    private String[] findStationNameArray(String stationTW) {
        String[] stationNameArray = null;
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationTW))
            {
                String customId = MainActivity.allMetroStationObjs.get(i).getCustomid();
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.allMetroStationObjs.get(i).getOtherStations());

                if (customId.startsWith("T"))
                {
                    stationNameArray = MainActivity.allMetroStationObjs.get(i).getOtherStations().split(",");
                }
                break;
            }
        }
        return stationNameArray;
    }

    private String[] findStationNameArrayById(int id) {
        String[] stationNameArray = null;
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getId() == id)
            {
                String customId = MainActivity.allMetroStationObjs.get(i).getCustomid();
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.allMetroStationObjs.get(i).getOtherStations());

                if (customId.startsWith("T"))
                {
                    stationNameArray = MainActivity.allMetroStationObjs.get(i).getOtherStations().split(",");
                }
                break;
            }
        }
        return stationNameArray;
    }

    private String findStationCustomId(int id) {
        String stationNameCustomId = null;
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getId() == id)
            {
                stationNameCustomId= MainActivity.allMetroStationObjs.get(i).getCustomid();
                break;
            }
        }
        return stationNameCustomId;
    }

    private String findSimplyStationName(String stationTW) {
//        Log.d("input : "+stationTW);
        String stationName = "";
        if (!locale.equals("zh_TW")) {
            // Log.d("search in EN");
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationTW))
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getNameen();

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
                    stationName = MainActivity.allMetroStationObjs.get(i).getNametw() +"\n" +MainActivity.allMetroStationObjs.get(i).getNameen();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
//        Log.d("stationName:  "+stationName);
        return stationName;
    }

    private String findSimplyStationNameById(int id) {
//        Log.d("input : "+id);
        String stationName = "";
        if (!locale.equals("zh_TW")) {
            // Log.d("search in EN");
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getId() == id)
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getNameen();

                     Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        else{
            for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
            {
                if (MainActivity.allMetroStationObjs.get(i).getId() == id)
                {
                    stationName = MainActivity.allMetroStationObjs.get(i).getNametw() + "\n" + MainActivity.allMetroStationObjs.get(i).getNameen();

                    // Log.d("stationName: "+ stationName);
                    break;
                }
            }
        }
        Log.d("stationName:  "+stationName);
        return stationName;
    }

    private int findStationIdByName(String stationTW) {
//        Log.d("input : "+stationTW);
        int stationId = 0;
        for (int i = 0; i < MainActivity.allMetroStationObjs.size(); i++)
        {
            if (MainActivity.allMetroStationObjs.get(i).getNametw().equals(stationTW))
            {
                stationId = MainActivity.allMetroStationObjs.get(i).getId();

                // Log.d("stationName: "+ stationName);
                break;
            }
        }
//        Log.d("stationName:  "+stationId);
        return stationId;
    }
}