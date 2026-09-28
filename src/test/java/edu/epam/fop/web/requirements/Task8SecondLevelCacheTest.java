package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Course;
import jakarta.persistence.Cacheable;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.jcache.ConfigSettings;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 8: courses are stored in the second-level cache.
 */
class Task8SecondLevelCacheTest extends DatabaseTest {

    private Statistics stats;

    @BeforeEach
    void resetStatistics() {
        stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
    }

    @Test
    void secondLevelCacheIsEnabledWithEhcache() {
        Map<String, Object> settings = sessionFactory.getProperties();

        assertEquals("true", settings.get(AvailableSettings.USE_SECOND_LEVEL_CACHE));
        assertEquals("org.hibernate.cache.jcache.internal.JCacheRegionFactory",
                settings.get(AvailableSettings.CACHE_REGION_FACTORY));
        assertEquals("ehcache.xml", settings.get(ConfigSettings.CONFIG_URI));
        assertEquals("org.ehcache.jsr107.EhcacheCachingProvider", settings.get(ConfigSettings.PROVIDER));
    }

    @Test
    void courseIsCacheableWithReadWriteStrategy() {
        assertNotNull(Course.class.getAnnotation(Cacheable.class));
        assertEquals(CacheConcurrencyStrategy.READ_WRITE, Course.class.getAnnotation(Cache.class).usage());
    }

    @Test
    void courseIsReadFromSecondLevelCacheInAnotherSession() {
        Long id = sessionFactory.fromTransaction(session -> {
            Course course = new Course("course-1");
            session.persist(course);
            return course.getId();
        });
        sessionFactory.getCache().evictAllRegions();
        stats.clear();

        // the first read misses the cache, loads the course from the database and puts it into the cache;
        // the second read in the same session is served by the session itself
        try (Session session = sessionFactory.openSession()) {
            session.get(Course.class, id);
            session.get(Course.class, id);
        }
        assertStats(0, 1, 1);
        assertTrue(sessionFactory.getCache().containsEntity(Course.class, id));

        // another session finds the course in the second-level cache
        readCourse(id);
        assertStats(1, 1, 1);

        // after eviction, the course is loaded from the database and cached again
        sessionFactory.getCache().evictAllRegions();
        assertFalse(sessionFactory.getCache().containsEntity(Course.class, id));
        readCourse(id);
        assertStats(1, 2, 2);

        // an unknown course is a miss that puts nothing
        readCourse(id + 1);
        assertStats(1, 3, 2);
    }

    private void readCourse(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.get(Course.class, id);
        }
    }

    private void assertStats(long hits, long misses, long puts) {
        assertEquals(hits, stats.getSecondLevelCacheHitCount(), "hits");
        assertEquals(misses, stats.getSecondLevelCacheMissCount(), "misses");
        assertEquals(puts, stats.getSecondLevelCachePutCount(), "puts");
    }
}
