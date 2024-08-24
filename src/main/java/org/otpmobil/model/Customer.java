package org.otpmobil.model;

public class Customer {
    private String webshopId;
    private String customerId;
    private String customerName;
    private String customerAddress;

    public Customer() {
    }

    public Customer(String webshopId, String customerId, String customerName, String customerAddress) {
        this.webshopId = webshopId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
    }

    public String getWebshopId() {
        return webshopId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }
}
