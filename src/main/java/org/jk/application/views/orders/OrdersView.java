package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
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
    private final Grid<Info> statsGrid = new Grid<>();

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
        topLayout.setWidth("100%");
        topLayout.setHeight("50%");
        topLayout.getStyle().set("border-bottom", "2px solid rgba(235, 243, 255, 0.2)");
        topLayout.getStyle().set("border-right", "2px solid rgba(235, 243, 255, 0.2)");

        VerticalLayout left = new VerticalLayout(topLayout, Layouts.renderStatsGrid(statsGrid));
        left.setHeight("100%");
        left.setWidth("50%");

        Grid<String> expensesGrid = new Grid<>();
        expensesGrid.setWidth("50%");
        expensesGrid.setHeight("100%");
        VerticalLayout right = new VerticalLayout(expensesGrid);

        HorizontalLayout mainLayout = new HorizontalLayout(left, expensesGrid);
        mainLayout.setSizeFull();

        setSizeFull();
        add(upload, mainLayout);
    }

    private Dialog createDialog(boolean dialogType) {
        Dialog dialog = new Dialog();
        dialog.setHeight("80%");
        dialog.setWidth("80%");

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE_CIRCLE.create(), c -> dialog.close());
        closeBtn.setWidth("100%");

        if (dialogType) dialog.add(Layouts.ordersLayout(ordersGrid));
        else dialog.add(Layouts.printsLayout(itemGrid));

        dialog.add(closeBtn);
        return dialog;
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
}
