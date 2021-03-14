package org.jk.application.backend.service.dbServices;

import org.jk.application.backend.dao.ProductDao;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.repositories.ProductDB;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ProductService implements ProductDB {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void addProduct(Product product) {
        productDao.addProduct(product);
    }

    @Override
    public void deleteProduct(int id) {
        productDao.deleteProduct(id);
    }

    @Override
    public List<Product> getProducts() {
        return List.copyOf(productDao.getProducts());
    }

    @Override
    public Collection<Integer> getIds() {
        return productDao.getIds();
    }

    @Override
    public Collection<String> getNames() {
        return productDao.getNames();
    }

    @Override
    public void updateProduct(Product product) {
        productDao.updateProduct(product);
    }
}
