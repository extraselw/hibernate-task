package edu.epam.fop.web.jpa.repository;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Generic CRUD operations built on Hibernate {@link SessionFactory} and {@link org.hibernate.Session}.
 * <p>
 * Every operation opens its own session and runs in its own transaction, which is committed on success
 * and rolled back on failure. Therefore, all returned entities are detached: their lazy associations
 * are available only if they were explicitly fetched.
 *
 * @param <Entity> - type of JPA entity
 * @param <Id>     - type of primary key
 */
public interface JpaRepository<Entity, Id> {

    SessionFactory getSessionFactory();

    Class<Entity> getEntityClass();

    /**
     * Persists a new entity. A generated id is assigned to the passed entity.
     *
     * @return id of the persisted entity
     */
    default Id save(Entity entity) {
        return getSessionFactory().fromTransaction(session -> {
            session.persist(entity);
            @SuppressWarnings("unchecked") // the identifier of Entity is always of type Id
            Id id = (Id) session.getIdentifier(entity);
            return id;
        });
    }

    /**
     * @return entity with the given id, or {@code null} if there is no such entity
     */
    default Entity find(Id id) {
        return getSessionFactory().fromTransaction(session -> session.find(getEntityClass(), id));
    }

    /**
     * @return all entities ordered by id
     */
    default List<Entity> findAll() {
        return getSessionFactory().fromTransaction(session -> {
            String entityName = session.getMetamodel().entity(getEntityClass()).getName();
            return session
                    .createSelectionQuery("from " + entityName + " e order by id(e)", getEntityClass())
                    .getResultList();
        });
    }

    /**
     * Loads the entity and applies the new values to it with {@link #applyChanges(Object, Object)}.
     * The changes are written to the database by dirty checking when the transaction is committed.
     *
     * @return the updated entity
     * @throws EntityNotFoundException if there is no entity with the given id
     */
    default Entity update(Id id, Entity newValues) {
        return getSessionFactory().fromTransaction(session -> {
            Entity entity = Sessions.findOrThrow(session, getEntityClass(), id);
            applyChanges(entity, newValues);
            return entity;
        });
    }

    /**
     * @throws EntityNotFoundException if there is no entity with the given id
     */
    default void delete(Id id) {
        getSessionFactory().inTransaction(session ->
                session.remove(Sessions.findOrThrow(session, getEntityClass(), id)));
    }

    /**
     * Copies the updatable state of {@code newValues} to the persistent {@code entity}.
     * Repositories of updatable entities override this method.
     */
    default void applyChanges(Entity entity, Entity newValues) {
        throw new UnsupportedOperationException(getEntityClass().getSimpleName() + " cannot be updated");
    }
}
