package io.github.subhamtyagi.ocr.models;


import java.util.ArrayList;

public class RecognizedResults {
    private String  fullText;
    private ArrayList<RecognizedText> items;

    public RecognizedResults() {
        items = new ArrayList<RecognizedText>();
    }
    public void add(RecognizedText item) {
        items.add(item);
    }

    public RecognizedText get(int index) {
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

};