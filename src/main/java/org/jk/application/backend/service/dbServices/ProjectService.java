package org.jk.application.backend.service.dbServices;

import org.jk.application.backend.dao.ProjectDao;
import org.jk.application.backend.model.storage.Project;
import org.jk.application.backend.repositories.ProjectDB;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ProjectService implements ProjectDB {

    private final ProjectDao projectDao;

    public ProjectService(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void addProject(Project project) {
        projectDao.addProject(project);
    }

    @Override
    public Collection<Project> getProjects() {
        return projectDao.getProjects();
    }

    @Override
    public Project getProjectByName(String name) {
        return projectDao.getProjects().stream()
                .filter(project -> project.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public Project getProjectById(int id) {
        return projectDao.getProjects().stream()
                .filter(project -> project.getId() == id)
                .findFirst().orElse(null);
    }

    @Override
    public void deleteProject(int id) {
        projectDao.deleteProject(id);
    }

    @Override
    public void updateProject(Project project) {
        projectDao.deleteProject(project.getId());
        projectDao.addProject(project);
    }

    @Override
    public Collection<Integer> getIds() {
        return projectDao.getIds();
    }
}
