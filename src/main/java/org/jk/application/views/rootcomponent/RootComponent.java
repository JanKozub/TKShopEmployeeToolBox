package org.jk.application.views.rootcomponent;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.jk.application.views.orders.OrdersView;

@Route("")
public class RootComponent extends Div implements BeforeEnterObserver {
    public RootComponent(){
        setText("Default path");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.rerouteTo(OrdersView.class);
    }
}
