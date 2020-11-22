package org.jk.application.views.invoicegenerator;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.views.main.MainView;

@Route(value = "invoice-generator", layout = MainView.class)
@PageTitle("Invoice Generator")
@CssImport("./styles/views/invoicegenerator/invoice-generator-view.css")
public class InvoiceGeneratorView extends Div {

    public InvoiceGeneratorView() {
        setId("invoice-generator-view");
        add(new Label("Content placeholder"));
    }

}
