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

class EditField extends HorizontalLayout {
    EditField(Product p, ProductService productService, boolean type) {
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
            nf.setVisible(false);
            if (!nf.isEmpty()) {
                if (type) {
                    productService.updateProduct(new Product(p.getId(), p.getName(), p.getQuantity(),
                            p.getPrice(), nf.getValue(), p.getPrintTime()));
                    button.setText(productService.getProductByName(p.getName()).getPrintPrice() + " PLN");
                } else {
                    productService.updateProduct(new Product(p.getId(), p.getName(), p.getQuantity(),
                            p.getPrice(), p.getPrintPrice(), nf.getValue()));
                    button.setText(productService.getProductByName(p.getName()).getPrintTime() + " h");
                }
            }
            button.setVisible(true);
        });

        button.addClickListener(b -> {
            button.setVisible(false);
            nf.setVisible(true);
            nf.focus();
        });
        button.getStyle().set("background-color", "#1676f3");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setVisible(true);
        nf.setVisible(false);
        add(button, nf);
    }

}
