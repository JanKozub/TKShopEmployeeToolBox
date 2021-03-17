package org.jk.application.backend.dao;

import org.apache.ibatis.annotations.*;
import org.jk.application.backend.model.storage.Item;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ItemDao {

    @Insert("INSERT INTO Items(id, projectId, name, quantity, demand) " +
            "VALUES(#{id}, #{projectId}, #{name}, #{quantity}, #{demand})")
    void addItem(Item item);

    @Select("SELECT * FROM Items")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class),
            @Arg(column = "projectId", javaType = int.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "quantity", javaType = int.class),
            @Arg(column = "demand", javaType = int.class),
    })
    Collection<Item> getItems();

    @Select("SELECT * FROM Items")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class),
    })
    Collection<Integer> getIds();

    @Delete("DELETE FROM Items WHERE id = #{id}")
    void deleteItem(int id);

    @Update("UPDATE Items SET name = #{name} WHERE id = #{id}")
    void updateName(int id, String name);

    @Update("UPDATE Items SET quantity = #{quantity} WHERE id = #{id}")
    void updateQuantity(int id, int quantity);

    @Update("UPDATE Items SET demand = #{demand} WHERE id = #{id}")
    void updateDemand(int id, int demand);

}
