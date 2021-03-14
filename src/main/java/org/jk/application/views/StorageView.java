package org.jk.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.backend.model.Item;
import org.jk.application.backend.model.Project;
import org.jk.application.backend.service.IdGenerator;
import org.jk.application.backend.service.dbServices.ProjectService;
import org.jk.application.views.main.MainView;

import java.util.Collection;

@Route(value = "storage", layout = MainView.class)
@PageTitle("Storage")
public class StorageView extends VerticalLayout {

    private final ProjectService projectService;
    private final IdGenerator idGenerator;

    private HorizontalLayout header;
    private Button inventory;
    private Button addProject;
    private Grid<Item> grid;

    private int currentProjectId = 0;

    public StorageView(ProjectService projectService) {
        this.projectService = projectService;
        this.idGenerator = new IdGenerator(projectService);

        setId("storage-view");

        header = new HorizontalLayout();
        inventory = new Button("Inventory", VaadinIcon.LIST.create(), e -> {
            currentProjectId = 0;
            refreshTopBar();

        });
        inventory.getStyle().set("margin-left", "10px");

        Dialog addProjectDialog = new Dialog();
        TextField projectName = new TextField("Project Name");
        Button addButton = new Button("Add Project");
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(
                clk -> {
                    projectService.addProject(new Project(idGenerator.generateProjectId(), projectName.getValue()));
                    refreshTopBar();
                    projectName.clear();
                    addProjectDialog.close();
                }
        );
        VerticalLayout verticalLayout = new VerticalLayout(projectName, addButton);
        verticalLayout.setAlignItems(Alignment.CENTER);
        addProjectDialog.add(verticalLayout);

        addProject = new Button("Add Project", VaadinIcon.PLUS.create(), e -> addProjectDialog.open());
        addProject.getStyle().set("margin-left", "10px");

        addProject.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        header.setWidth("100%");
        refreshTopBar();

        Div line = new Div();
        line.getStyle().set("background-color", "rgba(173, 200, 235, 0.14)");
        line.setWidth("100%");
        line.setHeight("3px");

        Button addItem = new Button("Add Item");
        addItem.setWidth("25%");
        addItem.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button removeItem = new Button("Remove Item");
        removeItem.setWidth("25%");
        removeItem.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button editProject = new Button("Edit Project");
        editProject.setWidth("25%");
        editProject.getStyle().set("color", "lightblue");

        Button deleteProject = new Button("Delete Project", dp -> {
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
            dialog.open();
        });
        deleteProject.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteProject.setWidth("25%");

        HorizontalLayout buttons = new HorizontalLayout(addItem, removeItem, editProject, deleteProject);
        buttons.setWidth("100%");

        grid = new Grid<>();

        grid.addColumn(Item::getName).setHeader("Name");
        grid.addColumn(Item::getQuantity).setHeader("Quantity");
        grid.addColumn(Item::getDemand).setHeader("Demand");
        grid.addColumn(Item::getId).setHeader("ID");

        add(header, line, buttons, grid);

        setSizeFull();
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
                System.out.println(currentProjectId);
                if (currentProjectId == 0) {
                    inventory.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                } else
                    inventory.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
                button.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
            }

            header.add(button);
        }
        header.add(addProject);
    }

    private void refreshGrid(int id) {
    }
}
