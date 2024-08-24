package org.otpmobil.model;

import java.util.Date;

public class Payment {
    private String webshopId;
    private String customerId;
    private String paymentMethod;
    private Double amount;
    private String bankAccount;
    private String cardNumber;
    private Date paymentDate;

    public Payment() {
    }

    public Payment(String webshopId, String customerId, String paymentMethod, Double amount, String bankAccount,
                   String cardNumber, Date paymentDate) {
        this.webshopId = webshopId;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
    }

    public String getWebshopId() {
        return webshopId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }
}
