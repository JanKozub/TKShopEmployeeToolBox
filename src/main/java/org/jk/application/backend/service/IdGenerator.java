package org.jk.application.backend.service;

import org.jk.application.backend.service.dbServices.ItemService;
import org.jk.application.backend.service.dbServices.ProductService;
import org.jk.application.backend.service.dbServices.ProjectService;

import java.util.Random;

public class IdGenerator {

    public IdGenerator() {
    }
    public static int generateProjectId(ProjectService projectService) {
        int id = getRandomNumber();

        while (projectService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    public static int generateItemId(ItemService itemService) {
        int id = getRandomNumber();

        while(itemService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    public static int generateProductId(ProductService productService) {
        int id = getRandomNumber();
        while(productService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    private static int getRandomNumber() {
        Random random = new Random();
        return random.ints(1000, 10000).findFirst().orElse(-1);
    }
}
