package edu.epam.fop.web.demos.task7;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import edu.epam.fop.web.jpa.entity.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Shows the whole path DETACHED -> PERSISTENT -> REMOVED -> DETACHED.
 * <ul>
 *     <li><b>Detached</b> - the object has an id and a database row, but no session tracks it.</li>
 *     <li><b>Persistent</b> (managed) - the object is associated with an open session.</li>
 *     <li><b>Removed</b> - the object is scheduled for deletion: the session no longer considers it managed,
 *     but its row is deleted only when the session is flushed (here - on commit).</li>
 * </ul>
 * Once the deletion is committed and the session is closed, the object is detached again:
 * it still holds its id and data in memory, but there is neither a row nor a session behind it.
 */
public class DetachedThenPersistentThenRemovedAndDetachedStatesDemo {

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = SessionFactoryProvider.createSessionFactory()) {
            Course detached = new Course("Lifecycle demo: detached, persistent, removed");

            // The course is saved by a session that is closed right away, so the course ends up DETACHED
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.persist(detached);
                transaction.commit();
            }
            Long id = detached.getId();
            System.out.println("[DETACHED]   id = " + id + ", the session that saved the course is closed");

            Course course;
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                // DETACHED -> PERSISTENT: get() loads the row into this session as a managed instance.
                // It is a new instance for the same row, the original detached object itself stays detached.
                course = session.get(Course.class, id);
                System.out.println("[PERSISTENT] session.contains(course) = " + session.contains(course)
                        + ", session.contains(detached) = " + session.contains(detached));

                // PERSISTENT -> REMOVED: remove() only schedules the deletion, no DELETE is executed yet
                session.remove(course);
                System.out.println("[REMOVED]    session.contains(course) = " + session.contains(course)
                        + ", session.get() in the same session returns " + session.get(Course.class, id));

                // The DELETE statement is executed when the session is flushed on commit
                transaction.commit();
                System.out.println("[REMOVED]    deletion committed");
            }

            // REMOVED -> DETACHED: the session is closed. The removed object still has its id and name in memory...
            System.out.println("[DETACHED]   id = " + course.getId() + ", name = " + course.getName());

            // ...but the row is gone, so a new session finds nothing
            try (Session session = sessionFactory.openSession()) {
                System.out.println("[DETACHED]   session.get() in a new session returns " + session.get(Course.class, id));
            }
        }
    }
}
