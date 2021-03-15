package org.jk.application.views.rootcomponent;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.jk.application.views.storage.StorageView;

@Route("")
public class RootComponent extends Div implements BeforeEnterObserver {
    public RootComponent(){
        setText("Default path");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.rerouteTo(StorageView.class);
    }
}
