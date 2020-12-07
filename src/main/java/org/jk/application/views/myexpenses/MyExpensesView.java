package org.jk.application.views.myexpenses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.data.model.Expense;
import org.jk.application.data.service.ExpensesService;
import org.jk.application.views.main.MainView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "my-expenses-view", layout = MainView.class)
@PageTitle("My Expenses")
public class MyExpensesView extends HorizontalLayout {

    private final Grid<Expense> expenseGrid = new Grid<>();

    public MyExpensesView() {
        setId("my-expenses-view");

        Dialog addExpenseDialog = new Dialog();
        DatePicker datePicker = new DatePicker("Date");
        datePicker.setRequired(true);
        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        NumberField numberField = new NumberField("Price");
        numberField.setWidthFull();
        numberField.setRequiredIndicatorVisible(true);
        Button submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        submitButton.setWidthFull();
        submitButton.addClickListener(click -> {
            System.out.println(new Expense(1, datePicker.getValue(), nameField.getValue(), numberField.getValue()));
            ExpensesService.addExpense(new Expense(1, datePicker.getValue(), nameField.getValue(), numberField.getValue()));
            addExpenseDialog.close();
            datePicker.clear();
            nameField.clear();
            numberField.clear();

            refreshGrid();
        });

        addExpenseDialog.add(new VerticalLayout(nameField, datePicker, numberField, submitButton));

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setHeightFull();
        leftLayout.setWidth("80%");

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
        expenseGrid.addColumn(new TextRenderer<>(Expense::getName)).setHeader("Date");
        expenseGrid.addColumn(new TextRenderer<>(e -> String.valueOf(e.getPrice()))).setHeader("Price");
        expenseGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        expenseGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        expenseGrid.setSizeFull();
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

        leftLayout.add(buttons, expenseGrid);

        VerticalLayout rightLayout = new VerticalLayout();

        Button showMyButton = new Button("Show My");
        showMyButton.setWidthFull();
        showMyButton.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);

        Button exportButton = new Button("Export");
        exportButton.setWidthFull();
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button importButton = new Button("Import");
        importButton.setWidthFull();
        importButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        rightLayout.add(showMyButton, exportButton, importButton);
        rightLayout.setHeightFull();
        rightLayout.setWidth("20%");

        setSizeFull();
        add(leftLayout, rightLayout);
    }

    private void refreshGrid() {
        expenseGrid.setItems(ExpensesService.getExpenses());
    }

}
