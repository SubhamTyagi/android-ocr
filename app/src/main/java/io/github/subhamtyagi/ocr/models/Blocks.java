package io.github.subhamtyagi.ocr.models;


import android.graphics.Rect;


/**
 * A POJO Class contains the details of each TextBlock on Image
 */
public class Blocks {
    /***
     * A text on a particular point or box
     */
    private String text;
    /***
     * A box of text
     */
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