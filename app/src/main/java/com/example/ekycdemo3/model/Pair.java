package com.example.ekycdemo3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pair {
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("max_threshold_to_verify")
    @Expose
    private Double maxThresholdToVerify;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("similarity_metric")
    @Expose
    private String similarityMetric;
    @SerializedName("verified")
    @Expose
    private Boolean verified;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getMaxThresholdToVerify() {
        return maxThresholdToVerify;
    }

    public void setMaxThresholdToVerify(Double maxThresholdToVerify) {
        this.maxThresholdToVerify = maxThresholdToVerify;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSimilarityMetric() {
        return similarityMetric;
    }

    public void setSimilarityMetric(String similarityMetric) {
        this.similarityMetric = similarityMetric;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "distance=" + distance +
                ", maxThresholdToVerify=" + maxThresholdToVerify +
                ", model='" + model + '\'' +
                ", similarityMetric='" + similarityMetric + '\'' +
                ", verified=" + verified +
                '}';
    }
}
