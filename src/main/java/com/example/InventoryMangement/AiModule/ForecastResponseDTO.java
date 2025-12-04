package com.example.InventoryMangement.AiModule;

public class ForecastResponseDTO {
    private boolean success;
    private String message;
    private String forecast;

    public ForecastResponseDTO(boolean success, String message, String forecast) {
        this.success = success;
        this.message = message;
        this.forecast = forecast;
    }


}

