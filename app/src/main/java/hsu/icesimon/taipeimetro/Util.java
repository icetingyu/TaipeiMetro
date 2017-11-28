package hsu.icesimon.taipeimetro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by simon on 2017/11/27.
 */

public class Util {

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public String findStationName(int stationId, String locale) {
        String stationName = "";
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
            if (MainActivity.mrtStationInfo.get(i).getId() == stationId) {
                if (!locale.equals("zh_TW")) {
                    stationName = MainActivity.mrtStationInfo.get(i).getCustomid()+" "+MainActivity.mrtStationInfo.get(i).getNameen();
                } else {
                    stationName = MainActivity.mrtStationInfo.get(i).getCustomid()+" "+MainActivity.mrtStationInfo.get(i).getNametw()  +"\n" +MainActivity.mrtStationInfo.get(i).getNameen();;
                }
                break;
            }
        }
        return stationName;
    }

    public String findStationName(String stationTW, String locale) {
        Log.d("input : "+stationTW);
        String stationName = "";
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
            if (MainActivity.mrtStationInfo.get(i).getNametw().equals(stationTW)) {
                if (!locale.equals("zh_TW")) {
                    // Log.d("search in EN");
                    stationName = MainActivity.mrtStationInfo.get(i).getCustomid()+" "+MainActivity.mrtStationInfo.get(i).getNameen();

                } else {
                    stationName = MainActivity.mrtStationInfo.get(i).getCustomid()+" "+MainActivity.mrtStationInfo.get(i).getNametw() + "\n" + MainActivity.mrtStationInfo.get(i).getNameen();
                }
                // Log.d("stationName: "+ stationName);
                break;
            }
        }
        Log.d("stationName:  "+stationName);
        return stationName;
    }

    public String findLineName(int lineId, String locale) {
        String lineEnglishName = "";
        if (!locale.equals("zh_TW")) {
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

    public String findTransferDirection(String direction, String locale) {
        String transferDirection = "";
        if (direction.equals("往北投/往淡水")) {
            transferDirection = "To Beitou / To Tamsui";
        }
        else if (direction.equals("往大安/往象山")) {
            transferDirection = "To Daan / To Xiangshan";
        }
        else if (direction.equals("往南勢角")) {
            transferDirection = "To Daan/ To Xiangshan";
        }
        else if (direction.equals("往蘆洲")) {
            transferDirection = "To Luzhou";
        }
        else if (direction.equals("往迴龍")) {
            transferDirection = "To Huilong";
        }
        else if (direction.equals("往台電大樓/往新店")) {
            transferDirection = "To Taipower Building / To Xindian";
        }
        else if (direction.equals("往新店")) {
            transferDirection = "To Xindian";
        }
        else if (direction.equals("往松山")) {
            transferDirection = "To SongShan";
        }
        else if (direction.equals("往南港展覽館")) {
            transferDirection = "To Taipei Nangang Exhibition Center";
        }
        else if (direction.equals("往動物園")) {
            transferDirection = "To Taipei Zoo";
        }
        else if (direction.equals("往亞東醫院/往永寧")) {
            transferDirection = "To Far Eastern Hospital / To Yongning";
        }
        else if (direction.equals("往永寧")) {
            transferDirection = "To Yongning";
        }
        else if (direction.equals("往象山")) {
            transferDirection = "To Xiangshan";
        }
        else if (direction.equals("往淡水")) {
            transferDirection = "To Tamsui";
        }
        else if (direction.equals("往新北投")) {
            transferDirection = "To Xinbeitou";
        }
        else if (direction.equals("往七張")) {
            transferDirection = "To Qizhang";
        }
        else if (direction.equals("往北投")) {
            transferDirection = "To Beitou";
        }
        else if (direction.equals("往小碧潭")) {
            transferDirection = "To Xiaobitan";
        }
        else if (direction.equals("往迴龍/往蘆洲")) {
            transferDirection = "To Huilong / Luzhou";
        }

        if (!locale.equals("zh_TW")) {
            return transferDirection;
        } else {
            transferDirection = direction + "\n" + transferDirection;
        }
        return transferDirection;
    }

    public String[] findStationNameArray(String stationTW) {
        String[] stationNameArray = null;
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++)
        {
            if (MainActivity.mrtStationInfo.get(i).getNametw().equals(stationTW))
            {
                String customId = MainActivity.mrtStationInfo.get(i).getCustomid();
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.mrtStationInfo.get(i).getOtherStations());

                if (customId.startsWith("T"))
                {
                    stationNameArray = MainActivity.mrtStationInfo.get(i).getOtherStations().split(",");
                }
                break;
            }
        }
        return stationNameArray;
    }

    public String[] findStationNameArrayById(int id) {
        String[] stationNameArray = null;
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++)
        {
            if (MainActivity.mrtStationInfo.get(i).getId() == id)
            {
                String customId = MainActivity.mrtStationInfo.get(i).getCustomid();
                // intersection
//                Log.d("customId :"+customId);
//                Log.d("customId2 :"+MainActivity.mrtStationInfo.get(i).getOtherStations());

                if (customId.startsWith("T"))
                {
                    stationNameArray = MainActivity.mrtStationInfo.get(i).getOtherStations().split(",");
                }
                break;
            }
        }
        return stationNameArray;
    }

//    private String findStationCustomId(int id) {
//        String stationNameCustomId = null;
//        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++)
//        {
//            if (MainActivity.mrtStationInfo.get(i).getId() == id)
//            {
//                stationNameCustomId= MainActivity.mrtStationInfo.get(i).getCustomid();
//                break;
//            }
//        }
//        return stationNameCustomId;
//    }

    public String findSimplyStationName(String stationTW, String locale) {
        String stationName = "";
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
            if (MainActivity.mrtStationInfo.get(i).getNametw().equals(stationTW)) {
                if (!locale.equals("zh_TW")) {
                    stationName = MainActivity.mrtStationInfo.get(i).getNameen();
                } else {
                    stationName = MainActivity.mrtStationInfo.get(i).getNametw() +"\n" +MainActivity.mrtStationInfo.get(i).getNameen();
                }
                break;
            }
        }
        return stationName;
    }

    public String findSimplyStationNameById(int id, String locale) {
        String stationName = "";
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
            if (MainActivity.mrtStationInfo.get(i).getId() == id) {
                if (!locale.equals("zh_TW")) {
                    stationName = MainActivity.mrtStationInfo.get(i).getNameen();
                } else {
                    stationName = MainActivity.mrtStationInfo.get(i).getNametw() + "\n" + MainActivity.mrtStationInfo.get(i).getNameen();
                }
                break;
            }
        }
        Log.d("stationName:  "+stationName);
        return stationName;
    }

    public int findStationIdByName(String stationTW) {
        int stationId = 0;
        for (int i = 0; i < MainActivity.mrtStationInfo.size(); i++) {
            if (MainActivity.mrtStationInfo.get(i).getNametw().equals(stationTW))
            {
                stationId = MainActivity.mrtStationInfo.get(i).getId();
                // Log.d("stationName: "+ stationName);
                break;
            }
        }
        return stationId;
    }

    public double disToPriceDefault(int dis) {
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
}
