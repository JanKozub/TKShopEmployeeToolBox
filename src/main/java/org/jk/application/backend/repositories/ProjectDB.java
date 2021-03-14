package org.jk.application.backend.repositories;

import org.jk.application.backend.model.storage.Project;

import java.util.Collection;

public interface ProjectDB {

    void addProject(Project project);

    Collection<Project> getProjects();

    Project getProjectByName(String name);

    Project getProjectById(int id);

    void deleteProject(int id);

    void updateProject(Project project);

    Collection<Integer> getIds();
}
