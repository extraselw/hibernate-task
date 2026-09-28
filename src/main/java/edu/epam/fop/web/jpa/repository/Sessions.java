package edu.epam.fop.web.jpa.repository;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

/**
 * Session helpers shared by the repositories.
 */
final class Sessions {

    private Sessions() {
    }

    /**
     * Finds an entity by its id within the given session.
     *
     * @throws EntityNotFoundException if there is no entity with such id
     */
    static <T> T findOrThrow(Session session, Class<T> entityClass, Object id) {
        T entity = session.find(entityClass, id);
        if (entity == null) {
            throw new EntityNotFoundException(entityClass.getSimpleName() + " with id " + id + " not found");
        }
        return entity;
    }
}
