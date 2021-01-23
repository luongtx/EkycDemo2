package com.example.ekycdemo3.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("input")
    @Expose
    private String input;
    @SerializedName("prediction")
    @Expose
    private List<Prediction> predictions = null;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("request_file_id")
    @Expose
    private String requestFileId;
    @SerializedName("filepath")
    @Expose
    private String filepath;
    @SerializedName("id")
    @Expose
    private String id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getRequestFileId() {
        return requestFileId;
    }

    public void setRequestFileId(String requestFileId) {
        this.requestFileId = requestFileId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}