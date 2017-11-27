package hsu.icesimon.taipeimetro;

/**
 * Created by Simon Hsu on 15/4/19.
 */
public class MetroStationObj {
    int id;
    String customid;
    String nametw;
    String nameen;
    int  zip;
    String addresstw;
    String lat;
    String otherStations;
    String landmarksTW;
    String landmarksEN;

    public String getLandmarksEN() {
        return landmarksEN;
    }

    public void setLandmarksEN(String landmarksEN) {
        this.landmarksEN = landmarksEN;
    }

    public String getLandmarksTW() {
        return landmarksTW;
    }

    public void setLandmarksTW(String landmarksTW) {
        this.landmarksTW = landmarksTW;
    }

    public String getOtherStations() {
        return otherStations;
    }

    public void setOtherStations(String otherStations) {
        this.otherStations = otherStations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomid() {
        return customid;
    }

    public void setCustomid(String customid) {
        this.customid = customid;
    }

    public String getNametw() {
        return nametw;
    }

    public void setNametw(String nametw) {
        this.nametw = nametw;
    }

    public String getNameen() {
        return nameen;
    }

    public void setNameen(String nameen) {
        this.nameen = nameen;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getAddresstw() {
        return addresstw;
    }

    public void setAddresstw(String addresstw) {
        this.addresstw = addresstw;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getOfficiallinenametw() {
        return officiallinenametw;
    }

    public void setOfficiallinenametw(String officiallinenametw) {
        this.officiallinenametw = officiallinenametw;
    }

    public String getOfficiallinenameen() {
        return officiallinenameen;
    }

    public void setOfficiallinenameen(String officiallinenameen) {
        this.officiallinenameen = officiallinenameen;
    }

    public String getCustomlineid() {
        return customlineid;
    }

    public void setCustomlineid(String customlineid) {
        this.customlineid = customlineid;
    }

    String lon;
    String officiallinenametw;
    String officiallinenameen;
    String customlineid;


}
