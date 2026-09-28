package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Department;
import edu.epam.fop.web.jpa.entity.Student;
import edu.epam.fop.web.jpa.repository.DepartmentRepository;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.DepartmentService;
import edu.epam.fop.web.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 2: departments are created and never changed; students are moved to and searched by departments.
 */
class Task2DepartmentTest extends DatabaseTest {

    private DepartmentRepository departmentRepository;
    private StudentRepository studentRepository;
    private DepartmentService departmentService;
    private StudentService studentService;

    @BeforeEach
    void createServices() {
        departmentRepository = new DepartmentRepository(sessionFactory);
        studentRepository = new StudentRepository(sessionFactory);
        departmentService = new DepartmentService(departmentRepository);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void departmentIsCreated() {
        Department created = departmentService.create("department-1");

        assertEquals("department-1", departmentRepository.find(created.getId()).getName());
    }

    @Test
    void departmentCannotBeUpdatedOrDeleted() {
        Department created = departmentService.create("department-1");

        assertThrows(UnsupportedOperationException.class,
                () -> departmentRepository.update(created.getId(), new Department("renamed")));
        assertThrows(UnsupportedOperationException.class, () -> departmentRepository.delete(created.getId()));
        assertEquals("department-1", departmentRepository.find(created.getId()).getName());
    }

    @Test
    void newStudentHasNoDepartment() {
        Student created = studentService.create("student-1");

        assertNull(studentRepository.find(created.getId()).getDepartment());
    }

    @Test
    void studentIsMovedToDepartment() {
        Student student = studentService.create("student-1");
        Department department = departmentService.create("department-1");

        studentService.moveToDepartment(student.getId(), department.getId());

        assertEquals(department.getId(), studentRepository.find(student.getId()).getDepartment().getId());
    }

    @Test
    void searchByDepartmentReturnsOnlyStudentsOfThatDepartment() {
        Department first = departmentService.create("department-1");
        Department second = departmentService.create("department-2");
        Department empty = departmentService.create("department-3");
        moveToDepartment(studentService.create("student-1"), first);
        moveToDepartment(studentService.create("student-2"), second);
        moveToDepartment(studentService.create("student-3"), second);
        studentService.create("student-4");

        assertEquals(List.of("student-1"), studentNames(first));
        assertEquals(List.of("student-2", "student-3"), studentNames(second));
        assertTrue(studentNames(empty).isEmpty());
    }

    @Test
    void movingToUnknownDepartmentFailsAndKeepsStudentUnchanged() {
        Student student = studentService.create("student-1");

        assertThrows(EntityNotFoundException.class, () -> studentService.moveToDepartment(student.getId(), 42));
        assertNull(studentRepository.find(student.getId()).getDepartment());
    }

    @Test
    void movingUnknownStudentFails() {
        Department department = departmentService.create("department-1");

        assertThrows(EntityNotFoundException.class, () -> studentService.moveToDepartment(42, department.getId()));
    }

    private void moveToDepartment(Student student, Department department) {
        studentService.moveToDepartment(student.getId(), department.getId());
    }

    private List<String> studentNames(Department department) {
        return studentService.findByDepartment(department.getId()).stream().map(Student::getName).toList();
    }
}
