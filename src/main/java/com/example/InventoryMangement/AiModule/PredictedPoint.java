package com.example.InventoryMangement.AiModule;

public class PredictedPoint {

    private String date;
    private int predictedUnits;

    public PredictedPoint(String date, int predictedUnits) {
        this.date = date;
        this.predictedUnits = predictedUnits;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPredictedUnits() {
        return predictedUnits;
    }

    public void setPredictedUnits(int predictedUnits) {
        this.predictedUnits = predictedUnits;
    }
}
