package org.jk.application.views.pricelist;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.data.model.product.Product;
import org.jk.application.views.main.MainView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "price-list-view", layout = MainView.class)
@PageTitle("Price List")
public class PriceListView extends VerticalLayout {

    public PriceListView() {
        setId("price-list-view");

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
                        products.add(new Product(num, name, price, printPrice ,parser.getValueAsDouble()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        Grid<Product> productGrid = new Grid<>();

        productGrid.addColumn(new TextRenderer<>(p -> p.getNum()+ "")).setHeader("Num");
        productGrid.addColumn(new TextRenderer<>(Product::getName)).setHeader("Name");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrice() + " PLN")).setHeader("Price");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrintPrice() + " PLN")).setHeader("Print Price");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrintTime() + " h/psc")).setHeader("Print Time");

        productGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        productGrid.setItems(products);
        productGrid.setSizeFull();

        setSizeFull();
        add(productGrid);
    }

}

