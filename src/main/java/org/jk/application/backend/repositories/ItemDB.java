package org.jk.application.backend.repositories;

import org.jk.application.backend.model.storage.Item;

import java.util.Collection;

public interface ItemDB {

    void addItem(Item item);

    Item getItemById(int id);

    Collection<Item> getItems();

    Collection<Item> getItemsWithProjectId(int id);

    Collection<Integer> getIds();

    void deleteItem(int id);
}
