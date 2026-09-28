package edu.epam.fop.web.demos.task7;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import edu.epam.fop.web.jpa.entity.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Shows the TRANSIENT and PERSISTENT states of an entity.
 * <ul>
 *     <li><b>Transient</b> - an object just created with {@code new}: it has no id, no database row,
 *     and no session knows about it.</li>
 *     <li><b>Persistent</b> (managed) - an object associated with an open session: it has an id and a row,
 *     and the session keeps it in sync with the database automatically.</li>
 * </ul>
 */
public class TransientAndPersistentStatesDemo {

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = SessionFactoryProvider.createSessionFactory();
             Session session = sessionFactory.openSession()) {

            // TRANSIENT: a plain Java object, Hibernate knows nothing about it yet
            Course course = new Course("Lifecycle demo: transient and persistent");
            System.out.println("[TRANSIENT]  id = " + course.getId()
                    + ", session.contains(course) = " + session.contains(course));

            Transaction transaction = session.beginTransaction();

            // TRANSIENT -> PERSISTENT: persist() puts the object into the session's persistence context.
            // The id comes from an IDENTITY column, so Hibernate executes the INSERT immediately to obtain it.
            session.persist(course);
            System.out.println("[PERSISTENT] id = " + course.getId()
                    + ", session.contains(course) = " + session.contains(course));

            // The session tracks a persistent object, so no explicit "update" call is needed:
            // dirty checking detects the change and executes an UPDATE when the transaction is committed.
            course.setName("Lifecycle demo: renamed while persistent");
            transaction.commit();

            // A change that is made but never committed...
            course.setName("Lifecycle demo: unsaved name");
            System.out.println("[PERSISTENT] name in memory = " + course.getName());

            // ...is discarded by refresh(), which re-reads the state of a persistent object from the database.
            // The re-read name is the one set before the commit, which proves that dirty checking saved it.
            session.refresh(course);
            System.out.println("[PERSISTENT] name after refresh() = " + course.getName());
        }
    }
}
