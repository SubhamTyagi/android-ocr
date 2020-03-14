package io.github.subhamtyagi.ocr.models;


import android.graphics.Rect;

public class RecognizedText {
    private String text;
    private Rect rect;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }
}