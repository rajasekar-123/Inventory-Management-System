package com.example.InventoryMangement.Product.contoller;

import com.example.InventoryMangement.Product.Entity.StockAdjustment;
import com.example.InventoryMangement.Product.myservice.StockAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/product")
@CrossOrigin(origins = "http://localhost:4200")
public class StockAdjustmentContorller {

	@Autowired
	private StockAdjustmentService adjustmentService;
	
	@PostMapping("adjust-stock/{productId}")
	public String adjustStock(@PathVariable Integer productId, @RequestBody StockAdjustment adjustment)
	{
		adjustmentService.adjustStock(productId, adjustment);
		return "Stock updated successfully";
	}
}
