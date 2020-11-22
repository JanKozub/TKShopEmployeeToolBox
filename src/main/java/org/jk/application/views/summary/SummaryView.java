package org.jk.application.views.summary;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.views.main.MainView;

@Route(value = "summary-view", layout = MainView.class)
@PageTitle("Summary")
public class SummaryView extends Div {

    public SummaryView() {
        setId("summary-view");
        add(new Label("Content placeholder"));
    }

}
