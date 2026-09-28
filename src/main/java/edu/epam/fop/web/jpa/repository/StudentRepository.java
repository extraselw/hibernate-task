package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Department;
import edu.epam.fop.web.jpa.entity.Student;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.util.List;

@RequiredArgsConstructor
public class StudentRepository implements JpaRepository<Student, Long> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<Student> getEntityClass() {
        return Student.class;
    }

    /**
     * The department is not copied, it is changed only by {@link #moveToDepartment(Long, Long)}.
     */
    @Override
    public void applyChanges(Student student, Student newValues) {
        student.setName(newValues.getName());
        student.setActive(newValues.isActive());
    }

    /**
     * @return students of the department ordered by id
     */
    public List<Student> findByDepartmentId(Long departmentId) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("from Student s where s.department.id = :departmentId order by s.id",
                        Student.class)
                .setParameter("departmentId", departmentId)
                .getResultList());
    }

    /**
     * Assigns the student to the department.
     *
     * @return the updated student
     * @throws EntityNotFoundException if either the student or the department does not exist
     */
    public Student moveToDepartment(Long studentId, Long departmentId) {
        return sessionFactory.fromTransaction(session -> {
            Student student = Sessions.findOrThrow(session, Student.class, studentId);
            student.setDepartment(Sessions.findOrThrow(session, Department.class, departmentId));
            return student;
        });
    }

    /**
     * Loads the student together with the grades, so that the grades and the average grade
     * are available outside of the session.
     *
     * @return the student, or {@code null} if there is no student with such id
     */
    public Student findWithGrades(Long studentId) {
        return sessionFactory.fromTransaction(session -> session
                .createSelectionQuery("from Student s left join fetch s.grades where s.id = :studentId", Student.class)
                .setParameter("studentId", studentId)
                .uniqueResult());
    }
}
