package com.example.InventoryMangement.AiModule;

import java.util.List;

public class AdvancedForecastResponse {

    private String aiText;
    private List<PredictedPoint> chartData;

    public AdvancedForecastResponse(String aiText, List<PredictedPoint> chartData) {
        this.aiText = aiText;
        this.chartData = chartData;
    }

    public String getAiText() {
        return aiText;
    }

    public void setAiText(String aiText) {
        this.aiText = aiText;
    }

    public List<PredictedPoint> getChartData() {
        return chartData;
    }

    public void setChartData(List<PredictedPoint> chartData) {
        this.chartData = chartData;
    }
}
