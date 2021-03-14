package org.jk.application.backend.dao;

import org.apache.ibatis.annotations.*;
import org.jk.application.backend.model.Project;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjectDao {

    @Insert("INSERT INTO Projects(id, name) VALUES(#{id}, #{name})")
    void addProject(Project project);

    @Select("SELECT * FROM Projects")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class),
            @Arg(column = "name", javaType = String.class),
    })
    Collection<Project> getProjects();

    @Delete("DELETE FROM Projects WHERE id = #{id}")
    void deleteProject(int id);

    @Select("SELECT * FROM Projects")
    @ConstructorArgs({
            @Arg(column = "id", javaType = int.class)
    })
    Collection<Integer> getIds();

}
