package edu.epam.fop.web.demos.task8;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import edu.epam.fop.web.jpa.entity.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.List;

/**
 * Shows how the second-level cache works for {@link Course}, which is marked as {@code @Cacheable}.
 * <p>
 * An entity is looked up in three places, in this order:
 * <ol>
 *     <li>the first-level cache - the persistence context of the current session, it lives as long as the session;</li>
 *     <li>the second-level cache - the "edu.epam.fop.web.jpa.entity.Course" Ehcache region,
 *     it is shared by all sessions of the session factory;</li>
 *     <li>the database - a loaded entity is then put into both caches.</li>
 * </ol>
 * Statistics show how many second-level cache lookups found the entity (hit) or not (miss),
 * and how many entities were put into the second-level cache (put).
 * Each step prints the expected counters next to the actual ones.
 */
public class CacheDemo {
    public static void main(String[] args) {
        SessionFactory sessionFactory = getSessionFactory();
        Statistics stats = getStatistics(sessionFactory);

        List<Long> ids = saveTwoCourses(sessionFactory);
        Long course1Id = ids.get(0);
        Long course2Id = ids.get(1);

        cleanupCachesAndEnableStats(sessionFactory, stats);
        printData(stats, 0,0,0, "cleanupCachesAndEnableStats"); // expected 0-0-0
        getCourse1WithinOneSessionTwice(sessionFactory, course1Id);
        printData(stats, 0, 1, 1, "getCourse1WithinOneSessionTwice"); // 0-1-1
        getCourse(sessionFactory, course1Id);
        printData(stats, 1, 1, 1, "get course 1"); // 1-1-1
        getCourse(sessionFactory, course2Id);
        printData(stats, 1, 2, 2, "get course 2"); // 1-2-2
        getCourse(sessionFactory, course2Id);
        printData(stats, 2, 2, 2, "get course 2");
        cleanupAllCache(sessionFactory);
        System.out.println("cleanupAllCache");
        getCourse(sessionFactory, course1Id);
        printData(stats, 2, 3, 3, "get course 1"); // 2-3-3
        getCourse(sessionFactory, course2Id);
        printData(stats, 2, 4, 4, "get course 2"); // 2-4-4
        getCourse(sessionFactory, course1Id);
        printData(stats, 3, 4, 4, "get course 1"); // 3-4-4
        getCourse(sessionFactory, course2Id);
        printData(stats, 4, 4, 4, "get course 2"); // 4-4-4
        getCourse(sessionFactory, 123L);
        printData(stats, 4, 5, 4, "get unknown course"); // 4-5-4

        sessionFactory.close();
    }

    private static SessionFactory getSessionFactory() {
        // the second-level cache is enabled in hibernate.cfg.xml and configured in ehcache.xml
        return SessionFactoryProvider.createSessionFactory();
    }

    private static Statistics getStatistics(SessionFactory sessionFactory) {
        return sessionFactory.getStatistics();
    }

    private static void printData(Statistics stats, int hit, int miss, int put, String operation) {
        System.out.println();
        System.out.println("***** Second Level Cache STATS ***** operation = " + operation);
        System.out.print("exp Hit=" + hit + " ");
        System.out.print("Miss=" + miss + " ");
        System.out.println("Put=" + put + " ");
        System.out.print("act Hit=" + stats.getSecondLevelCacheHitCount() + " ");
        System.out.print("Miss=" + stats.getSecondLevelCacheMissCount() + " ");
        System.out.println("Put=" + stats.getSecondLevelCachePutCount() + " ");
        System.out.println();
    }

    private static List<Long> saveTwoCourses(SessionFactory sessionFactory) {
        return sessionFactory.fromTransaction(session -> {
            Course course1 = new Course("Cache demo course 1");
            Course course2 = new Course("Cache demo course 2");
            session.persist(course1);
            session.persist(course2);
            return List.of(course1.getId(), course2.getId());
        });
    }

    private static void cleanupCachesAndEnableStats(SessionFactory sessionFactory, Statistics stats) {
        // Saving the courses has already put them into the second-level cache,
        // so the cache and the counters are reset to start from scratch
        cleanupAllCache(sessionFactory);
        stats.clear();
        stats.setStatisticsEnabled(true);
    }

    private static void getCourse1WithinOneSessionTwice(SessionFactory sessionFactory, Long id) {
        try (Session session = sessionFactory.openSession()) {
            // The first lookup misses the second-level cache (miss), loads the course from the database
            // and puts it into the second-level cache (put) and into the session
            session.get(Course.class, id);
            // The second lookup is served by the first-level cache (the session itself),
            // the second-level cache is not even consulted, so the counters do not change
            session.get(Course.class, id);
        }
    }

    private static void cleanupAllCache(SessionFactory sessionFactory) {
        // Removes all entries from all second-level cache regions, the statistics counters are kept
        sessionFactory.getCache().evictAllRegions();
    }

    private static void getCourse(SessionFactory sessionFactory, Long id) {
        // A new session has an empty first-level cache, so the course is looked up in the second-level cache:
        // - if it is there, it is returned without querying the database (hit);
        // - otherwise, it is a miss, the course is loaded from the database and put into the cache (put);
        // - a course that does not exist in the database is a miss that puts nothing.
        try (Session session = sessionFactory.openSession()) {
            session.get(Course.class, id);
        }
    }
}
