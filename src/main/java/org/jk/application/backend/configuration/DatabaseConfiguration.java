package org.jk.application.backend.configuration;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.type.TypeHandler;
import org.jk.application.backend.service.TimeService;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Configuration
@Component
@ComponentScan("org.jk.application.backend.dao")
@MapperScan("org.jk.application.backend.dao")
class DatabaseConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, List<TypeHandler<?>> typeHandlers) {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeHandlers(typeHandlers.toArray(new TypeHandler[0]));
        return sessionFactory;
    }

    @Bean
    public DataSource dataSourceLocal(@Value("${db.url:file:~/test}") String databasePath, @Value("${db.username:sa}") String username, @Value("${db.password:sa}") String password) throws SQLException, IOException {
        String url = "jdbc:h2:" + databasePath;
        PooledDataSource dataSource = new PooledDataSource("org.h2.Driver", url, username, password);
        dataSource.setPoolMaximumActiveConnections(10);

        try (Connection connection = dataSource.getConnection()) {
            int currentSchemaVersion = initializeDatabase(connection) + 1;

            while (tryApplyPatch(currentSchemaVersion, connection)) currentSchemaVersion++;

            log.info("Database version: {}", getSchemaVersion(connection));
        }

        return dataSource;
    }


    private int initializeDatabase(Connection connection) throws IOException, SQLException {
        try {
            return getSchemaVersion(connection);
        } catch (SQLException ex) {
            log.info("Initializing database");
            tryApplyPatch(0, connection);
            return 0;
        }
    }

    private boolean tryApplyPatch(int version, Connection connection) throws IOException, SQLException {
        String path = getPatchResource(version);
        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            log.info("Applying DB patch {}", path);
            try (InputStream initScript = resource.getInputStream();
                 Statement statement = connection.createStatement()) {

                String script = new String(initScript.readAllBytes());
                statement.executeUpdate(script);
                statement.executeUpdate("INSERT INTO DbPatches(patch_number, applied_date) VALUES(" + version + ", " + TimeService.now() + ")");
                return true;
            }
        } else return false;
    }

    private int getSchemaVersion(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT MAX(patch_number) FROM DbPatches");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else
                throw new SQLException("No schema");
        }
    }

    private String getPatchResource(int version) {
        if (version == 0) {
            return "db-patches/db-init.sql";
        } else
            return "db-patches/db-patch-" + version + ".sql";
    }
}
