package org.jk.application.views.storage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.jk.application.backend.model.storage.Item;
import org.jk.application.backend.model.storage.Project;
import org.jk.application.backend.service.IdGenerator;
import org.jk.application.backend.service.dbServices.ItemService;
import org.jk.application.backend.service.dbServices.ProjectService;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

class StorageLayouts {

    private final ItemService itemService;
    private final ProjectService projectService;

    private final HorizontalLayout header;
    private final Button inventory;
    private final Button addProject;
    private final Grid<Item> grid;
    private final Button editProjectButton;
    private final Button deleteProjectButton;

    StorageLayouts(ItemService itemService, ProjectService projectService,
                   HorizontalLayout header, Button inventory, Button addProject,
                   Grid<Item> grid, Button editProjectButton, Button deleteProjectButton) {
        this.itemService = itemService;
        this.projectService = projectService;

        this.header = header;
        this.inventory = inventory;
        this.addProject = addProject;

        this.grid = grid;
        this.editProjectButton = editProjectButton;
        this.deleteProjectButton = deleteProjectButton;
    }

    void refreshGrid(AtomicInteger currentProjectId) {
        grid.setItems(itemService.getItemsWithProjectId(currentProjectId.get()));
    }

    void refreshTopBar(AtomicInteger currentProjectId) {
        header.removeAll();
        header.add(inventory);

        Collection<Project> projects = projectService.getProjects();
        for (Project p : projects) {
            Button button = new Button(p.getName(), VaadinIcon.HARDDRIVE_O.create(), e -> {
                currentProjectId.set(p.getId());
                refreshGrid(currentProjectId);
                refreshTopBar(currentProjectId);
            });

            if (currentProjectId.get() == p.getId()) {
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                inventory.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
            } else {
                if (currentProjectId.get() == 0) {
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

        editProjectButton.setEnabled(currentProjectId.get() != 0);
        deleteProjectButton.setEnabled(currentProjectId.get() != 0);
    }

    Div createCenterLine() {
        Div line = new Div();
        line.getStyle().set("background-color", "rgba(173, 200, 235, 0.14)");
        line.setWidth("100%");
        line.setHeight("3px");
        return line;
    }

    Dialog deleteProjectDialog(AtomicInteger currentProjectId) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Label("Are you sure?"));

        dialogLayout.add(new HorizontalLayout(new Button("Yes", y -> {
            projectService.deleteProject(currentProjectId.get());
            currentProjectId.set(0);
            refreshTopBar(currentProjectId);
            dialog.close();
        }), new Button("Not really", n -> dialog.close())));
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        return dialog;
    }

    Dialog addItemDialog(AtomicInteger currentProjectId) {
        Dialog itemDialog = new Dialog();
        TextField itemName = new TextField("Item Name");
        itemName.setWidth("100%");
        itemName.setRequired(true);
        NumberField itemQuantity = new NumberField("Current Quantity");
        itemQuantity.setWidth("100%");
        NumberField itemDemand = new NumberField("Demand");
        itemDemand.setWidth("100%");
        Button confirmBtn = new Button("Add", c -> {
            if (!itemName.isEmpty()) {
                itemService.addItem(new Item(
                        IdGenerator.generateItemId(itemService), currentProjectId.get(),
                        itemName.getValue(), !itemQuantity.isEmpty() ? itemQuantity.getValue().intValue() : 0,
                        !itemDemand.isEmpty() ? itemDemand.getValue().intValue() : 0));
                itemName.clear();
                itemQuantity.clear();
                itemDemand.clear();
                itemDialog.close();
                refreshGrid(currentProjectId);
            } else {
                itemName.setErrorMessage("Fill Up Name Field!");
                itemName.setInvalid(true);
            }
        });
        confirmBtn.setWidth("100%");
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        VerticalLayout idvl = new VerticalLayout(new Label("New Item"), itemName, itemQuantity, itemDemand, confirmBtn);
        idvl.setAlignItems(FlexComponent.Alignment.CENTER);
        itemDialog.add(idvl);
        return itemDialog;
    }

    Dialog editProjectDialog(AtomicInteger currentProjectId) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        TextField newName = new TextField();
        newName.setRequired(true);
        Button button = new Button("Submit", b -> {
            if (!newName.getValue().isEmpty()) {
                projectService.updateProject(new Project(currentProjectId.get(), newName.getValue()));
                refreshTopBar(currentProjectId);
                dialog.close();
            } else {
                newName.setErrorMessage("Fill Up Name Field!");
                newName.setInvalid(true);
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.setWidth("100%");
        dialogLayout.add(new Label("Change name"), newName, button);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        return dialog;
    }

    Dialog addProjectDialog(AtomicInteger currentProjectId) {
        Dialog dialog = new Dialog();
        TextField projectName = new TextField("Project Name");
        projectName.setRequired(true);
        Button addButton = new Button("Add Project");
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(
                clk -> {
                    if (!projectName.isEmpty()) {
                        projectService.addProject(new Project(IdGenerator.generateProjectId(projectService), projectName.getValue()));
                        refreshTopBar(currentProjectId);
                        projectName.clear();
                        dialog.close();
                    }
                    else {
                        projectName.setErrorMessage("Fill Up Name Field!");
                        projectName.setInvalid(true);
                    }
                }
        );
        VerticalLayout verticalLayout = new VerticalLayout(projectName, addButton);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(verticalLayout);
        return dialog;
    }

    Button deleteButton(AtomicInteger currentProjectId, Item i) {
        Button button = new Button("Delete", e -> {
            itemService.deleteItem(i.getId());
            refreshGrid(currentProjectId);
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }

    NumberField editValueField(AtomicInteger currentProjectId, Item i, boolean type) {
        NumberField numberField = new NumberField();
        if (type) numberField.setValue((double) i.getDemand());
        else numberField.setValue((double) i.getQuantity());
        numberField.setStep(1);
        numberField.setHasControls(true);
        numberField.addValueChangeListener(e -> {
            if (type) itemService.updateDemand(i.getId(), numberField.getValue().intValue());
            else itemService.updateQuantity(i.getId(), numberField.getValue().intValue());
            refreshGrid(currentProjectId);
        });
        return numberField;
    }

    TextField nameEditField(AtomicInteger currentProjectId, Item i) {
        TextField textField = new TextField();
        textField.setValue(i.getName());
        textField.addValueChangeListener(v -> {
            itemService.updateName(i.getId(), v.getValue());
            refreshGrid(currentProjectId);
        });
        return textField;
    }
}
