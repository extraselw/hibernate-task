package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Course;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CourseRepository implements JpaRepository<Course, Long> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<Course> getEntityClass() {
        return Course.class;
    }

    @Override
    public void applyChanges(Course course, Course newValues) {
        course.setName(newValues.getName());
        course.setActive(newValues.isActive());
        course.setStartAt(newValues.getStartAt());
        course.setEndAt(newValues.getEndAt());
    }

    /**
     * @return courses that started at or before the moment and either have no deadline or end after it
     */
    public List<Course> findActive(OffsetDateTime moment) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("from Course c"
                        + " where c.startAt <= :moment and (c.endAt is null or c.endAt > :moment)"
                        + " order by c.startAt, c.id", Course.class)
                .setParameter("moment", moment)
                .getResultList());
    }

    /**
     * @return courses that start after the moment
     */
    public List<Course> findFuture(OffsetDateTime moment) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("from Course c where c.startAt > :moment order by c.startAt, c.id", Course.class)
                .setParameter("moment", moment)
                .getResultList());
    }

    /**
     * @return courses in which the discipline is taught, ordered by id
     */
    public List<Course> findByDisciplineId(Long disciplineId) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("select dc.course from DisciplineCourse dc"
                        + " where dc.discipline.id = :disciplineId"
                        + " order by dc.course.id", Course.class)
                .setParameter("disciplineId", disciplineId)
                .getResultList());
    }
}
