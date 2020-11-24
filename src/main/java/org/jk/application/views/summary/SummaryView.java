package org.jk.application.views.summary;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jk.application.data.model.order.Item;
import org.jk.application.data.model.order.Order;
import org.jk.application.data.service.XmlParserService;
import org.jk.application.views.main.MainView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "summary-view", layout = MainView.class)
@PageTitle("Summary")
public class SummaryView extends VerticalLayout {

    public SummaryView() {
        setId("summary-view");
        List<Order> orders = new ArrayList<>();
        try {
            XmlParserService xmlParserService = new XmlParserService("src/main/resources/files/temp.xml");
            orders = xmlParserService.getOrders();
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

        List<Entry> entries = new ArrayList<>();

        for (Order order : orders) {
            List<Item> items = order.getItems();

            for (int i = 0; i < items.size(); i++) {
                int finalI = i;
                Item item = items.get(i);
                Entry entry = entries.stream().filter(p -> p.getName().equals(items.get(finalI).getName())).findFirst().orElse(null);
                if (entry == null) {
                    entries.add(new Entry(item.getName(), 1, item.getPrice()));
                } else {
                    entries.remove(entry);
                    entries.add(new Entry(item.getName(), entry.getNum() + item.getQuantity(), item.getPrice()));
                }
            }
        }

        int sum = 0;
        for (Entry entry : entries) {
            sum = sum + entry.getNum();
        }

        ListDataProvider<Entry> dataProvider =
                DataProvider.ofCollection(entries);

        dataProvider.setSortOrder(Entry::getNum, SortDirection.DESCENDING);

        Grid<Entry> itemGrid = new Grid<>();
        itemGrid.setWidthFull();
        itemGrid.setHeightFull();

        itemGrid.addColumn(new TextRenderer<>(Entry::getName)).setHeader("Name");
        itemGrid.addColumn(new TextRenderer<>(entry -> Integer.toString(entry.getNum()))).setHeader("Psc");
        itemGrid.addColumn(new TextRenderer<>(entry -> entry.getPrice() + " PLN")).setHeader("Price/psc");
        itemGrid.setItems(dataProvider);

        setSizeFull();
        add(itemGrid, new Label("sum= " + sum));
    }

    public static class Entry {

        private String name;
        private int num;
        private double price;

        public Entry(String name, int num, double price) {
            this.name = name;
            this.num = num;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

}
