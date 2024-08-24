package org.otpmobil.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.otpmobil.model.Customer;
import org.otpmobil.model.Payment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

public class DataLoader {
    private static final int CUSTOMER_WEBSHOP_ID_INDEX = 0;
    private static final int CUSTOMER_ID_INDEX = 1;
    private static final int CUSTOMER_NAME_INDEX = 2;
    private static final int CUSTOMER_ADDRESS_INDEX = 3;

    private static final int PAYMENT_WEBSHOP_ID_INDEX = 0;
    private static final int PAYMENT_CUSTOMER_ID_INDEX = 1;
    private static final int PAYMENT_METHOD_INDEX = 2;
    private static final int PAYMENT_AMOUNT_INDEX = 3;
    private static final int PAYMENT_BANK_ACCOUNT_INDEX = 4;
    private static final int PAYMENT_CARD_NUMBER_INDEX = 5;
    private static final int PAYMENT_DATE_INDEX = 6;

    public static final String CARD_PAYMENT = "card";
    public static final String TRANSFER_PAYMENT = "transfer";

    private static final List<String> ACCEPTED_PAYMENT_METHODS = Arrays.asList(CARD_PAYMENT, TRANSFER_PAYMENT);

    private static final String[] DATE_PATTERNS = new String[]{"yyyy.MM.dd", "yyyy.MM.d"};

    public static final String SEPARATOR = ";";

    public List<Customer> loadCustomers(final String filePath) {
        final List<Customer> customers = new ArrayList<>();
        Set<String> customerIds = new HashSet<>();

        try (final Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] values = line.split(SEPARATOR);

                if (values.length != 4) {
                    logError("Nem megfelelő ügyfél rekord: " + line);
                    continue;
                }

                final String customerId = values[CUSTOMER_ID_INDEX];

                if (customerIds.contains(customerId)) {
                    logError("Már létezik ilyen ügyfél ezzel az azonositóval: " + line);
                    continue;
                }

                customerIds.add(customerId);

                final Customer customer = new Customer(
                        values[CUSTOMER_WEBSHOP_ID_INDEX],
                        customerId,
                        values[CUSTOMER_NAME_INDEX],
                        values[CUSTOMER_ADDRESS_INDEX]
                );

                customers.add(customer);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        return customers;
    }

    public List<Payment> loadPayments(final String filePath) {
        final List<Payment> payments = new ArrayList<>();

        try (final Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] values = line.split(SEPARATOR);

                if (values.length != 7) {
                    logError("Nem megfelelő fizetési rekord: " + line);
                    continue;
                }

                final String paymentMethod = values[PAYMENT_METHOD_INDEX];

                if (!ACCEPTED_PAYMENT_METHODS.contains(paymentMethod)) {
                    logError("Nem megfelelő fizetési mód: " + line);
                    continue;
                }

                final String bankAccount = values[PAYMENT_BANK_ACCOUNT_INDEX];
                final String cardNumber = values[PAYMENT_CARD_NUMBER_INDEX];

                switch (paymentMethod) {
                    case CARD_PAYMENT -> {
                        if (StringUtils.isBlank(cardNumber)) {
                            logError("Hiányzó bankkártyaszám: " + line);
                            continue;
                        }
                    }
                    case TRANSFER_PAYMENT -> {
                        if (StringUtils.isBlank(bankAccount)) {
                            logError("Hiányzó bankszámlaszám: " + line);
                            continue;
                        }
                    }
                }

                final Date paymentDate;

                try {
                    paymentDate = DateUtils.parseDateStrictly(values[PAYMENT_DATE_INDEX], DATE_PATTERNS);
                } catch (ParseException ex) {
                    logError("Nem megfelelő dátum formátum: " + line);
                    continue;
                }

                final Payment payment = new Payment(
                        values[PAYMENT_WEBSHOP_ID_INDEX],
                        values[PAYMENT_CUSTOMER_ID_INDEX],
                        paymentMethod,
                        Double.parseDouble(values[PAYMENT_AMOUNT_INDEX]),
                        bankAccount,
                        cardNumber,
                        paymentDate
                );

                payments.add(payment);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        return payments;
    }

    private void logError(final String errorMessage) {
        try (final FileWriter fw = new FileWriter("src/main/resources/application.log", true)) {
            fw.write(Instant.now()
                    + " "
                    + errorMessage
                    + System.lineSeparator());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
