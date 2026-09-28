package edu.epam.fop.web.jpa;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

/**
 * Creates Hibernate {@link SessionFactory} instances configured by {@value #CONFIG_RESOURCE}.
 * <p>
 * Before a factory is built, the database schema is brought up to date with the Flyway migrations
 * from {@value #MIGRATIONS_LOCATION}, using the same connection settings as Hibernate.
 */
public final class SessionFactoryProvider {

    private static final String CONFIG_RESOURCE = "hibernate.cfg.xml";
    private static final String MIGRATIONS_LOCATION = "classpath:db/migration";

    private SessionFactoryProvider() {
    }

    /**
     * Migrates the database schema and builds a new session factory.
     * The caller owns the returned factory and is responsible for closing it.
     *
     * @return a new, ready to use session factory
     */
    public static SessionFactory createSessionFactory() {
        return createSessionFactory(new Properties());
    }

    /**
     * Same as {@link #createSessionFactory()}, but the given settings replace the ones from {@value #CONFIG_RESOURCE},
     * e.g. to connect to another database schema.
     *
     * @param overriddenSettings Hibernate settings that take precedence over the configuration file
     * @return a new, ready to use session factory
     */
    public static SessionFactory createSessionFactory(Properties overriddenSettings) {
        Configuration configuration = new Configuration().configure(CONFIG_RESOURCE);
        configuration.addProperties(overriddenSettings);
        migrateSchema(configuration.getProperties());
        return configuration.buildSessionFactory();
    }

    private static void migrateSchema(Properties settings) {
        Flyway.configure()
                .dataSource(
                        settings.getProperty(AvailableSettings.JAKARTA_JDBC_URL),
                        settings.getProperty(AvailableSettings.JAKARTA_JDBC_USER),
                        settings.getProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD))
                .locations(MIGRATIONS_LOCATION)
                .load()
                .migrate();
    }
}
