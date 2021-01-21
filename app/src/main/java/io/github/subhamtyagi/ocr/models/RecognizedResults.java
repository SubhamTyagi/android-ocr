package io.github.subhamtyagi.ocr.models;


import java.util.ArrayList;

/**
 * RecognizedResult: A model class to store all Blocks
 */
public class RecognizedResults {
    /***
     * full text string of whole image
     */
    private String fullText;
    /***
     *Array list to store @Blocks items that store text and its box location
     */
    private final ArrayList<Blocks> items;

    public RecognizedResults() {
        items = new ArrayList<>();
    }

    public void add(Blocks item) {
        items.add(item);
    }

    public Blocks get(int index) {
        return items.get(index);
    }

    public int size() {
        return items.size();
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

}