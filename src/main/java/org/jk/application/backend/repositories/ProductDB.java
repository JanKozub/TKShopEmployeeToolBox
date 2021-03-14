package org.jk.application.backend.repositories;

import org.jk.application.backend.model.order.Product;

import java.util.Collection;
import java.util.List;

public interface ProductDB {

    void addProduct(Product product);

    void deleteProduct(int id);

    List<Product> getProducts();

    Collection<Integer> getIds();
}