package org.jk.application.views.storage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.backend.model.storage.Item;
import org.jk.application.backend.service.dbServices.ItemService;
import org.jk.application.backend.service.dbServices.ProjectService;
import org.jk.application.views.main.MainView;

import java.util.concurrent.atomic.AtomicInteger;

@Route(value = "storage", layout = MainView.class)
@PageTitle("Storage")
public class StorageView extends VerticalLayout {

    private final StorageLayouts storageLayouts;
    private final AtomicInteger currentProjectId = new AtomicInteger(0);

    public StorageView(ProjectService projectService, ItemService itemService) {

        Grid<Item> grid = new Grid<>();
        Button inventory = new Button("Inventory", VaadinIcon.LIST.create());
        Button addProject = new Button("Add Project", VaadinIcon.PLUS.create());
        HorizontalLayout header = new HorizontalLayout();
        Button editProjectButton = new Button("Edit Project", VaadinIcon.EDIT.create());
        Button deleteProjectButton = new Button("Delete Project", VaadinIcon.MINUS_CIRCLE_O.create());

        storageLayouts = new StorageLayouts(itemService, projectService, header, inventory,
                addProject, grid, editProjectButton, deleteProjectButton);

        setId("storage-view");

        inventory.addClickListener(e -> {
            currentProjectId.set(0);
            storageLayouts.refreshTopBar(currentProjectId);
            storageLayouts.refreshGrid(currentProjectId);
        });
        inventory.getStyle().set("margin-left", "10px");

        addProject.addClickListener(e -> storageLayouts.addProjectDialog(currentProjectId).open());
        addProject.getStyle().set("margin-left", "10px");
        addProject.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        header.setWidth("100%");

        Button addItem = new Button("Add Item", VaadinIcon.PLUS_CIRCLE_O.create(), i -> storageLayouts.addItemDialog(currentProjectId).open());
        addItem.setWidth("33%");
        addItem.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        editProjectButton.addClickListener(e -> storageLayouts.editProjectDialog(currentProjectId).open());
        editProjectButton.setWidth("33%");

        deleteProjectButton.addClickListener(e -> storageLayouts.deleteProjectDialog(currentProjectId).open());
        deleteProjectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteProjectButton.setWidth("33%");

        HorizontalLayout buttons = new HorizontalLayout(addItem, editProjectButton, deleteProjectButton);
        buttons.setWidth("100%");

        grid.addColumn(new ComponentRenderer<>(i -> storageLayouts.nameEditField(currentProjectId, i))).setHeader("Name");
        grid.addColumn(new ComponentRenderer<>(i -> storageLayouts.editValueField(currentProjectId, i, false))).setHeader("Quantity");
        grid.addColumn(new ComponentRenderer<>(i -> storageLayouts.editValueField(currentProjectId, i, true))).setHeader("Demand");
        grid.addColumn(Item::getId).setHeader("ID");
        grid.addColumn(new ComponentRenderer<>(i -> storageLayouts.deleteButton(currentProjectId, i))).setHeader("Delete");
        add(header, storageLayouts.createCenterLine(), buttons, grid);

        setSizeFull();
        storageLayouts.refreshGrid(currentProjectId);
        storageLayouts.refreshTopBar(currentProjectId);
    }
}
