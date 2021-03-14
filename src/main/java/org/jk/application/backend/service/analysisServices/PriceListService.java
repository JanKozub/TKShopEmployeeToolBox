package org.jk.application.backend.service.analysisServices;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.jk.application.backend.model.Entry;
import org.jk.application.backend.model.order.PrintInfo;
import org.jk.application.backend.model.order.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PriceListService {

    public static List<PrintInfo> getProducts() {
        List<PrintInfo> printInfos = new ArrayList<>();
        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new File("src/main/resources/files/products.json"));

            String num = "";
            String name = "";
            double price = 0;
            double printPrice = 0;
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    parser.nextToken();

                    if (parser.getCurrentName().equals("num"))
                        num = parser.getValueAsString();

                    if (parser.getCurrentName().equals("name"))
                        name = parser.getValueAsString();

                    if (parser.getCurrentName().equals("price"))
                        price = parser.getValueAsDouble();

                    if (parser.getCurrentName().equals("printPrice"))
                        printPrice = parser.getValueAsDouble();

                    if (parser.getCurrentName().equals("printTime")) {
                        printInfos.add(new PrintInfo(new Product(num, name, 0, price), printPrice, parser.getValueAsDouble()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return printInfos;
    }
}
