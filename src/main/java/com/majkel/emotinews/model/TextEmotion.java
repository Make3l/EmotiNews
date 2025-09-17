package com.majkel.emotinews.model;

import org.w3c.dom.Text;

public class TextEmotion {
    private String label;
    private double confidence;

    public TextEmotion(){}
    public TextEmotion(String label, double confidence){
        this.label=label;
        this.confidence=confidence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
