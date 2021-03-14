package org.jk.application.backend.service;

import org.jk.application.backend.service.dbServices.ProjectService;

import java.util.Random;

public class IdGenerator {

    private final ProjectService projectService;

    public IdGenerator(ProjectService projectService) {
        this.projectService = projectService;
    }

    public int generateProjectId() {
        int id = getRandomNumber();

        while (projectService.getIds().contains(id)) {
            id = getRandomNumber();
        }
        return id;
    }

    private int getRandomNumber() {
        Random random = new Random();
        return random.ints(1000, 10000).findFirst().orElse(-1);
    }
}
