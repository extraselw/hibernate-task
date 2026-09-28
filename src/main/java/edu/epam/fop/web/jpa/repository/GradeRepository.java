package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.Grade;
import edu.epam.fop.web.jpa.entity.GradeId;
import edu.epam.fop.web.jpa.entity.Student;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

@RequiredArgsConstructor
public class GradeRepository implements JpaRepository<Grade, GradeId> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<Grade> getEntityClass() {
        return Grade.class;
    }

    @Override
    public void applyChanges(Grade grade, Grade newValues) {
        grade.setValue(newValues.getValue());
    }

    /**
     * Grades an existing student in an existing discipline.
     *
     * @return the created grade
     * @throws EntityNotFoundException if either the student or the discipline does not exist
     * @throws EntityExistsException   if the student is already graded in the discipline
     */
    public Grade create(Long studentId, Long disciplineId, Integer value) {
        return sessionFactory.fromTransaction(session -> {
            GradeId id = new GradeId(studentId, disciplineId);
            if (session.find(Grade.class, id) != null) {
                throw new EntityExistsException(
                        "Student " + studentId + " is already graded in discipline " + disciplineId);
            }
            Grade grade = new Grade(
                    Sessions.findOrThrow(session, Student.class, studentId),
                    Sessions.findOrThrow(session, Discipline.class, disciplineId),
                    value);
            session.persist(grade);
            return grade;
        });
    }
}
