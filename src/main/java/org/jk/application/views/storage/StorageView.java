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

    private int currentProjectId = 0;

    public StorageView(ProjectService projectService, ItemService itemService) {
        this.projectService = projectService;
        this.itemService = itemService;

        setId("storage-view");

        header = new HorizontalLayout();
        inventory = new Button("Inventory", VaadinIcon.LIST.create(), e -> {
            currentProjectId = 0;
            refreshTopBar();
        });
        inventory.getStyle().set("margin-left", "10px");

        Dialog addProjectDialog = createAddProjectDialog();

        addProject = new Button("Add Project", VaadinIcon.PLUS.create(), e -> addProjectDialog.open());
        addProject.getStyle().set("margin-left", "10px");
        addProject.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        header.setWidth("100%");
        refreshTopBar();

        Dialog itemDialog = createAddItemDialog();
        Button addItem = new Button("Add Item", VaadinIcon.PLUS_CIRCLE_O.create(), i -> itemDialog.open());
        addItem.setWidth("50%");
        addItem.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Dialog deleteProjectDialog = createDeleteProjectDialog();
        Button deleteProject = new Button("Delete Project", VaadinIcon.MINUS_CIRCLE_O.create(), dp -> deleteProjectDialog.open());
        deleteProject.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteProject.setWidth("50%");

        HorizontalLayout buttons = new HorizontalLayout(addItem, deleteProject);
        buttons.setWidth("100%");

        grid = new Grid<>();

        grid.addColumn(Item::getName).setHeader("Name");
        grid.addColumn(Item::getQuantity).setHeader("Quantity");
        grid.addColumn(Item::getDemand).setHeader("Demand");
        grid.addColumn(Item::getId).setHeader("ID");
        grid.addColumn(new ComponentRenderer<>(b -> new Button("Edit",e -> {
        }))).setHeader("Edit");
        grid.addColumn(new ComponentRenderer<>(b -> {
            Button button = new Button("Delete", e -> {
                itemService.deleteItem(b.getId());
                refreshGrid(currentProjectId);
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        })).setHeader("Delete");

        add(header, createCenterLine(), buttons, grid);

        setSizeFull();
    }

    private Dialog createAddProjectDialog() {
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

    private Dialog createAddItemDialog() {
        Dialog itemDialog = new Dialog(new Label("New Item"));
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
            refreshGrid(currentProjectId);
        });
        confirmBtn.setWidth("100%");
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        VerticalLayout idvl = new VerticalLayout(itemName, itemQuantity, itemDemand, confirmBtn);
        idvl.setAlignItems(Alignment.CENTER);
        itemDialog.add(idvl);
        return itemDialog;
    }

    private Dialog createDeleteProjectDialog() {
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
                refreshGrid(p.getId());
                currentProjectId = p.getId();
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
    }

    private void refreshGrid(int id) {
        grid.setItems(itemService.getItemsWithProjectId(id));
    }
}
