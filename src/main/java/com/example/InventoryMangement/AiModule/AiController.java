package com.example.InventoryMangement.AiModule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:4200/")
@RequestMapping("/api")

public class AiController {

    private final ForecastService forecastService;
    private final OllamaClient ollamaClient;
    private final AdvancedForecastService advancedForecastService;

    public AiController(ForecastService forecastService,
                        OllamaClient ollamaClient,AdvancedForecastService advancedForecastService) {
        this.forecastService = forecastService;
        this.ollamaClient = ollamaClient;
        this.advancedForecastService = advancedForecastService;
    }

    @GetMapping("/forecast/simple/{productId}")
    public String forecast(@PathVariable Integer productId) {
        return forecastService.simpleForecast(productId);
    }

    @GetMapping("/ai-test")
    public String test() {
        return ollamaClient.ask("Say hello without quotes.");
    }
    @GetMapping("/forecast/advanced/{productId}")
    public ResponseEntity<AdvancedForecastResponse> advanced(@PathVariable Integer productId) {
        return ResponseEntity.ok(advancedForecastService.advancedForecast(productId));
    }


}
