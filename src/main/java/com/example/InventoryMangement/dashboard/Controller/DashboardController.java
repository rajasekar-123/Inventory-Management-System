package com.example.InventoryMangement.dashboard.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InventoryMangement.dashboard.dto.DashboardDTO;
import com.example.InventoryMangement.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("http://localhost:4200/")
public class DashboardController {

    @Autowired
    private DashboardService service;

    @GetMapping
    public DashboardDTO getDashboard(
            @RequestParam(defaultValue = "month") String period
    ) {
        return service.getDashboard(period);
    }
}
