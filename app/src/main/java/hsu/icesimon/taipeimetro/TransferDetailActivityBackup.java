//package hsu.icesimon.taipeimetro;
//
///**
// * Created by Simon Hsu on 15/3/28.
// */
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TransferDetailActivityBackup extends Activity {
//    private SwipeRefreshLayout laySwipe;
//    ListView listView;
//    private String routes;
//    private int distance;
//    List<RowItem> rowItems;
//
//    private String realTicketUrl;
//    private String eventTitle;
//    private String eventDetail;
//    private String eventPlace;
//    private String eventImageUrl;
//    private ImageView eventDetailImage;
//    private TextView routePlanTime;
//    private TextView routePlanCost;
//    private ProgressBar progressBar;
//    private float swipeStartX;
//    private float swipeStartY;
//    private LayoutInflater mInflater;
//    private ImageButton favoriteBtn;
//    private SharedPreferences mSP;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.transfer_guide_layout);
//        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        mSP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        listView = (ListView) findViewById(R.id.list);
//        rowItems = new ArrayList<RowItem>();
//
////        rowItems = new ArrayList<RowItem>();
//        routePlanTime = (TextView) findViewById(R.id.routePlanTime);
////        progressBar = (ProgressBar) findViewById(R.id.progressbar);
////        updateTime = (TextView) findViewById(R.id.updateTime);
//
//
//        Intent intent = getIntent();
//        routes = intent.getStringExtra(MainActivity.ROUTEPLAN);
//
//        distance = 0;//Integer.parseInt(intent.getStringExtra(MainActivity.ROUTEDISTANCE));
//        Log.d("distance: "+distance);
//        int time = (int)(Math.floor((double)distance / 1000 / 30 * 60*1.5));
//        routePlanTime.setText("Time: " + time + " min");
//        routePlanCost.setText("Cost: NTD " + (int)disToPriceDefault(distance));
//
//        String[] routeData = routes.split(",");
////        for (int i = 0 ; i < routeData.length ; i++)
////        {
////            RowItem itemDetail = new RowItem(1,routeData[i]);
////            RowItem itemMiddle = new RowItem(0,"");
////            rowItems.add(itemDetail);
////            if (i < routeData.length -1)
////            {
////                rowItems.add(itemMiddle);
////            }
////        }
//
//        CustomBaseAdapter adapter = new CustomBaseAdapter(TransferDetailActivityBackup.this, rowItems);
//        listView.setAdapter(adapter);
////        eventTitle = intent.getStringExtra(MainActivity.EVTITLE);
////        eventDetail = intent.getStringExtra(MainActivity.EVDETAIL);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        // TODO Auto-generated method stub
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // 什麼都不用寫
//        } else {
//            // 什麼都不用寫
//        }
//    }
//
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            swipeStartX = ev.getX();
//            swipeStartY = ev.getY();
//        }
//        else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            if (ev.getX() - swipeStartX > 200)
//            {
//                finishActivity();
//                return true;
//            }
//        }
//        else if (ev.getAction() == MotionEvent.ACTION_UP) {
//            if (ev.getX() - swipeStartX > 200)
//            {
//                finishActivity();
//                return true;
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public void onBackPressed() {
//        finishActivity();
//    }
//
//
//    private void finishActivity() {
//        this.finish();
//        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//    }
//
//    private double disToPriceDefault(int dis) {
//        if (dis <= 5000)
//        {
//            return 20;
//        }
//        if (dis <= 23000)
//        {
//            return 25 + Math.floor((dis - 5000) / 3000) * 5;
//        }
//        return 55 + Math.floor((dis - 23000) / 4000) * 5;
//    }
//}