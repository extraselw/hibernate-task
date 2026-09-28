package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * Base class of the tests that work with the database.
 * <p>
 * The tests work in their own schema {@value #TEST_SCHEMA},
 * which is recreated and migrated by Flyway for every test class. All tables are emptied, and their ids
 * restarted from 1, before every test, so the tests do not depend on each other.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DatabaseTest {

    private static final String TEST_SCHEMA = "university_test";

    protected SessionFactory sessionFactory;

    @BeforeAll
    void createSessionFactoryForTestSchema() throws SQLException {
        Properties settings = new Configuration().configure().getProperties();
        String url = settings.getProperty(AvailableSettings.JAKARTA_JDBC_URL);
        recreateTestSchema(url,
                settings.getProperty(AvailableSettings.JAKARTA_JDBC_USER),
                settings.getProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD));

        Properties testSettings = new Properties();
        testSettings.setProperty(AvailableSettings.JAKARTA_JDBC_URL,
                url + (url.contains("?") ? "&" : "?") + "currentSchema=" + TEST_SCHEMA);
        sessionFactory = SessionFactoryProvider.createSessionFactory(testSettings);
    }

    @AfterAll
    void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void emptyTables() {
        execute("truncate grade, discipline_course, discipline, course, student, department restart identity");
        // the second-level cache does not know about the truncation, so it is cleared as well
        sessionFactory.getCache().evictAllRegions();
    }

    /**
     * Runs a native SQL query that returns a single text column.
     *
     * @param parameters values of the positional parameters {@code ?1}, {@code ?2}, ...
     */
    protected List<String> selectStrings(String sql, Object... parameters) {
        return sessionFactory.fromTransaction(session -> {
            NativeQuery<String> query = session.createNativeQuery(sql, String.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        });
    }

    protected void execute(String sql) {
        sessionFactory.inTransaction(session -> session.createNativeMutationQuery(sql).executeUpdate());
    }

    private static void recreateTestSchema(String url, String user, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            statement.execute("drop schema if exists " + TEST_SCHEMA + " cascade");
            statement.execute("create schema " + TEST_SCHEMA);
        }
    }
}
