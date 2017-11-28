package hsu.icesimon.taipeimetro;

import java.util.ArrayList;

/**
 * Created by Simon Hsu on 15/4/19.
 */

public class MetroRouteObj {
    int startStnId;
    int endStnId;
    String startStn;
    String endStn;
    ArrayList<String> tickets;
    String timeCost;
    String transferInfo;

    public int getStartStnId() {
        return startStnId;
    }

    public void setStartStnId(int startStnId) {
        this.startStnId = startStnId;
    }

    public int getEndStnId() {
        return endStnId;
    }

    public void setEndStnId(int endStnId) {
        this.endStnId = endStnId;
    }

    public String getStartStn() {
        return startStn;
    }

    public void setStartStn(String startStn) {
        this.startStn = startStn;
    }

    public String getEndStn() {
        return endStn;
    }

    public void setEndStn(String endStn) {
        this.endStn = endStn;
    }

    public ArrayList<String> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<String> tickets) {
        this.tickets = tickets;
    }

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public String getTransferInfo() {
        return transferInfo;
    }

    public void setTransferInfo(String transferInfo) {
        this.transferInfo = transferInfo;
    }
}
