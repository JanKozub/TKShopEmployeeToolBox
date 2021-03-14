package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.FileUtils;
import org.jk.application.backend.model.Entry;
import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.model.order.PrintInfo;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.analysisServices.PriceListService;
import org.jk.application.backend.service.analysisServices.XmlParserService;
import org.jk.application.views.main.MainView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "orders", layout = MainView.class)
@PageTitle("Orders")
public class OrdersView extends VerticalLayout {

    private final Grid<Order> grid = new Grid<>(Order.class);
    private final Grid<Object[]> itemGrid = new Grid<>();
    private final Upload upload;

    List<Order> orders = new ArrayList<>();

    public OrdersView() {
        setId("orders-view");

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setHeight("5%");
        upload.setWidth("98%");
        upload.setAcceptedFileTypes(".xml");
        Button uploadButton = new Button("Upload");
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(e ->
        {
            refreshGrid(buffer.getInputStream());
            upload.setDropAllowed(true);
        });

        Dialog ordersDialog = createGridDialog();
        Button ordersButton = new Button("Show Orders", VaadinIcon.LIST.create(), o -> ordersDialog.open());
        ordersButton.setWidth("80%");

        Dialog printsDialog = createPrintsDialog();
        Button printsButton = new Button("Show Prints", VaadinIcon.PRINT.create(), o -> printsDialog.open());
        printsButton.setWidth("80%");

        VerticalLayout topLayout = new VerticalLayout(ordersButton, printsButton);
        topLayout.setWidth("50%");
        topLayout.setHeight("50%");
        topLayout.getStyle().set("border-bottom", "2px solid rgba(235, 243, 255, 0.2)");
        topLayout.getStyle().set("border-right", "2px solid rgba(235, 243, 255, 0.2)");

        Label totalOrdersPrice = new Label("Total Orders Price: 99999PLN");
        Label totalPrintsPrice = new Label("Total Prints Price: 1340PLN");
        Label totalExpensesPrice = new Label("Total expenses Price: 930PLN");

        VerticalLayout bottomLayout = new VerticalLayout(totalOrdersPrice, totalPrintsPrice, totalExpensesPrice);
        bottomLayout.setWidth("50%");
        bottomLayout.setHeight("50%");

        setSizeFull();
        add(upload, topLayout, bottomLayout);
    }

    private Dialog createGridDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeight("80%");
        dialog.setWidth("80%");

        grid.setColumns("id", "date");
        grid.addColumn(new ComponentRenderer<>(OrdersView::renderItems)).setHeader("Items");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setHeight("90%");

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        dialog.add(grid, closeBtn);
        return dialog;
    }

    private Dialog createPrintsDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("80%");
        dialog.setHeight("80%");

        List<PrintInfo> products = PriceListService.getProducts();

//        int sum = 0;
//        for (Entry entry : entries) {
//            sum = sum + entry.getNum();
//        }

        itemGrid.addColumn(new TextRenderer<>(t -> Objects.requireNonNull(parseProduct(t[1])).getName())).setHeader("Name").setFooter("Ilość przedmiotów razem: ");
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

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        dialog.add(itemGrid, closeBtn);
        return dialog;
    }

    private static VerticalLayout renderItems(Order order) {
        List<Product> items = order.getProducts();
        VerticalLayout layout = new VerticalLayout();
        for (int i = 0; i < items.size(); i++) {
            Label name = new Label("Name: " + items.get(0).getName());
            Label price = new Label("Price: " + items.get(0).getPrice() + " PLN");
            Label quantity = new Label("Quantity: " + items.get(0).getQuantity());

            Div line = new Div();
            line.setWidthFull();
            line.setHeight("2px");
            line.getStyle().set("background-color", "rgba(235, 243, 255, 0.9)");

            layout.add(name, price, quantity);

            if (i + 1 != items.size())
                layout.add(line);
        }
        return layout;
    }

    private void refreshGrid(InputStream inputStream) {
        try {
            XmlParserService xmlParserService = new XmlParserService(inputStream);
            orders = xmlParserService.getOrders();
            grid.setItems(orders);

            refreshItemGrid(xmlParserService.getCountedItems());
            upload.setDropLabel(new Span("File has been loaded"));
        } catch (Exception exception) {
            Notification notification = new Notification("Error occured while uploading", 3000, Notification.Position.TOP_END);
            notification.setOpened(true);
            System.out.println(exception.toString());
        }
    }

    private void refreshItemGrid(List<Object[]> entries) {
        ListDataProvider<Object[]> dataProvider = DataProvider.ofCollection(entries);
        dataProvider.setSortOrder(t -> (Integer) t[0], SortDirection.DESCENDING);
        itemGrid.setItems(dataProvider);
    }

    private Product parseProduct(Object t) {
        try {
            return (Product) t;
        } catch (NullPointerException ex) {
            System.out.println("null");
            return null;
        }
    }
}
