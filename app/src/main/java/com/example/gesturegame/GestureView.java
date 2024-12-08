package com.example.gesturegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GestureView extends View {
    private Paint paint;
    private Path path;
    private List<PointF> points; // To store the gesture points
    private OnGestureDetectedListener listener;

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFFFFFF); // Default color (white)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12f); // Default stroke width
        paint.setAntiAlias(true);

        path = new Path();
        points = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(), event.getY());
                points.clear();
                points.add(new PointF(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                points.add(new PointF(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                points.add(new PointF(event.getX(), event.getY()));
                analyzeGesture();
                path.reset();
                break;
        }
        invalidate(); // Redraw the view to reflect changes
        return true;
    }

    private void analyzeGesture() {
        if (points.size() < 3) {
            notifyGestureDetected("Unknown Gesture");
            return;
        }

        RectF bounds = new RectF();
        path.computeBounds(bounds, true);

        float width = bounds.width();
        float height = bounds.height();
        float aspectRatio = width / height;
        float startEndDistance = distance(points.get(0), points.get(points.size() - 1));
        float totalLength = calculateStrokeLength();

        if (aspectRatio > 4.0f) {
            notifyGestureDetected("Horizontal Line");
        } else if (aspectRatio < 0.25f) {
            notifyGestureDetected("Vertical Line");
        } else if (startEndDistance / totalLength < 0.2f) {
            notifyGestureDetected("Circle");
        } else {
            notifyGestureDetected("Unknown Gesture");
        }
    }

    private float calculateStrokeLength() {
        float length = 0;
        for (int i = 1; i < points.size(); i++) {
            length += distance(points.get(i - 1), points.get(i));
        }
        return length;
    }

    private float distance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }

    private void notifyGestureDetected(String gesture) {
        if (listener != null) {
            listener.onGestureDetected(gesture);
        }
    }

    public void setLineSize(float size) {
        paint.setStrokeWidth(size);
    }

    public void setOnGestureDetectedListener(OnGestureDetectedListener listener) {
        this.listener = listener;
    }

    public interface OnGestureDetectedListener {
        void onGestureDetected(String gesture);
    }
}
