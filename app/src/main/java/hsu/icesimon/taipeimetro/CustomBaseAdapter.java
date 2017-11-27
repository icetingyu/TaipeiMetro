package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 15/3/28.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class CustomBaseAdapter extends BaseAdapter {
    Context context;
    List<RowItem> rowItems;
    List<RowItem> localRowItems = new ArrayList<RowItem>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_ITEM = 1;
    LayoutInflater mInflater;
    ArrayList<String> links = new ArrayList<String>();

    public CustomBaseAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

    }

    // So Strange, without this the holder will show duplicate items.
    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return rowItems.size();
    }

    @Override
    public RowItem getItem(int position) {
        // TODO Auto-generated method stub
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // .d("type : "+ (sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM));
//        return (rowItems.get(position).getType() == 0) ? TYPE_SEPARATOR : TYPE_ITEM;
        return rowItems.get(position).getType();

    }

    public String getItemDesc(int position) {

        return rowItems.get(position).getDesc();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
//        Log.d("rowType : "+rowType);
        if (convertView == null) {
            holder = new ViewHolder();
            if (rowType == 0)
            {
                convertView = mInflater.inflate(R.layout.route_start, null);
                holder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                holder.stationNum = (TextView) convertView.findViewById(R.id.stationNum);
                holder.container = (RelativeLayout) convertView.findViewById(R.id.routeContainer);
                holder.startLineIcon = (ImageView) convertView.findViewById(R.id.startLineIcon);
            }
            else if (rowType == 1)
            {
                convertView = mInflater.inflate(R.layout.route_progress, null);
                holder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                holder.stationNum = (TextView) convertView.findViewById(R.id.stationNum);
                holder.container = (RelativeLayout) convertView.findViewById(R.id.routeContainer);
                holder.startLineIcon = (ImageView) convertView.findViewById(R.id.progressLineIcon);
            }
            else if (rowType == 2)
            {
                convertView = mInflater.inflate(R.layout.route_transfer, null);
                holder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                holder.stationNum = (TextView) convertView.findViewById(R.id.stationNum);
                holder.container = (RelativeLayout) convertView.findViewById(R.id.routeContainer);
                holder.startLineIcon = (ImageView) convertView.findViewById(R.id.startLineIcon);
            }
            else if (rowType == 3)
            {
                convertView = mInflater.inflate(R.layout.route_end, null);
                holder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                holder.stationNum = (TextView) convertView.findViewById(R.id.stationNum);
                holder.container = (RelativeLayout) convertView.findViewById(R.id.routeContainer);
                holder.startLineIcon = (ImageView) convertView.findViewById(R.id.startLineIcon);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String desc = getItem(position).getDesc();
        String line = getItem(position).getLine();
        holder.stationName.setText(desc);
        // Log.d("type: " + type + "  desc : " + desc+ " holder.txtDesc. : "+holder.txtDesc.getText());

        int bgColor = Color.parseColor("#FFFFFF");
        int whiteTextColor = Color.parseColor("#FFFFFF");
        int blackTextColor = Color.parseColor("#000000");

        holder.stationName.setTextColor(blackTextColor);
        if (rowType == 0){
            if (line.equals("1"))
            {
                holder.startLineIcon.setImageResource(R.drawable.brown_start);
            }
            else  if (line.equals("2"))
            {
                holder.startLineIcon.setImageResource(R.drawable.red_start);
            }
            else  if (line.equals("3"))
            {
                holder.startLineIcon.setImageResource(R.drawable.green_start);
            }
            else  if (line.equals("4"))
            {
                holder.startLineIcon.setImageResource(R.drawable.yellow_start);
                holder.stationName.setTextColor(blackTextColor);
            }
            else  if (line.equals("5"))
            {
                holder.startLineIcon.setImageResource(R.drawable.blue_start);
            }
        }
        else if (rowType == 1)
        {
            if (line.equals("1"))
            {
                holder.startLineIcon.setImageResource(R.drawable.brown_progress);
            }
            else  if (line.equals("2"))
            {
                holder.startLineIcon.setImageResource(R.drawable.red_progress);
            }
            else  if (line.equals("3"))
            {
                holder.startLineIcon.setImageResource(R.drawable.green_progress);
            }
            else  if (line.equals("4"))
            {
                holder.startLineIcon.setImageResource(R.drawable.yellow_progress);
                holder.stationName.setTextColor(blackTextColor);
            }
            else  if (line.equals("5"))
            {
                holder.startLineIcon.setImageResource(R.drawable.blue_progress);
            }
        }
        else if (rowType == 2)
        {
            holder.startLineIcon.setImageResource(R.drawable.transferstation);
        }
        else if (rowType == 3)
        {
            if (line.equals("1"))
            {
                holder.startLineIcon.setImageResource(R.drawable.brown_end);
            }
            else  if (line.equals("2"))
            {
                holder.startLineIcon.setImageResource(R.drawable.red_end);
            }
            else  if (line.equals("3"))
            {
                holder.startLineIcon.setImageResource(R.drawable.green_end);
            }
            else  if (line.equals("4"))
            {
                holder.startLineIcon.setImageResource(R.drawable.yellow_end);
                holder.stationName.setTextColor(blackTextColor);
            }
            else  if (line.equals("5"))
            {
                holder.startLineIcon.setImageResource(R.drawable.blue_end);
            }
        }

        final RowItem rowItem = getItem(position);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String stationID = rowItem.getStationID();
                Log.d("stationID; "+stationID);
                if (!stationID.equals(""))
                {
                    Intent intent = new Intent(context, StationDetailActivity.class);
                    intent.putExtra(MainActivity.CURRENTSTATION, Integer.parseInt(stationID));
                    ((TransferDetailActivity)context).startActivityForResult(intent, Activity.RESULT_OK);
                    ((TransferDetailActivity)context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            }
        });
        return convertView;
    }

    /* private view holder class */
    private class ViewHolder {
        TextView stationName;
        TextView stationNum;
        RelativeLayout container;
        ImageView startLineIcon;
    }
}