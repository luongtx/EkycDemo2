package com.example.ekycdemo3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceVerification {

    @SerializedName("pair_1")
    @Expose
    private Pair pair;
    @SerializedName("seconds")
    @Expose
    private Double seconds;
    @SerializedName("trx_id")
    @Expose
    private String trxId;

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public Double getSeconds() {
        return seconds;
    }

    public void setSeconds(Double seconds) {
        this.seconds = seconds;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    @Override
    public String toString() {
        return "FaceVerification{" +
                "pair=" + pair +
                ", seconds=" + seconds +
                ", trxId='" + trxId + '\'' +
                '}';
    }
}
