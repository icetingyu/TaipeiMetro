package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 15/3/27.
 */

public class RowItem {
    private int type;
    private String desc;
    private String line;
    private String stationID;

    public RowItem(int type, String desc, String line, String stationID) {
        this.type = type;
        this.desc = desc;
        this.line = line;
        this.stationID = stationID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(String title) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }
}