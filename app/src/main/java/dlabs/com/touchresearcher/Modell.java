package dlabs.com.touchresearcher;

import java.util.ArrayList;

/**
 * Created by i321396 on 2018. 09. 08..
 */

public class Modell {

    private int maxx;
    private int maxy;
    private ArrayList<Touch> touches;
    boolean record;
    private long latestTouchTime;
    private int mesaureID;

    Modell(int maxy, int maxx,int mesaureID) {
        touches = new ArrayList<>();
        this.maxy = maxy;
        record = false;
        this.maxx= maxx;
        this.mesaureID = mesaureID;
    }
    protected void addTouch(Touch touch){
        touches.add(touch);
    }

    protected int translateCoordinate(int y){
        return (y-maxy)*(-1);
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public void clearModell(){
        touches.clear();
    }

    protected ArrayList<Touch> getTouches(){
        return touches;
    }

    public int getMaxx() {
        return maxx;
    }

    public int getMaxy() {
        return maxy;
    }

    public long getLatestTouchTime() {
        return latestTouchTime;
    }

    public void setLatestTouchTime(long latestTouchTime) {
        this.latestTouchTime = latestTouchTime;
    }

    public int getMesaureID() {
        return mesaureID;
    }

}
