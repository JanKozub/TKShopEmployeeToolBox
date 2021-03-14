package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.dbServices.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Layouts {

    static Dialog createDialog(ProductService productService, boolean dialogType, Grid<Order> ordersGrid, Grid<Object[]> printsGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeight("80%");
        dialog.setWidth("80%");

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        if (dialogType) dialog.add(ordersLayout(ordersGrid));
        else dialog.add(printsLayout(productService, printsGrid));

        dialog.add(closeBtn);
        return dialog;
    }

    static VerticalLayout ordersLayout(Grid<Order> ordersGrid) {
        ordersGrid.setColumns("id", "date");
        ordersGrid.addColumn(new ComponentRenderer<>(Layouts::renderItems)).setHeader("Items");
        ordersGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        ordersGrid.setHeight("90%");
        VerticalLayout layout = new VerticalLayout(ordersGrid);
        layout.setHeight("90%");
        return layout;
    }

    static VerticalLayout printsLayout(ProductService productService, Grid<Object[]> itemGrid) {
        List<Product> products = new ArrayList<>(productService.getProducts());

        itemGrid.addColumn(new TextRenderer<>(t -> Objects.requireNonNull(parseProduct(t[1])).getName())).setHeader("Name");
        itemGrid.addColumn(new TextRenderer<>(t -> Integer.toString((Integer) t[0]))).setHeader("Psc");
        itemGrid.addColumn(new TextRenderer<>(t -> Objects.requireNonNull(parseProduct(t[1])).getPrice() + " PLN")).setHeader("Price/psc");
        itemGrid.addColumn(new TextRenderer<>(t -> {
            AtomicReference<String> val = new AtomicReference<>("unknown");
            products.forEach(product -> {
                if (product.getName().equals(Objects.requireNonNull(parseProduct(t[1])).getName())) {
                    val.set(String.valueOf((Integer) t[0] * product.getPrintPrice()));
                }
            });
            return String.valueOf(val);
        })).setHeader("Print price");
        itemGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        itemGrid.setHeight("90%");

        VerticalLayout layout = new VerticalLayout(itemGrid);
        layout.setHeight("90%");
        return layout;
    }

    static VerticalLayout renderItems(Order order) {
        List<Product> items = order.getProducts();
        VerticalLayout layout = new VerticalLayout();
        for (int i = 0; i < items.size(); i++) {
            Label name = new Label("Name: " + items.get(0).getName());
            Label price = new Label("Price: " + items.get(0).getPrice() + " PLN");
            Label quantity = new Label("Quantity: " + items.get(0).getQuantity());
            layout.add(name, price, quantity);
        }
        return layout;
    }

    static Grid<Info> renderStatsGrid(Grid<Info> statsGrid) {
        statsGrid.addColumn(new TextRenderer<>(Info::getInfo)).setHeader("Info");
        statsGrid.addColumn(new TextRenderer<>(i -> i.getData() + " PLN")).setHeader("Data");
        statsGrid.setWidth("100%");

        return statsGrid;
    }

    private static Product parseProduct(Object t) {
        try {
            return (Product) t;
        } catch (NullPointerException ex) {
            System.out.println("null");
            return null;
        }
    }

    static VerticalLayout productsLayout(ProductService productService, Grid<Product> productGrid) {
        productGrid.addColumn(new TextRenderer<>(p -> String.valueOf(p.getId()))).setHeader("ID");
        productGrid.addColumn(new TextRenderer<>(Product::getName)).setHeader("Name");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrice() + " PLN")).setHeader("Price");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrintPrice() + " PLN")).setHeader("Print Price");
        productGrid.addColumn(new TextRenderer<>(p -> p.getPrintTime() + " h/psc")).setHeader("Print Time");
        productGrid.addColumn(new ComponentRenderer<>(c -> {
            Button button = new Button("Edit", VaadinIcon.EDIT.create(), e -> {

            });
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            button.getStyle().set("background-color", "#1676f3");
            return button;
        }));
        productGrid.addColumn(new ComponentRenderer<>(c -> {
            Button button = new Button("Delete", VaadinIcon.MINUS_CIRCLE_O.create(),
                    e -> {
                        productService.deleteProduct(c.getId());
                        productGrid.setItems(productService.getProducts());
                    });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            button.getStyle().set("opacity", "85%");
            return button;
        }));

        productGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        productGrid.setItems(productService.getProducts());
        productGrid.setSizeFull();

        return new VerticalLayout(productGrid);
    }
}
