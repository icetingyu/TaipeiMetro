package hsu.icesimon.taipeimetro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*

/**
 * Created by Simon Hsu on 15/3/28.
 */
class CustomBaseAdapter(var context: Context, var rowItems: List<RowItem>) : BaseAdapter() {
    var localRowItems: List<RowItem> = ArrayList()
    var mInflater: LayoutInflater
    var links = ArrayList<String>()
    private val sectionHeader = TreeSet<Int>()

    // So Strange, without this the holder will show duplicate items.
    override fun getViewTypeCount(): Int {
        return 4
    }

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return rowItems.size
    }

    override fun getItem(position: Int): RowItem {
        // TODO Auto-generated method stub
        return rowItems[position]
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        // .d("type : "+ (sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM));
//        return (rowItems.get(position).getType() == 0) ? TYPE_SEPARATOR : TYPE_ITEM;
        return rowItems[position].type
    }

    fun getItemDesc(position: Int): String? {
        return rowItems[position].desc
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        // TODO Auto-generated method stub
        var convertView = convertView
        var holder: ViewHolder? = null
        val rowType = getItemViewType(position)
        //        Log.d("rowType : "+rowType);
        if (convertView == null) {
            holder = ViewHolder()
            if (rowType == 0) {
                convertView = mInflater.inflate(R.layout.route_start, null)
                holder!!.stationName = convertView.findViewById<View>(R.id.stationName) as TextView
                holder.stationNum = convertView.findViewById<View>(R.id.stationNum) as TextView
                holder.container = convertView.findViewById<View>(R.id.routeContainer) as RelativeLayout
                holder.startLineIcon = convertView.findViewById<View>(R.id.startLineIcon) as ImageView
            } else if (rowType == 1) {
                convertView = mInflater.inflate(R.layout.route_progress, null)
                holder!!.stationName = convertView.findViewById<View>(R.id.stationName) as TextView
                holder.stationNum = convertView.findViewById<View>(R.id.stationNum) as TextView
                holder.container = convertView.findViewById<View>(R.id.routeContainer) as RelativeLayout
                holder.startLineIcon = convertView.findViewById<View>(R.id.progressLineIcon) as ImageView
            } else if (rowType == 2) {
                convertView = mInflater.inflate(R.layout.route_transfer, null)
                holder!!.stationName = convertView.findViewById<View>(R.id.stationName) as TextView
                holder.stationNum = convertView.findViewById<View>(R.id.stationNum) as TextView
                holder.container = convertView.findViewById<View>(R.id.routeContainer) as RelativeLayout
                holder.startLineIcon = convertView.findViewById<View>(R.id.startLineIcon) as ImageView
            } else if (rowType == 3) {
                convertView = mInflater.inflate(R.layout.route_end, null)
                holder!!.stationName = convertView.findViewById<View>(R.id.stationName) as TextView
                holder.stationNum = convertView.findViewById<View>(R.id.stationNum) as TextView
                holder.container = convertView.findViewById<View>(R.id.routeContainer) as RelativeLayout
                holder.startLineIcon = convertView.findViewById<View>(R.id.startLineIcon) as ImageView
            }
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val desc = getItem(position).desc
        val line = getItem(position).line
        holder!!.stationName!!.text = desc
        // Log.d("type: " + type + "  desc : " + desc+ " holder.txtDesc. : "+holder.txtDesc.getText());
        val bgColor = Color.parseColor("#FFFFFF")
        val whiteTextColor = Color.parseColor("#FFFFFF")
        val blackTextColor = Color.parseColor("#000000")
        holder.stationName!!.setTextColor(blackTextColor)
        if (rowType == 0) {
            if (line == "1") {
                holder.startLineIcon!!.setImageResource(R.drawable.brown_start)
            } else if (line == "2") {
                holder.startLineIcon!!.setImageResource(R.drawable.red_start)
            } else if (line == "3") {
                holder.startLineIcon!!.setImageResource(R.drawable.green_start)
            } else if (line == "4") {
                holder.startLineIcon!!.setImageResource(R.drawable.yellow_start)
                holder.stationName!!.setTextColor(blackTextColor)
            } else if (line == "5") {
                holder.startLineIcon!!.setImageResource(R.drawable.blue_start)
            }
        } else if (rowType == 1) {
            if (line == "1") {
                holder.startLineIcon!!.setImageResource(R.drawable.brown_progress)
            } else if (line == "2") {
                holder.startLineIcon!!.setImageResource(R.drawable.red_progress)
            } else if (line == "3") {
                holder.startLineIcon!!.setImageResource(R.drawable.green_progress)
            } else if (line == "4") {
                holder.startLineIcon!!.setImageResource(R.drawable.yellow_progress)
                holder.stationName!!.setTextColor(blackTextColor)
            } else if (line == "5") {
                holder.startLineIcon!!.setImageResource(R.drawable.blue_progress)
            }
        } else if (rowType == 2) {
            holder.startLineIcon!!.setImageResource(R.drawable.transferstation)
        } else if (rowType == 3) {
            if (line == "1") {
                holder.startLineIcon!!.setImageResource(R.drawable.brown_end)
            } else if (line == "2") {
                holder.startLineIcon!!.setImageResource(R.drawable.red_end)
            } else if (line == "3") {
                holder.startLineIcon!!.setImageResource(R.drawable.green_end)
            } else if (line == "4") {
                holder.startLineIcon!!.setImageResource(R.drawable.yellow_end)
                holder.stationName!!.setTextColor(blackTextColor)
            } else if (line == "5") {
                holder.startLineIcon!!.setImageResource(R.drawable.blue_end)
            }
        }
        val rowItem = getItem(position)
        convertView.setOnClickListener {
            val stationID = rowItem.stationID
            Log.d("stationID; $stationID")
            if (stationID != "") {
                val intent = Intent(context, StationDetailActivity::class.java)
                intent.putExtra(MainActivity.CURRENTSTATION, stationID.toInt())
                (context as TransferDetailActivity).startActivityForResult(intent, Activity.RESULT_OK)
                (context as TransferDetailActivity).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
            }
        }
        return convertView
    }

    /* private view holder class */
    private inner class ViewHolder {
        var stationName: TextView? = null
        var stationNum: TextView? = null
        var container: RelativeLayout? = null
        var startLineIcon: ImageView? = null
    }

    companion object {
        private const val TYPE_SEPARATOR = 0
        private const val TYPE_ITEM = 1
    }

    init {
        mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}