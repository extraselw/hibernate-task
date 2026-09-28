package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.Grading;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Deleting a discipline also deletes its relations with courses and its grades (by the database cascade),
 * but not the related courses or students.
 */
@RequiredArgsConstructor
public class DisciplineRepository implements JpaRepository<Discipline, Long> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<Discipline> getEntityClass() {
        return Discipline.class;
    }

    @Override
    public void applyChanges(Discipline discipline, Discipline newValues) {
        discipline.setName(newValues.getName());
    }

    /**
     * @return disciplines taught in the course, ordered by id
     */
    public List<Discipline> findByCourseId(Long courseId) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("""
                        select dc.discipline from DisciplineCourse dc
                        where dc.course.id = :courseId
                        order by dc.discipline.id""", Discipline.class)
                .setParameter("courseId", courseId)
                .getResultList());
    }

    /**
     * @return disciplines taught in the course with the given grading, ordered by id
     */
    public List<Discipline> findByCourseIdAndGrading(Long courseId, Grading grading) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("""
                        select dc.discipline from DisciplineCourse dc
                        where dc.course.id = :courseId and dc.grading = :grading
                        order by dc.discipline.id""", Discipline.class)
                .setParameter("courseId", courseId)
                .setParameter("grading", grading)
                .getResultList());
    }
}
