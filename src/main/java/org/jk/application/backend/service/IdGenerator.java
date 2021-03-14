package org.jk.application.backend.service;

import org.jk.application.backend.service.dbServices.ItemService;
import org.jk.application.backend.service.dbServices.ProjectService;

import java.util.Random;

public class IdGenerator {

    private final ProjectService projectService;
    private final ItemService itemService;

    public IdGenerator(ProjectService projectService, ItemService itemService) {
        this.projectService = projectService;
        this.itemService = itemService;
    }

    public int generateProjectId() {
        int id = getRandomNumber();

        while (projectService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    public int generateItemId() {
        int id = getRandomNumber();

        while(itemService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    private int getRandomNumber() {
        Random random = new Random();
        return random.ints(1000, 10000).findFirst().orElse(-1);
    }
}
