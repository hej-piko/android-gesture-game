package com.example.gesturegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.gesturegame.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class GestureView extends View {
    private Paint paint;
    private Path path;
    private List<PointF> points;
    private OnGestureDetectedListener listener;

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFFFFFF); // White color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12f);
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
                classifyGesture();
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    private void classifyGesture() {
        String gesture = detectShape(points);

        // Notify the listener with the result
        if (listener != null) {
            listener.onGestureDetected(gesture);
        }
    }

    private String detectShape(List<PointF> points) {
        if (points.size() < 2) {
            return "Unknown";
        }

        // Calculate the bounding box
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

        for (PointF point : points) {
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
        }

        float width = maxX - minX;
        float height = maxY - minY;

        // Determine if it's a vertical line
        if (width < height * 0.2) {
            return "Vertical Line";
        }

        // Determine if it's a horizontal line
        if (height < width * 0.2) {
            return "Horizontal Line";
        }

        // TensorFlow Lite model-based classification for other shapes
        try {
            // Load the TensorFlow Lite model
            Model model = Model.newInstance(getContext());

            // Prepare input data
            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(284 * 2 * 4); // Assuming input shape [1, 284, 2]
            inputBuffer.order(ByteOrder.nativeOrder());

            // Normalize points and fill buffer
            for (PointF point : points) {
                float normalizedX = (point.x - minX) / (maxX - minX == 0 ? 1 : maxX - minX);
                float normalizedY = (point.y - minY) / (maxY - minY == 0 ? 1 : maxY - minY);
                inputBuffer.putFloat(normalizedX);
                inputBuffer.putFloat(normalizedY);
            }

            // Fill remaining space with zeros
            int remainingPoints = 284 - points.size();
            for (int i = 0; i < remainingPoints; i++) {
                inputBuffer.putFloat(0f);
                inputBuffer.putFloat(0f);
            }

            // Load input tensor
            TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 284, 2}, DataType.FLOAT32);
            inputFeature.loadBuffer(inputBuffer);

            // Run model inference
            Model.Outputs outputs = model.process(inputFeature);
            TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();

            // Process output probabilities
            float[] probabilities = outputFeature.getFloatArray();

            // Determine the predicted gesture or unknown
            String[] labels = {"Circle", "Triangle", "Square"};
            float confidenceThreshold = 0.2f;

            int maxIndex = 0;
            for (int i = 1; i < probabilities.length; i++) {
                if (probabilities[i] > probabilities[maxIndex]) {
                    maxIndex = i;
                }
            }

            if (probabilities[maxIndex] >= confidenceThreshold) {
                return labels[maxIndex];
            }

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    public void setOnGestureDetectedListener(OnGestureDetectedListener listener) {
        this.listener = listener;
    }

    public void setLineSize(float size) {
        paint.setStrokeWidth(size);
    }

    public interface OnGestureDetectedListener {
        void onGestureDetected(String gesture);
    }
}
