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
import org.jk.application.data.service.PriceListService;
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

        List<Product> products = PriceListService.getProducts();

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

