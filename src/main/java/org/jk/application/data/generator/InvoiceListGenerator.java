package org.jk.application.data.generator;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.jk.application.data.model.order.Item;
import org.jk.application.data.model.order.Order;

import java.util.List;

public class InvoiceListGenerator {

    public static VerticalLayout getInvoice(Order order) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("--------------------------------------"));
        layout.add(new Label("FAKTURA " + order.getNumber()));
        layout.add(new Label(""));
        layout.add(new Label("Data sprzedaży: " + order.getDate()));
        layout.add(new Label(""));
        layout.add(new Label("Nabywca: " + order.getBuyer().getName()));
        layout.add(new Label("Tel: " + order.getBuyer().getPhone()));
        layout.add(new Label("Adres: " + order.getDelivery().getStreet()));
        layout.add(new Label("Kod pocztowy: " + order.getDelivery().getCity() + " " + order.getDelivery().getZipcode()));
        String payment = order.getPayment().getProvider();
        if (payment.equals("OFFLINE"))
            payment = "Pobranie";
        layout.add(new Label("Forma płatności: " + payment));
        layout.add(new Label(""));
        List<Item> items = order.getItems();
        for (Item item : items) {
            layout.add(new Label("Nazwa produktu: " + item.getName()));
            layout.add(new Label("Ilość: " + item.getQuantity()));
            layout.add(new Label("Cena: " + item.getPrice()));
        }
        layout.add(new Label("Przesyłka: " + order.getDelivery().getCost() + " " + order.getDelivery().getMethodName()));
        return layout;
    }
}
