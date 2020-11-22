package org.jk.application.views.myexpenses;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.views.main.MainView;

@Route(value = "my-expenses-view", layout = MainView.class)
@PageTitle("My Expenses")
public class MyExpensesView extends Div {

    public MyExpensesView() {
        setId("my-expenses-view");
        add(new Label("Content placeholder"));
    }

}
