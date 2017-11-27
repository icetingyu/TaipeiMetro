package hsu.icesimon.taipeimetro;

import java.util.ArrayList;

/**
 * Created by Simon Hsu on 15/4/19.
 */
public class RealTimeCalculateObj {
    String timeCost;
    String routeGuide;
    ArrayList<String> tickets = new ArrayList<String>();
    int startStnId;
    int endStnId;

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

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public String getRouteGuide() {
        return routeGuide;
    }

    public void setRouteGuide(String routeGuide) {
        this.routeGuide = routeGuide;
    }

    public ArrayList<String> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<String> tickets) {
        this.tickets = tickets;
    }
}
