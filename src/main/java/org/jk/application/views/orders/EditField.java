package org.jk.application.views.orders;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.dbServices.ProductService;

public class EditField extends HorizontalLayout {
    public EditField(Product p, ProductService productService, Grid<Product> productGrid, boolean type) {
        String label;
        if (type) {
            label = p.getPrintPrice() + " PLN";
            if (label.equals("-1.0 PLN")) label = "UNSET";
        } else {
            label = p.getPrintTime() + " h";
            if (label.equals("-1.0 h")) label = "UNSET";
        }

        Button button = new Button(label, VaadinIcon.EDIT.create());
        button.setWidth("140px");

        NumberField nf = new NumberField();
        nf.setWidth("140px");
        nf.addKeyDownListener(Key.ENTER, c -> {
            removeAll();
            if (type) {
                productService.updateProduct(new Product(p.getId(), p.getName(), p.getQuantity(),
                        p.getPrice(), nf.getValue(), p.getPrintPrice()));
            } else {
                productService.updateProduct(new Product(p.getId(), p.getName(), p.getQuantity(),
                        p.getPrice(), p.getPrintPrice(), nf.getValue()));
            }
            productGrid.setItems(productService.getProducts());
            add(button);
        });

        button.addClickListener(b -> {
            removeAll();
            add(nf);
            nf.focus();
        });
        button.getStyle().set("background-color", "#1676f3");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(button);
    }

}
