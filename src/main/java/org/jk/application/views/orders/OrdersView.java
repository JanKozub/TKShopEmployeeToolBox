package org.jk.application.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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
import org.jk.application.backend.model.Expense;
import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.service.analysisServices.ExpensesService;
import org.jk.application.backend.service.analysisServices.XmlParserService;
import org.jk.application.views.main.MainView;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        Dialog ordersDialog = Layouts.createDialog(true, ordersGrid, itemGrid);
        Button ordersButton = new Button("Show Orders", VaadinIcon.LIST.create(), o -> ordersDialog.open());
        ordersButton.setWidth("80%");

        Dialog printsDialog = Layouts.createDialog(false, ordersGrid, itemGrid);
        Button printsButton = new Button("Show Prints", VaadinIcon.PRINT.create(), o -> printsDialog.open());
        printsButton.setWidth("80%");

        VerticalLayout topLayout = new VerticalLayout(ordersButton, printsButton);
        topLayout.setWidth("100%");
        topLayout.setHeight("50%");
        topLayout.getStyle().set("border-bottom", "2px solid rgba(235, 243, 255, 0.2)");
        topLayout.getStyle().set("border-right", "2px solid rgba(235, 243, 255, 0.2)");

        VerticalLayout left = new VerticalLayout(topLayout, Layouts.renderStatsGrid(statsGrid));
        left.setWidth("50%");

        Grid<Expense> expensesGrid = new Grid<>();
        expensesGrid.addColumn(new TextRenderer<>(e -> String.valueOf(e.getId()))).setHeader("ID");
        expensesGrid.addColumn(new TextRenderer<>(e -> e.getDate().format(DateTimeFormatter.ofPattern("d MMM uuu")))).setHeader("Date");
        expensesGrid.addColumn(new TextRenderer<>(Expense::getName)).setHeader("Name");
        expensesGrid.addColumn(new TextRenderer<>(e -> e.getPrice() + " PLN")).setHeader("Price").setFooter(Layouts.getSum());
        expensesGrid.addColumn(new ComponentRenderer<>(e -> {
            Button button = new Button("Delete");
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }));
        expensesGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        expensesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        expensesGrid.setItems(ExpensesService.getExpenses());
        expensesGrid.setHeight("100%");
        expensesGrid.setWidth("100%");

        Dialog addExpenseDialog = Layouts.createExpenseDialog(expensesGrid);
        Button addExpense = new Button("Add", VaadinIcon.PLUS_CIRCLE_O.create(), c -> addExpenseDialog.open());
        addExpense.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addExpense.setWidth("50%");

        ArrayList<Integer> selected = new ArrayList<>();
        expensesGrid.asMultiSelect().addValueChangeListener(e -> {
            selected.clear();
            e.getValue().forEach(expense -> selected.add(expense.getId()));
        });

        Button deleteExpense = new Button("Delete", VaadinIcon.MINUS_CIRCLE_O.create(), c -> {
            ExpensesService.removeExpense(selected);
            expensesGrid.setItems(ExpensesService.getExpenses());
        });

        deleteExpense.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteExpense.setWidth("50%");
        HorizontalLayout buttons = new HorizontalLayout(addExpense, deleteExpense);
        buttons.setWidth("100%");

        VerticalLayout right = new VerticalLayout(buttons, expensesGrid);
        right.setWidth("50%");

        HorizontalLayout mainLayout = new HorizontalLayout(left, right);
        mainLayout.setSizeFull();

        setSizeFull();
        add(upload, mainLayout);
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
