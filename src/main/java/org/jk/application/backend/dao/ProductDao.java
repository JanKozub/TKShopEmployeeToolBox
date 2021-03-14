package org.jk.application.backend.dao;

import org.apache.ibatis.annotations.*;
import org.jk.application.backend.model.order.Product;
import org.jk.application.backend.model.storage.Project;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductDao {

    @Insert("INSERT INTO Products(id, name, quantity, price, printPrice, printTime) " +
            "VALUES(#{id}, #{name}, #{quantity}, #{price}, #{printPrice}, #{printTime})")
    void addProduct(Product product);

    @Select("SELECT * FROM Products")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "quantity", javaType = int.class),
            @Arg(column = "price", javaType = double.class),
            @Arg(column = "printPrice", javaType = double.class),
            @Arg(column = "printTime", javaType = double.class)
    })
    Collection<Product> getProducts();

    @Delete("DELETE FROM Products WHERE id = #{id}")
    void deleteProduct(int id);

    @Select("SELECT * FROM Products")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class)
    })
    Collection<Integer> getIds();

    @Select("SELECT * FROM Products")
    @ConstructorArgs({
            @Arg(column = "name", javaType = int.class)
    })
    Collection<String> getNames();

    @Update("UPDATE Products SET printPrice = #{printPrice}, printTime = #{printTime} WHERE id = #{id}")
    void updateProduct(Product product);
}
