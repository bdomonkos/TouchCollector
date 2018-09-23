package dlabs.com.touchresearcher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i321396 on 2018. 09. 08..
 */

public class SimpleDrawingView extends View {
    // setup initial color
    private final int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;
    private List<Point> circlePoints;
    private ArrayList<List<Point>> lists;
    private Modell modell;
    private Activity activity;
    Canvas canvas;



    public SimpleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        circlePoints = new ArrayList<Point>();
        lists = new ArrayList<List<Point>> ();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(modell.isRecord()){
            for (List<Point> points:lists
                 ) {
                for (Point p : points) {
                    canvas.drawPoint(p.x, p.y, drawPaint);
                }
                for(int i = 0; i < points.size()-1;i++){
                    canvas.drawLine(points.get(i).x,points.get(i).y,
                            points.get(i+1).x,points.get(i+1).y,drawPaint);
                }
            }


            for (Point p : circlePoints) {
                canvas.drawPoint(p.x, p.y, drawPaint);
            }
            for(int i = 0; i < circlePoints.size()-1;i++){
                canvas.drawLine(circlePoints.get(i).x,circlePoints.get(i).y,
                        circlePoints.get(i+1).x,circlePoints.get(i+1).y,drawPaint);
            }

        }
        canvas.drawCircle(0, modell.getMaxy(), 40, drawPaint);
        this.canvas = canvas;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(modell.isRecord()) {
            float touchX = event.getRawX();
            float touchY = event.getRawY();
            event.getOrientation();
            long time= System.currentTimeMillis();
            if(modell.getLatestTouchTime() == 0){
                time = 0;
                modell.setLatestTouchTime(System.currentTimeMillis());
            }
            else{
                time = time - modell.getLatestTouchTime();

            }

            Touch touch = new Touch(Math.round(event.getX()), modell.translateCoordinate(Math.round(event.getY())), event.getPressure(event.getActionIndex()),
                    event.getAction(),time);
            modell.addTouch(touch);
            showCurrentTouch(touch.getInformation());
            circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
            if(event.getAction() == 1){
                lists.add(circlePoints);
                circlePoints = new ArrayList<Point>();
            }
            // indicate view should be redrawn
            postInvalidate();
        }
        return true;
    }

    public Modell getModell() {
        return modell;
    }

    public void setModell(Modell modell) {
        this.modell = modell;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void showCurrentTouch(String s){
        ((TextView)activity.findViewById(R.id.textView)).setText(s);
    }
    protected void clearCanvas(){
        invalidate();
        circlePoints.clear();
        lists.clear();
    }

}
