package org.jk.application.views.orders;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.FileUtils;
import org.jk.application.data.entity.Order;
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
    private List<Order> orders;
    private final String filePath = "src/main/resources/files/temp.xml";

    public OrdersView() {
        setId("orders-view");

        grid = new Grid<>(Order.class);
        grid.setColumns("id", "date", "buyer", "items", "payment", "delivery");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeight("90%");

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setHeight("5%");
        upload.setAcceptedFileTypes(".xml");
        upload.addSucceededListener(e ->
        {
            try {
                File file = new File(filePath);
                FileUtils.copyInputStreamToFile(buffer.getInputStream(), file);
                refreshGrid();
            }catch (IOException ex) {
                upload.setDropLabel(new Span("Error occurred while loading file"));
            }
            upload.setDropAllowed(true);
        });

        refreshGrid();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(upload);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        try {
            XmlParserService xmlParserService = new XmlParserService(filePath);
            orders = xmlParserService.getOrders();
            grid.setItems(orders);
            upload.setDropLabel(new Span("File has been loaded"));
        } catch (Exception exception) {
            upload.setDropLabel(new Span("Please upload .xml file!"));
        }
    }
}
