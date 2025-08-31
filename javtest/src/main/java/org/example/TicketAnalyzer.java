package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TicketAnalyzer {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -cp target/ticket-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar org.example.TicketAnalyzer tickets.json");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            ObjectMapper mapper = new ObjectMapper();
            TicketsData data = mapper.readValue(new File(filePath), TicketsData.class);

            List<Ticket> vvoToTlvTickets = data.getTickets().stream()
                    .filter(Ticket::isVvoToTlv)
                    .collect(Collectors.toList());

            if (vvoToTlvTickets.isEmpty()) {
                System.out.println("No tickets found for Vladivostok -> Tel-Aviv");
                return;
            }

            System.out.println("Анализ билетов Владивосток -> Тель-Авив");
            System.out.println("========================================");
            System.out.println();

            System.out.println("Минимальное время полета для каждого авиаперевозчика:");
            System.out.println("-----------------------------------------------------");

            Map<String, Optional<Long>> minFlightTimesByCarrier = vvoToTlvTickets.stream()
                    .collect(Collectors.groupingBy(
                            Ticket::getCarrier,
                            Collectors.mapping(
                                    Ticket::getFlightTime,
                                    Collectors.minBy(Long::compare)
                            )
                    ));

            minFlightTimesByCarrier.forEach((carrier, timeOpt) -> {
                timeOpt.ifPresent(time -> {
                    long hours = time / 60;
                    long minutes = time % 60;
                    System.out.printf("%s: %d часов %d минут%n", carrier, hours, minutes);
                });
            });

            System.out.println();

            List<Integer> prices = vvoToTlvTickets.stream()
                    .map(Ticket::getPrice)
                    .sorted()
                    .collect(Collectors.toList());

            double averagePrice = prices.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            double medianPrice = calculateMedian(prices);
            double difference = averagePrice - medianPrice;

            System.out.println("Анализ цен:");
            System.out.println("-----------");
            System.out.printf("Количество билетов: %d%n", prices.size());
            System.out.printf("Средняя цена: %.2f руб.%n", averagePrice);
            System.out.printf("Медианная цена: %.2f руб.%n", medianPrice);
            System.out.printf("Разница между средней и медианной ценой: %.2f руб.%n", difference);

        } catch (Exception e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static double calculateMedian(List<Integer> sortedPrices) {
        int size = sortedPrices.size();
        if (size == 0) return 0.0;

        if (size % 2 == 0) {
            return (sortedPrices.get(size/2 - 1) + sortedPrices.get(size/2)) / 2.0;
        } else {
            return sortedPrices.get(size/2);
        }
    }
}