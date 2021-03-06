package org.jk.application.views.myexpenses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.jk.application.data.model.Expense;
import org.jk.application.data.service.ExpensesService;
import org.jk.application.views.main.MainView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Route(value = "my-expenses-view", layout = MainView.class)
@PageTitle("My Expenses")
public class MyExpensesView extends VerticalLayout {

    private final Grid<Expense> expenseGrid = new Grid<>();

    public MyExpensesView() {
        setId("my-expenses-view");

        Label sumLabel = new Label(getSum());

        Dialog addExpenseDialog = new Dialog();
        DatePicker datePicker = new DatePicker("Date");
        datePicker.setRequired(true);
        datePicker.setValue(LocalDate.now());
        datePicker.getElement().setAttribute("theme", "align-center");
        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        NumberField numberField = new NumberField("Price");
        numberField.setWidthFull();
        numberField.setRequiredIndicatorVisible(true);
        Button submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        submitButton.setWidthFull();
        submitButton.addClickListener(click -> {
            ExpensesService.addExpense(new Expense(1, datePicker.getValue(), nameField.getValue(), numberField.getValue()));
            addExpenseDialog.close();
            datePicker.setValue(LocalDate.now());
            nameField.clear();
            numberField.clear();

            sumLabel.setText(getSum());

            refreshGrid();
        });

        addExpenseDialog.add(new VerticalLayout(nameField, datePicker, numberField, submitButton));
        addExpenseDialog.addDialogCloseActionListener(e -> {
            addExpenseDialog.close();
            datePicker.clear();
            nameField.clear();
            numberField.clear();
        });

        Button addButton = new Button("Add");
        addButton.setWidth("50%");
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(click -> addExpenseDialog.open());

        Button removeButton = new Button("Remove");
        removeButton.setWidth("50%");
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(addButton, removeButton);
        buttons.setWidthFull();

        expenseGrid.addColumn(new TextRenderer<>(e -> String.valueOf(e.getId()))).setHeader("ID");
        expenseGrid.addColumn(new TextRenderer<>(e -> e.getDate().format(DateTimeFormatter.ofPattern("d MMM uuu")))).setHeader("Date");
        expenseGrid.addColumn(new TextRenderer<>(Expense::getName)).setHeader("Name");
        expenseGrid.addColumn(new TextRenderer<>(e -> e.getPrice() + " PLN")).setHeader("Price").setFooter(sumLabel);
        expenseGrid.addColumn(new ComponentRenderer<>(e -> {
            Button button = new Button("Delete");
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }));
        expenseGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        expenseGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        expenseGrid.setHeightByRows(true);
        refreshGrid();

        ArrayList<Integer> selected = new ArrayList<>();
        expenseGrid.asMultiSelect().addValueChangeListener(e -> {
            selected.clear();
            e.getValue().forEach(expense -> selected.add(expense.getId()));
        });

        removeButton.addClickListener(click -> {
            ExpensesService.removeExpense(selected);
            refreshGrid();
        });

        setSizeFull();
        add(buttons, expenseGrid);
    }

    private String getSum() {
        double sum = 0;

        sum += ExpensesService.getExpenses().stream().mapToDouble(Expense::getPrice).sum();

        return sum + " PLN";
    }

    private void refreshGrid() {
        expenseGrid.setItems(ExpensesService.getExpenses());
    }

}
