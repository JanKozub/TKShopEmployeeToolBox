package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import org.jk.application.backend.model.Expense;
import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.model.order.PrintInfo;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.analysisServices.ExpensesService;
import org.jk.application.backend.service.analysisServices.PriceListService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Layouts {
    static Dialog createDialog(boolean dialogType, Grid<Order> ordersGrid, Grid<Object[]> itemGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeight("80%");
        dialog.setWidth("80%");

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        if (dialogType) dialog.add(ordersLayout(ordersGrid));
        else dialog.add(printsLayout(itemGrid));

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

    static VerticalLayout printsLayout(Grid<Object[]> itemGrid) {
        List<PrintInfo> products = PriceListService.getProducts();
        itemGrid.addColumn(new TextRenderer<>(t -> Objects.requireNonNull(parseProduct(t[1])).getName())).setHeader("Name");
        itemGrid.addColumn(new TextRenderer<>(t -> Integer.toString((Integer) t[0]))).setHeader("Psc");
        itemGrid.addColumn(new TextRenderer<>(t -> Objects.requireNonNull(parseProduct(t[1])).getPrice() + " PLN")).setHeader("Price/psc");
        itemGrid.addColumn(new TextRenderer<>(t -> {
            AtomicReference<String> val = new AtomicReference<>("unknown");
            products.forEach(product -> {
                if (product.getProduct().getName().equals(Objects.requireNonNull(parseProduct(t[1])).getName())) {
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
        statsGrid.addColumn(new TextRenderer<>(Info::getData)).setHeader("Data");
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

    static Dialog createExpenseDialog(Grid<Expense> expenseGrid) {
        Label sumLabel = new Label(getSum());

        Dialog addExpenseDialog = new Dialog();
        DatePicker datePicker = new DatePicker("Date");
        datePicker.setRequired(true);
        datePicker.setValue(LocalDate.now());
        datePicker.getElement().setAttribute("theme", "align-center");
        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        NumberField numberField = new NumberField("Price");
        numberField.setWidthFull();
        numberField.setRequiredIndicatorVisible(true);
        Button submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        submitButton.setWidthFull();
        submitButton.addClickListener(click -> {
            ExpensesService.addExpense(new Expense(1, datePicker.getValue(), nameField.getValue(), numberField.getValue()));
            addExpenseDialog.close();
            datePicker.setValue(LocalDate.now());
            nameField.clear();
            numberField.clear();

            sumLabel.setText(getSum());

            expenseGrid.setItems(ExpensesService.getExpenses());
        });

        addExpenseDialog.add(new VerticalLayout(nameField, datePicker, numberField, submitButton));
        addExpenseDialog.addDialogCloseActionListener(e -> {
            addExpenseDialog.close();
            datePicker.clear();
            nameField.clear();
            numberField.clear();
        });

        return addExpenseDialog;
    }

    static String getSum() {
        double sum = 0;

        sum += ExpensesService.getExpenses().stream().mapToDouble(Expense::getPrice).sum();

        return sum + " PLN";
    }
}