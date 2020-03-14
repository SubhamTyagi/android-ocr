package io.github.subhamtyagi.ocr.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;

import io.github.subhamtyagi.ocr.models.RecognizedResults;
import io.github.subhamtyagi.ocr.models.RecognizedText;


public class BoxImageView extends androidx.appcompat.widget.AppCompatImageView {

    private RecognizedResults recognizedResults;
    private Paint yellow = new Paint();
    private Paint black = new Paint();
    private Paint blue = new Paint();
    private boolean mDrawOverlay = true;

    public BoxImageView(Context context) {
        super(context);
        initializePaints();
    }

    public BoxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints();
    }

    public BoxImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePaints();
    }

    private void initializePaints() {
        yellow.setColor(Color.YELLOW);
        black.setColor(Color.BLACK);
        blue.setColor(Color.BLUE);

        yellow.setAlpha(0xaf);
        black.setAlpha(0xaf);
        blue.setAlpha(0x2f);

        yellow.setAntiAlias(true);
        black.setAntiAlias(true);
        blue.setAntiAlias(true);

        yellow.setStrokeWidth(2.0f);
        black.setStrokeWidth(4.0f);
        blue.setStrokeWidth(2.0f);

        yellow.setStyle(Paint.Style.STROKE);
        black.setStyle(Paint.Style.STROKE);
        blue.setStyle(Paint.Style.STROKE);

    }

    protected void drawTextWithinRect(Canvas canvas, String text, Rect rect, int fillColor, int outlineColor) {
        if ((text == null) || (text.length() == 0))
            return;

        canvas.save();

        int width = rect.right - rect.left+3;
        int height = rect.bottom - rect.top+3;

        //adjust size
        TextPaint textPaint = new TextPaint() ;
        textPaint.setTextSize(100);
        textPaint.setTextScaleX(1.0f);
        textPaint.setColor(fillColor);

        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        int h = bounds.bottom - bounds.top;
        float target = height * 0.9f;
        float size = (target / h) * 100f;
        textPaint.setTextSize(size);

        // adjust X scale
        textPaint.setTextScaleX(1.0f);
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int w = bounds.right - bounds.left;
        int text_h = bounds.bottom - bounds.top;
        int textBaseLine = bounds.bottom + ((height - text_h) / 2);
        float xScale = ((float) (width)) / w;
        textPaint.setTextScaleX(xScale);

        textPaint.setStyle(Style.STROKE);
        textPaint.setStrokeJoin(Join.ROUND);
        textPaint.setStrokeMiter(10);
        textPaint.setStrokeWidth(3);
        textPaint.setColor(outlineColor);
        canvas.drawText(text, rect.left/* + width / 2*/, rect.top + height - textBaseLine, textPaint);
        textPaint.setColor(fillColor);
        textPaint.setStyle(Style.FILL);
        canvas.drawText(text, rect.left/* + width / 2*/, rect.top + height - textBaseLine, textPaint);
        canvas.restore();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawOverlay) {
            canvas.save();
            Matrix m = getImageMatrix();
            canvas.concat(m);
            if (recognizedResults != null) {
                int n = recognizedResults.size();
                for (int i = 0; i < n; i++) {
                    RecognizedText item = recognizedResults.get(i);
                    canvas.drawRect(item.getRect(), blue);
                   // canvas.drawRect(item.getRect(), yellow);
                    drawTextWithinRect(canvas, item.getText(), item.getRect(), yellow.getColor(), black.getColor());

                }
            }
            canvas.restore();
        }

    }

    public void setRecognitionResults(RecognizedResults results) {
        recognizedResults = results;
        invalidate();

    }

    public void setDrawOverlay(boolean b) {
        mDrawOverlay = b;
        invalidate();
    }

}