package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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
import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.model.order.PrintInfo;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.analysisServices.PriceListService;
import org.jk.application.backend.service.analysisServices.XmlParserService;
import org.jk.application.views.main.MainView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "orders", layout = MainView.class)
@PageTitle("Orders")
public class OrdersView extends VerticalLayout {

    private final Grid<Order> ordersGrid = new Grid<>(Order.class);
    private final Grid<Object[]> itemGrid = new Grid<>();
    Grid<Info> statsGrid = new Grid<>();

    List<Order> orders = new ArrayList<>();

    public OrdersView() {
        setId("orders-view");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setHeight("5%");
        upload.setWidth("98%");
        upload.setAcceptedFileTypes(".xml");
        Button uploadButton = new Button("Upload");
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(e -> refreshGrid(buffer.getInputStream()));

        Dialog ordersDialog = createDialog(true);
        Button ordersButton = new Button("Show Orders", VaadinIcon.LIST.create(), o -> ordersDialog.open());
        ordersButton.setWidth("80%");

        Dialog printsDialog = createDialog(false);
        Button printsButton = new Button("Show Prints", VaadinIcon.PRINT.create(), o -> printsDialog.open());
        printsButton.setWidth("80%");

        VerticalLayout topLayout = new VerticalLayout(ordersButton, printsButton);
        topLayout.setWidth("50%");
        topLayout.setHeight("50%");
        topLayout.getStyle().set("border-bottom", "2px solid rgba(235, 243, 255, 0.2)");
        topLayout.getStyle().set("border-right", "2px solid rgba(235, 243, 255, 0.2)");

        statsGrid.addColumn(new TextRenderer<>(Info::getInfo)).setHeader("Info");
        statsGrid.addColumn(new TextRenderer<>(Info::getData)).setHeader("Data");
        statsGrid.setWidth("50%");

        setSizeFull();
        add(upload, topLayout, statsGrid);
    }

    private VerticalLayout ordersLayout(){
        ordersGrid.setColumns("id", "date");
        ordersGrid.addColumn(new ComponentRenderer<>(OrdersView::renderItems)).setHeader("Items");
        ordersGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        ordersGrid.setHeight("90%");
        VerticalLayout layout = new VerticalLayout(ordersGrid);
        layout.setHeight("90%");
        return layout;
    }

    private VerticalLayout printsLayout(){
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

    private Dialog createDialog(boolean dialogType) {
        Dialog dialog = new Dialog();
        dialog.setHeight("80%");
        dialog.setWidth("80%");

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        if (dialogType) dialog.add(ordersLayout());
        else dialog.add(printsLayout());

        dialog.add(closeBtn);
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
            ordersGrid.setItems(orders);

            ListDataProvider<Object[]> dataProvider = DataProvider.ofCollection(xmlParserService.getCountedItems());
            dataProvider.setSortOrder(t -> (Integer) t[0], SortDirection.DESCENDING);
            itemGrid.setItems(dataProvider);

            List<Info> infos = new ArrayList<>();
            infos.add(new Info("Order price", Double.toString(xmlParserService.getOrdersPrice())));
            infos.add(new Info("Prints price", "null"));
            infos.add(new Info("Expenses price", "null"));
            statsGrid.setItems(infos);

        } catch (Exception exception) {
            Notification notification = new Notification("Error occured while uploading", 3000, Notification.Position.TOP_END);
            notification.setOpened(true);
            System.out.println(exception.toString());
        }
    }

    private Product parseProduct(Object t) {
        try {
            return (Product) t;
        } catch (NullPointerException ex) {
            System.out.println("null");
            return null;
        }
    }

    public static class Info {
        private final String info;
        private final String data;

        public Info(String info, String data) {
            this.info = info;
            this.data = data;
        }

        public String getInfo() {
            return info;
        }

        public String getData() {
            return data;
        }
    }
}
