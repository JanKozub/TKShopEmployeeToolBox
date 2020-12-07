package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.FileUtils;
import org.jk.application.data.model.order.Item;
import org.jk.application.data.model.order.Order;
import org.jk.application.data.service.XmlParserService;
import org.jk.application.views.main.MainView;

import java.io.*;
import java.util.List;

@Route(value = "orders", layout = MainView.class)
@PageTitle("Orders")
@CssImport("./styles/views/orders/orders-view.css")
public class OrdersView extends Div {

    private Grid<Order> grid;
    private Upload upload;
    private final String filePath = "src/main/resources/files/temp.xml";

    public OrdersView() {
        setId("orders-view");

        grid = new Grid<>(Order.class);
        grid.setColumns("number", "date");

        grid.addColumn(new ComponentRenderer<>(OrdersView::renderBuyer)).setHeader("Buyer");
        grid.addColumn(new ComponentRenderer<>(OrdersView::renderItems)).setHeader("Items");
        grid.addColumn(new ComponentRenderer<>(OrdersView::renderPayment)).setHeader("Payment");
        grid.addColumn(new ComponentRenderer<>(OrdersView::renderDelivery)).setHeader("Delivery");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeight("90%");

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setHeight("5%");
        upload.setWidth("98%");
        upload.setAcceptedFileTypes(".xml");
        Button uploadButton = new Button("Upload");
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(e ->
        {
            try {
                File file = new File(filePath);
                FileUtils.copyInputStreamToFile(buffer.getInputStream(), file);
                refreshGrid();
            } catch (IOException ex) {
                upload.setDropLabel(new Span("Error occurred while loading file"));
            }
            upload.setDropAllowed(true);
        });

        refreshGrid();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(upload);
        mainLayout.add(grid);

        add(mainLayout);
    }

    private static VerticalLayout renderItems(Order order) {
        List<Item> items = order.getItems();
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

    private static VerticalLayout renderPayment(Order order) {
        return new VerticalLayout(
                new Label("Date: " + order.getPayment().getDate()),
                new Label("Provider: " + order.getPayment().getProvider()),
                new Label("amount: " + order.getPayment().getAmount() + " PLN")
        );
    }

    private static VerticalLayout renderBuyer(Order order) {
        return new VerticalLayout(
                new Label("Name: " + order.getBuyer().getName()),
                new Label("Email: " + order.getBuyer().getEmail()),
                new Label("Phone: " + order.getBuyer().getPhone())
        );
    }

    private static VerticalLayout renderDelivery(Order order) {
        return new VerticalLayout(
                new Label("Method: " + order.getDelivery().getMethodName()),
                new Label("Cost: " + order.getDelivery().getCost() + " PLN"),
                new Label("Street: " + order.getDelivery().getStreet()),
                new Label("City: " + order.getDelivery().getCity() + " " + order.getDelivery().getZipcode()),
                new Label("Phone: " + order.getDelivery().getPhone())
        );
    }


    private void refreshGrid() {
        try {
            XmlParserService xmlParserService = new XmlParserService(filePath);
            List<Order> orders = xmlParserService.getOrders();
            grid.setItems(orders);
            upload.setDropLabel(new Span("File has been loaded"));
        } catch (Exception exception) {
            upload.setDropLabel(new Span("Please upload .xml file!"));
        }
    }
}
