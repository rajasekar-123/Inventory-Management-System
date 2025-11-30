package com.example.InventoryMangement.AiModule;
import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.Product.Entity.Transaction;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdvancedForecastService {

    private final TransactionRepo txRepo;
    private final ProductRepo productRepo;
    private final OllamaClient ollama;

    public AdvancedForecastService(TransactionRepo txRepo, ProductRepo productRepo, OllamaClient ollama) {
        this.txRepo = txRepo;
        this.productRepo = productRepo;
        this.ollama = ollama;
    }

    public AdvancedForecastResponse advancedForecast(Integer productId) {

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(60);

        List<Transaction> history = txRepo.findByProductIdAndDateBetween(productId, start, today);
        Product product = productRepo.findById(productId).orElse(null);

        if (product == null) {
            return new AdvancedForecastResponse("Product not found.", null);
        }

        if (history.isEmpty()) {
            return new AdvancedForecastResponse("Not enough sales history.", null);
        }

        int currentStock = product.getCurrentStock() == null ? 0 : product.getCurrentStock();

        // Build history details for AI prompt
        StringBuilder sb = new StringBuilder();
        history.forEach(tx ->
                sb.append(tx.getDate()).append(": ").append(tx.getQuantity()).append(" units\n")
        );

        String prompt = """
You are an inventory forecasting AI.

You MUST output exactly two parts:

PART 1 — SUMMARY  
Write 2–3 plain-English sentences summarizing the expected sales trend for the next 7 days.  
No lists, no headings, no rules, no examples. Only normal English sentences.

PART 2 — 7-DAY FORECAST  
Output EXACTLY these 7 lines, nothing more:

DAY1: <number>
DAY2: <number>
DAY3: <number>
DAY4: <number>
DAY5: <number>
DAY6: <number>
DAY7: <number>

Only output the number on each day. No units, no words, no explanation.

-----------------------
SALES HISTORY:
%s

CURRENT STOCK: %d
""".formatted(sb.toString(), currentStock);





        // AI TEXT output
        String aiResponse = ollama.ask(prompt);

        // -------- Parse 7-day forecast ----------
        // -------- Parse 7-day forecast (Flexible) ----------
        List<PredictedPoint> points = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().plusDays(1);

        String[] lines = aiResponse.split("\\r?\\n");

        for (String line : lines) {
            if (line == null) continue;

            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Remove dash, bullets, or extra spaces
            trimmed = trimmed.replaceFirst("^[\\-•]+\\s*", "");

            // Match formats: DAY1, Day1, Day 1, etc.
            if (!trimmed.toUpperCase().matches("^DAY\\s*\\d+\\s*:.*")) {
                continue;
            }

            try {
                // Extract day number and units
                Pattern p = Pattern.compile("DAY\\s*(\\d+)\\s*:\\s*([0-9]+)");
                Matcher m = p.matcher(trimmed.toUpperCase());

                if (!m.find()) continue;

                int dayNum = Integer.parseInt(m.group(1));
                int units = Integer.parseInt(m.group(2));

                // Add date + units
                LocalDate date = LocalDate.now().plusDays(dayNum - 1);

                points.add(new PredictedPoint(date.toString(), units));

            } catch (Exception ex) {
                System.out.println("Failed to parse line: " + trimmed);
            }
        }




        return new AdvancedForecastResponse(aiResponse, points);
    }


}

