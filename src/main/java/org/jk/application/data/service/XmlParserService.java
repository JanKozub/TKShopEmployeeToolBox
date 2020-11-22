package org.jk.application.data.service;

import org.jk.application.data.entity.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParserService {

    private Document doc;

    public XmlParserService(String filePath) throws Exception {
        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
    }

    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        NodeList orderList = doc.getElementsByTagName("order");
        NodeList deliveryList = doc.getElementsByTagName("cost");
        NodeList lineItems = doc.getElementsByTagName("lineItems");
        for (int temp = 0; temp < orderList.getLength(); temp++) {
            Node nNode = orderList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element orderElement = (Element) nNode;
                Element deliveryElement = (Element) deliveryList.item(temp);
                Element lineElement = (Element) lineItems.item(temp);

                List<Item> items = new ArrayList<>();
                for (int i = 0; i < lineElement.getElementsByTagName("offerName").getLength(); i++) {
                    Item item = new Item(
                            lineElement.getElementsByTagName("offerId").item(i).getTextContent(),
                            lineElement.getElementsByTagName("offerName").item(i).getTextContent(),
                            lineElement.getElementsByTagName("quantity").item(i).getTextContent(),
                            Double.parseDouble(lineElement.getElementsByTagName("amount").item(i).getTextContent())
                    );
                    items.add(item);
                }

                Order order = new Order(
                        orderElement.getElementsByTagName("id").item(0).getTextContent(),
                        orderElement.getElementsByTagName("orderDate").item(0).getTextContent(),
                        new Buyer(
                                orderElement.getElementsByTagName("name").item(0).getTextContent(),
                                orderElement.getElementsByTagName("email").item(0).getTextContent(),
                                orderElement.getElementsByTagName("phoneNumber").item(0).getTextContent()
                        ),
                        items,
                        new Payment(
                                orderElement.getElementsByTagName("lastChanged").item(0).getTextContent(),
                                orderElement.getElementsByTagName("provider").item(0).getTextContent(),
                                orderElement.getElementsByTagName("amount").item(0).getTextContent()
                        ),
                        new Delivery(
                                orderElement.getElementsByTagName("methodName").item(0).getTextContent(),
                                orderElement.getElementsByTagName("phoneNumber").item(0).getTextContent(),
                                orderElement.getElementsByTagName("street").item(0).getTextContent(),
                                orderElement.getElementsByTagName("zipCode").item(0).getTextContent(),
                                orderElement.getElementsByTagName("city").item(0).getTextContent(),
                                deliveryElement.getElementsByTagName("amount").item(0).getTextContent()
                        )
                );

                orders.add(order);
            }
        }
        return orders;
    }
}
