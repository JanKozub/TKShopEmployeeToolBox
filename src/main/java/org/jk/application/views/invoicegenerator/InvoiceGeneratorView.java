package org.jk.application.views.invoicegenerator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.data.model.order.Order;
import org.jk.application.data.generator.InvoiceListGenerator;
import org.jk.application.data.service.XmlParserService;
import org.jk.application.views.main.MainView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "invoice-generator", layout = MainView.class)
@PageTitle("Invoice Generator")
@CssImport("./styles/views/invoicegenerator/invoice-generator-view.css")
public class InvoiceGeneratorView extends Div {

    public InvoiceGeneratorView() {
        setId("invoice-generator-view");

        VerticalLayout left = new VerticalLayout();
        VerticalLayout right  = new VerticalLayout();

        Button generateButton = new Button("Generate");
        generateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        generateButton.setWidthFull();
        generateButton.addClickListener(buttonClickEvent -> generateLeft(left));
        generateLeft(left);

        Button clearButton = new Button("Clear");
        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.setWidthFull();
        clearButton.addClickListener(click -> left.removeAll());

        right.add(generateButton, clearButton);
        right.setAlignItems(FlexComponent.Alignment.CENTER);

        SplitLayout layout = new SplitLayout(left, right);
        layout.setHeightFull();
        layout.setPrimaryStyle("minWidth", "85%");
        layout.setPrimaryStyle("maxWidth", "85%");
        layout.setWidthFull();
        layout.setHeightFull();

        setHeight("90%");
        add(layout);
    }

    private void generateLeft(VerticalLayout left) {
        List<Order> orders = new ArrayList<>();
        try {
            XmlParserService xmlParserService = new XmlParserService("src/main/resources/files/temp.xml");
            orders = xmlParserService.getOrders();
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

        left.removeAll();
        for (Order order : orders) {
            left.add(InvoiceListGenerator.getInvoice(order));
        }
    }

}
