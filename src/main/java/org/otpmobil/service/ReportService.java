package org.otpmobil.service;

import org.otpmobil.model.Customer;
import org.otpmobil.model.Payment;
import org.otpmobil.util.DataLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    public Map<String, Double> calculateCustomerTotals(final List<Customer> customers, final List<Payment> payments) {
        final Map<String, Double> customerTotals = new HashMap<>();

        for (final Customer customer : customers) {
            final double total = payments.stream()
                    .filter(payment -> payment.getCustomerId().equals(customer.getCustomerId())
                            && payment.getWebshopId().equals(customer.getWebshopId()))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            customerTotals.put(customer.getCustomerName()
                    + DataLoader.SEPARATOR
                    + customer.getCustomerAddress(), total);
        }

        return customerTotals;
    }

    public Map<String, Double> findTopCustomers(final Map<String, Double> customerTotals) {
        return customerTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(2)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Map<String, Double>> calculateWebshopTotals(final List<Payment> payments) {
        final Map<String, Map<String, Double>> webshopTotals = new HashMap<>();

        for (final Payment payment : payments) {
            final String webshopId = payment.getWebshopId();
            final String paymentType = payment.getPaymentMethod();
            final double amount = payment.getAmount();

            final Map<String, Double> paymentTypeMap = webshopTotals.computeIfAbsent(webshopId, k -> new HashMap<>());

            paymentTypeMap.merge(paymentType, amount, Double::sum);
        }

        return webshopTotals;
    }

    public void saveReport(final Map<String, Double> reportData, final String filePath) {
        try (final FileWriter writer = new FileWriter(filePath)) {
            for (final Map.Entry<String, Double> entry : reportData.entrySet()) {
                writer.write(entry.getKey()
                        + DataLoader.SEPARATOR
                        + entry.getValue()
                        + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void saveWebshopTotalReport(final Map<String, Map<String, Double>> webshopData, final String filePath) {
        try (final FileWriter writer = new FileWriter(filePath)) {
            for (final Map.Entry<String, Map<String, Double>> entry : webshopData.entrySet()) {
                final double cardTotal = entry.getValue()
                        .getOrDefault(DataLoader.CARD_PAYMENT, 0.0);
                final double transferTotal = entry.getValue()
                        .getOrDefault(DataLoader.TRANSFER_PAYMENT, 0.0);

                writer.write(entry.getKey()
                        + DataLoader.SEPARATOR
                        + cardTotal
                        + DataLoader.SEPARATOR
                        + transferTotal
                        + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
