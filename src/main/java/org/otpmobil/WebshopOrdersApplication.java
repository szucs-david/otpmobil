package org.otpmobil;

import org.otpmobil.model.Customer;
import org.otpmobil.model.Payment;
import org.otpmobil.service.ReportService;
import org.otpmobil.util.DataLoader;

import java.util.List;
import java.util.Map;

public class WebshopOrdersApplication {
    public static void main(String[] args) {
        final DataLoader loader = new DataLoader();
        final ReportService reportService = new ReportService();

        final List<Customer> customers = loader.loadCustomers("src/main/resources/customer.csv");
        final List<Payment> payments = loader.loadPayments("src/main/resources/payments.csv");

        final Map<String, Double> customerTotals = reportService.calculateCustomerTotals(customers, payments);
        reportService.saveReport(customerTotals, "src/main/resources/report01.csv");

        final Map<String, Double> topCustomers = reportService.findTopCustomers(customerTotals);
        reportService.saveReport(topCustomers, "src/main/resources/top.csv");

        final Map<String, Map<String, Double>> webshopTotals = reportService.calculateWebshopTotals(payments);
        reportService.saveWebshopTotalReport(webshopTotals, "src/main/resources/report02.csv");
    }
}