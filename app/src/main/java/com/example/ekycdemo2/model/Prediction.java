package com.example.ekycdemo2.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prediction {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("xmin")
    @Expose
    private Integer xmin;
    @SerializedName("ymin")
    @Expose
    private Integer ymin;
    @SerializedName("xmax")
    @Expose
    private Integer xmax;
    @SerializedName("ymax")
    @Expose
    private Integer ymax;
    @SerializedName("score")
    @Expose
    private Double score;
    @SerializedName("ocr_text")
    @Expose
    private String ocrText;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getXmin() {
        return xmin;
    }

    public void setXmin(Integer xmin) {
        this.xmin = xmin;
    }

    public Integer getYmin() {
        return ymin;
    }

    public void setYmin(Integer ymin) {
        this.ymin = ymin;
    }

    public Integer getXmax() {
        return xmax;
    }

    public void setXmax(Integer xmax) {
        this.xmax = xmax;
    }

    public Integer getYmax() {
        return ymax;
    }

    public void setYmax(Integer ymax) {
        this.ymax = ymax;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }

}