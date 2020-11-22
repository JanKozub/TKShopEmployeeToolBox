package org.jk.application.views.pricelist;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.views.main.MainView;

@Route(value = "price-list-view", layout = MainView.class)
@PageTitle("Price List")
public class PriceListView extends Div {

    public PriceListView() {
        setId("price-list-view");
        add(new Label("Content placeholder"));
    }

}

