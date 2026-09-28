package edu.epam.fop.web.demos.task7;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import edu.epam.fop.web.jpa.entity.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Shows how a PERSISTENT entity becomes DETACHED and how a DETACHED entity becomes PERSISTENT again.
 * <ul>
 *     <li><b>Persistent</b> (managed) - the object is associated with an open session, which keeps it
 *     in sync with the database.</li>
 *     <li><b>Detached</b> - the object has an id and a database row, but it is no longer associated with a session
 *     (the session was closed, or the object was detached from it), so its changes are not tracked.</li>
 * </ul>
 */
public class DetachedAndPersistentStatesDemo {

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = SessionFactoryProvider.createSessionFactory()) {
            Course course = new Course("Lifecycle demo: detached and persistent");

            // PERSISTENT: the course is saved and managed by the first session
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.persist(course);
                transaction.commit();
                System.out.println("[PERSISTENT] id = " + course.getId()
                        + ", session.contains(course) = " + session.contains(course));
            }

            // PERSISTENT -> DETACHED: the session is closed, the object keeps its id, but nobody tracks it anymore.
            // This change will NOT be saved automatically.
            course.setName("Lifecycle demo: renamed while detached");

            try (Session session = sessionFactory.openSession()) {
                // The new session knows nothing about the detached object
                System.out.println("[DETACHED]   id = " + course.getId()
                        + ", session.contains(course) = " + session.contains(course));

                // get() returns another, persistent instance for the same row. It still has the old name,
                // because the change of the detached object was not written to the database.
                Course persistent = session.get(Course.class, course.getId());
                System.out.println("[PERSISTENT] stored name = " + persistent.getName()
                        + ", detached name = " + course.getName());

                // DETACHED -> PERSISTENT: merge() copies the state of the detached object onto the persistent
                // instance of this session and returns it. The dirty persistent instance is saved on commit.
                // The passed detached object itself stays detached.
                Transaction transaction = session.beginTransaction();
                Course merged = session.merge(course);
                transaction.commit();
                System.out.println("[PERSISTENT] merge() returned the persistent instance: " + (merged == persistent)
                        + ", session.contains(merged) = " + session.contains(merged)
                        + ", session.contains(course) = " + session.contains(course));

                // refresh() re-reads the persistent instance from the database: the merged name is really stored
                session.refresh(merged);
                System.out.println("[PERSISTENT] name after refresh() = " + merged.getName());

                // PERSISTENT -> DETACHED without closing the session: detach() removes the object from the session,
                // so, again, its further changes are not tracked
                session.detach(merged);
                System.out.println("[DETACHED]   after session.detach(merged): session.contains(merged) = "
                        + session.contains(merged));
            }
        }
    }
}
