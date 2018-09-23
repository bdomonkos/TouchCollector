package dlabs.com.touchresearcher;

/**
 * Created by i321396 on 2018. 09. 08..
 */

public class Touch {
    private int x;
    private int y;
    private double pressure;
    private int actionType;
    private long touchTime;

    public Touch(int x, int y, double pressure,int actionType, long touchTime) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.actionType = actionType;
        this.touchTime = touchTime;
    }
    protected String getInformation(){
        return "X:"+getX() + "Y:"+getY()  + "Pressure" + getPressure();
    }

    public String getX() {
        return String.valueOf(x);
    }

    public String getY() {
        return String.valueOf(y);
    }

    public String getPressure() {
        return String.valueOf(pressure);
    }

    public String  getActionType() {
        return String.valueOf(actionType);
    }

    public String getTouchTime() {
        return String.valueOf(touchTime);
    }

    public void setTouchTime(long touchTime) {
        this.touchTime = touchTime;
    }
}
