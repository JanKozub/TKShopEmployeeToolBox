package org.jk.application.backend.service.analysisServices;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.jk.application.backend.model.Entry;
import org.jk.application.backend.model.product.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PriceListService {

    public static List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new File("src/main/resources/files/products.json"));

            int num = 0;
            String name = "";
            double price = 0;
            double printPrice = 0;
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    parser.nextToken();

                    if (parser.getCurrentName().equals("num"))
                        num = parser.getValueAsInt();

                    if (parser.getCurrentName().equals("name"))
                        name = parser.getValueAsString();

                    if (parser.getCurrentName().equals("price"))
                        price = parser.getValueAsDouble();

                    if (parser.getCurrentName().equals("printPrice"))
                        printPrice = parser.getValueAsDouble();

                    if (parser.getCurrentName().equals("printTime")) {
                        products.add(new Product(num, name, price, printPrice, parser.getValueAsDouble()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return products;
    }

    public static double sumPrintPrice(List<Product> products, List<Entry> entries) {
        double sum = 0;
        for (Entry e : entries) {
            AtomicReference<Double> price = new AtomicReference<>((double) 0);

            products.forEach(product -> {
                if (product.getName().equals(e.getName())) {
                    price.set(e.getNum() * product.getPrintPrice());
                }
            });
            sum += price.get();
        }
        return sum;
    }

    public static double sumPrice(List<Product> products, List<Entry> entries) {
        double sum = 0;
        for (Entry e : entries) {
            AtomicReference<Double> price = new AtomicReference<>((double) 0);

            products.forEach(product -> {
                if (product.getName().equals(e.getName())) {
                    price.set(e.getNum() * product.getPrice());
                }
            });
            sum += price.get();
        }
        return sum;
    }
}
