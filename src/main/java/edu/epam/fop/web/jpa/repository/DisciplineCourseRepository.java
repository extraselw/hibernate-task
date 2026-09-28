package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Course;
import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.DisciplineCourse;
import edu.epam.fop.web.jpa.entity.DisciplineCourseId;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

@RequiredArgsConstructor
public class DisciplineCourseRepository implements JpaRepository<DisciplineCourse, DisciplineCourseId> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<DisciplineCourse> getEntityClass() {
        return DisciplineCourse.class;
    }

    @Override
    public void applyChanges(DisciplineCourse relation, DisciplineCourse newValues) {
        relation.setGrading(newValues.getGrading());
    }

    /**
     * Creates a relation without grading between an existing course and an existing discipline.
     *
     * @return the created relation
     * @throws EntityNotFoundException if either the course or the discipline does not exist
     * @throws EntityExistsException   if the course and the discipline are already related
     */
    public DisciplineCourse create(Long courseId, Long disciplineId) {
        return sessionFactory.fromTransaction(session -> {
            DisciplineCourseId id = new DisciplineCourseId(disciplineId, courseId);
            if (session.find(DisciplineCourse.class, id) != null) {
                throw new EntityExistsException(
                        "Course " + courseId + " is already linked with discipline " + disciplineId);
            }
            DisciplineCourse relation = new DisciplineCourse(
                    Sessions.findOrThrow(session, Discipline.class, disciplineId),
                    Sessions.findOrThrow(session, Course.class, courseId));
            session.persist(relation);
            return relation;
        });
    }
}
