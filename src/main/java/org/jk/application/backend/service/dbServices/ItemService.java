package org.jk.application.backend.service.dbServices;

import org.jk.application.backend.dao.ItemDao;
import org.jk.application.backend.model.storage.Item;
import org.jk.application.backend.repositories.ItemDB;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ItemService implements ItemDB {

    private final ItemDao itemDao;

    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public void addItem(Item item) {
        itemDao.addItem(item);
    }

    @Override
    public Item getItemById(int id) {
        return itemDao.getItems().stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Collection<Item> getItems() {
        return itemDao.getItems();
    }

    @Override
    public Collection<Item> getItemsWithProjectId(int id) {
        List<Item> items = new ArrayList<>();
        for (Item i : itemDao.getItems()) {
            if (i.getProjectId() == id)
                items.add(i);
        }
        return items;
    }

    @Override
    public Collection<Integer> getIds() {
        return itemDao.getIds();
    }

    @Override
    public void deleteItem(int id) {
        itemDao.deleteItem(id);
    }
}
