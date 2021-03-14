package org.jk.application.backend.service;

import org.jk.application.backend.model.order.Order;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.service.dbServices.ProductService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParserService {

    private final Document doc;
    private final ProductService productService;

    public XmlParserService(InputStream inputStream, ProductService productService) throws Exception {
        this.productService = productService;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
    }

    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        NodeList orderList = doc.getElementsByTagName("order");
        NodeList lineItems = doc.getElementsByTagName("lineItems");
        for (int temp = 0; temp < orderList.getLength(); temp++) {
            Node nNode = orderList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element orderElement = (Element) nNode;
                Element lineElement = (Element) lineItems.item(temp);

                List<Product> items = new ArrayList<>();
                for (int i = 0; i < lineElement.getElementsByTagName("offerName").getLength(); i++) {
                    Product item = new Product(
                            IdGenerator.generateProductId(productService),
                            lineElement.getElementsByTagName("offerName").item(i).getTextContent(),
                            Integer.parseInt(lineElement.getElementsByTagName("quantity").item(i).getTextContent()),
                            Double.parseDouble(lineElement.getElementsByTagName("amount").item(i).getTextContent()),
                            -1,
                            -1
                    );
                    items.add(item);
                }
                if (!orderElement.getElementsByTagName("status").item(1).getTextContent().equals("CANCELLED")) {
                    Order order = new Order(
                            orderElement.getElementsByTagName("id").item(0).getTextContent(),
                            orderElement.getElementsByTagName("orderDate").item(0).getTextContent(),
                            items
                    );
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public List<Object[]> getCountedItems() {

        List<Object[]> countedProducts = new ArrayList<>();

        for (Order order : getOrders()) {
            List<Product> items = order.getProducts();

            for (int i = 0; i < items.size(); i++) {
                int finalI = i;
                Product product = items.get(i);

                Object[] entry = countedProducts.stream().filter(p -> {
                    Product t = (Product) p[1];
                    return t.getName().equals(items.get(finalI).getName());
                }).findFirst().orElse(null);

                if (entry == null) {
                    countedProducts.add(new Object[]{1,
                            new Product(product.getId(), product.getName(),
                                    product.getQuantity(), product.getPrice(),
                                    product.getPrintPrice(), product.getPrintTime())});
                } else {
                    countedProducts.remove(entry);
                    countedProducts.add(new Object[]{(int) entry[0] + product.getQuantity(),
                            new Product(product.getId(), product.getName(),
                                    product.getQuantity(), product.getPrice(),
                                    product.getPrintPrice(), product.getPrintTime())});
                }
            }
        }
        return countedProducts;
    }

    public double getOrdersPrice() {
        double sum = 0;
        for (Object[] o : getCountedItems()) {
            sum = sum + ((Integer) o[0] * ((Product) o[1]).getPrice());
        }
        return sum;
    }

    public void updateProducts(List<Object[]> products) {
        for (Object[] o : products) {
            Product p = (Product) o[1];
            if (!productService.getNames().contains(p.getName())) {
                productService.addProduct(p);
            }
        }
    }
}
