package com.example.InventoryMangement.AiModule;




import com.example.InventoryMangement.Product.Entity.Transaction;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ForecastService {

    private final TransactionRepo txRepo;
    private final OllamaClient ollamaClient;

    public ForecastService(TransactionRepo txRepo, OllamaClient ollamaClient) {
        this.txRepo = txRepo;
        this.ollamaClient = ollamaClient;
    }

    public String simpleForecast(Integer productId) {

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(30);

        List<Transaction> history =
                txRepo.findByProductIdAndDateBetween(productId, start, today);

        StringBuilder sb = new StringBuilder();
        history.forEach(tx ->
                sb.append(tx.getDate()).append(": ").append(tx.getQuantity()).append(" units\n")
        );

        String prompt = """
                You are an AI forecasting assistant.
                Predict the next 7 days sales based on the last 30 days data.

                Sales History:
                %s

                Output format:
                Day 1: X units
                Day 2: Y units
                ...
                Day 7: Z units

                Keep response short and simple.
                """.formatted(sb);

        return ollamaClient.ask(prompt);
    }
}
