package org.jk.application.views.storage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.backend.model.storage.Item;
import org.jk.application.backend.model.storage.Project;
import org.jk.application.backend.service.IdGenerator;
import org.jk.application.backend.service.dbServices.ItemService;
import org.jk.application.backend.service.dbServices.ProjectService;
import org.jk.application.views.main.MainView;

import java.util.Collection;

@Route(value = "storage", layout = MainView.class)
@PageTitle("Storage")
public class StorageView extends VerticalLayout {

    private final ProjectService projectService;
    private final ItemService itemService;

    private final HorizontalLayout header;
    private final Button inventory;
    private final Button addProject;
    private final Grid<Item> grid;
    private final Button editProject;
    private final Button deleteProject;

    private int currentProjectId = 0;

    public StorageView(ProjectService projectService, ItemService itemService) {
        this.projectService = projectService;
        this.itemService = itemService;

        setId("storage-view");

        header = new HorizontalLayout();
        inventory = new Button("Inventory", VaadinIcon.LIST.create(), e -> {
            currentProjectId = 0;
            refreshTopBar();
            refreshGrid();
        });
        inventory.getStyle().set("margin-left", "10px");

        Dialog addProjectDialog = addProjectDialog();

        addProject = new Button("Add Project", VaadinIcon.PLUS.create(), e -> addProjectDialog.open());
        addProject.getStyle().set("margin-left", "10px");
        addProject.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        header.setWidth("100%");

        Button addItem = new Button("Add Item", VaadinIcon.PLUS_CIRCLE_O.create(), i -> addItemDialog().open());
        addItem.setWidth("33%");
        addItem.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        editProject = new Button("Edit Project", VaadinIcon.EDIT.create(), dp -> editProjectDialog().open());
        editProject.setWidth("33%");

        deleteProject = new Button("Delete Project", VaadinIcon.MINUS_CIRCLE_O.create(), dp -> deleteProjectDialog().open());
        deleteProject.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteProject.setWidth("33%");

        HorizontalLayout buttons = new HorizontalLayout(addItem, editProject, deleteProject);
        buttons.setWidth("100%");

        grid = new Grid<>();

        grid.addColumn(new ComponentRenderer<>(this::nameEditField)).setHeader("Name");
        grid.addColumn(new ComponentRenderer<>(i -> editValueField(i, false))).setHeader("Quantity");
        grid.addColumn(new ComponentRenderer<>(i -> editValueField(i, true))).setHeader("Demand");
        grid.addColumn(Item::getId).setHeader("ID");
        grid.addColumn(new ComponentRenderer<>(this::deleteButton)).setHeader("Delete");
        add(header, createCenterLine(), buttons, grid);

        setSizeFull();
        refreshGrid();
        refreshTopBar();
    }

    private TextField nameEditField(Item i) {
        TextField textField = new TextField();
        textField.setValue(i.getName());
        textField.addValueChangeListener(v -> {
            itemService.updateName(i.getId(), v.getValue());
            refreshGrid();
        });
        return textField;
    }

    private NumberField editValueField(Item i, boolean type) {
        NumberField numberField = new NumberField();
        if (type) numberField.setValue((double) i.getDemand());
        else numberField.setValue((double) i.getQuantity());
        numberField.setStep(1);
        numberField.setHasControls(true);
        numberField.addValueChangeListener(e -> {
            if (type) itemService.updateDemand(i.getId(), numberField.getValue().intValue());
            else itemService.updateQuantity(i.getId(), numberField.getValue().intValue());
            refreshGrid();
        });
        return numberField;
    }

    private Button deleteButton(Item i) {
        Button button = new Button("Delete", e -> {
            itemService.deleteItem(i.getId());
            refreshGrid();
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }

    private Dialog addProjectDialog() {
        Dialog dialog = new Dialog();
        TextField projectName = new TextField("Project Name");
        Button addButton = new Button("Add Project");
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(
                clk -> {
                    projectService.addProject(new Project(IdGenerator.generateProjectId(projectService), projectName.getValue()));
                    refreshTopBar();
                    projectName.clear();
                    dialog.close();
                }
        );
        VerticalLayout verticalLayout = new VerticalLayout(projectName, addButton);
        verticalLayout.setAlignItems(Alignment.CENTER);
        dialog.add(verticalLayout);
        return dialog;
    }

    private Dialog editProjectDialog() {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        TextField newName = new TextField();
        newName.setRequired(true);
        Button button = new Button("Submit", b -> {
            if (!newName.getValue().isEmpty()) {
                projectService.updateProject(new Project(currentProjectId, newName.getValue()));
                refreshTopBar();
                dialog.close();
            } else {
                newName.setErrorMessage("Fill up name");
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.setWidth("100%");
        dialogLayout.add(new Label("Change name"), newName, button);
        dialogLayout.setAlignItems(Alignment.CENTER);
        dialog.add(dialogLayout);
        return dialog;
    }

    private Dialog addItemDialog() {
        Dialog itemDialog = new Dialog();
        TextField itemName = new TextField("Item Name");
        itemName.setWidth("100%");
        NumberField itemQuantity = new NumberField("Current Quantity");
        itemQuantity.setWidth("100%");
        NumberField itemDemand = new NumberField("Demand");
        itemDemand.setWidth("100%");
        Button confirmBtn = new Button("Add", c -> {
            itemService.addItem(new Item(
                    IdGenerator.generateItemId(itemService), currentProjectId,
                    itemName.getValue(), itemQuantity.getValue().intValue(),
                    itemDemand.getValue().intValue()));
            itemName.clear();
            itemQuantity.clear();
            itemDemand.clear();
            itemDialog.close();
            refreshGrid();
        });
        confirmBtn.setWidth("100%");
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        VerticalLayout idvl = new VerticalLayout(new Label("New Item"), itemName, itemQuantity, itemDemand, confirmBtn);
        idvl.setAlignItems(Alignment.CENTER);
        itemDialog.add(idvl);
        return itemDialog;
    }

    private Dialog deleteProjectDialog() {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Label("Are you sure?"));

        dialogLayout.add(new HorizontalLayout(new Button("Yes", y -> {
            projectService.deleteProject(currentProjectId);
            currentProjectId = 0;
            refreshTopBar();
            dialog.close();
        }), new Button("Not really", n -> dialog.close())));
        dialogLayout.setAlignItems(Alignment.CENTER);
        dialog.add(dialogLayout);
        return dialog;
    }

    private Div createCenterLine() {
        Div line = new Div();
        line.getStyle().set("background-color", "rgba(173, 200, 235, 0.14)");
        line.setWidth("100%");
        line.setHeight("3px");
        return line;
    }

    private void refreshTopBar() {
        header.removeAll();
        header.add(inventory);
        Collection<Project> projects = projectService.getProjects();
        for (Project p : projects) {
            Button button = new Button(p.getName(), VaadinIcon.HARDDRIVE_O.create(), e -> {
                currentProjectId = p.getId();
                refreshGrid();
                refreshTopBar();
            });

            if (currentProjectId == p.getId()) {
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                inventory.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
            } else {
                if (currentProjectId == 0) {
                    inventory.removeThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
                    inventory.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                } else
                    inventory.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                inventory.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
                button.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
            }

            header.add(button);
        }
        header.add(addProject);

        editProject.setEnabled(currentProjectId != 0);
        deleteProject.setEnabled(currentProjectId != 0);
    }

    private void refreshGrid() {
        grid.setItems(itemService.getItemsWithProjectId(currentProjectId));
    }
}
