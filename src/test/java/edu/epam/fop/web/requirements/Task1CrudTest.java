package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Course;
import edu.epam.fop.web.jpa.entity.Student;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.CourseService;
import edu.epam.fop.web.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 1: CRUD operations of the repositories; new students and courses are inactive.
 */
class Task1CrudTest extends DatabaseTest {

    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private StudentService studentService;
    private CourseService courseService;

    @BeforeEach
    void createServices() {
        studentRepository = new StudentRepository(sessionFactory);
        courseRepository = new CourseRepository(sessionFactory);
        studentService = new StudentService(studentRepository);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void studentRepositoryCreatesReadsUpdatesAndDeletesStudents() {
        // Create
        Long id = studentRepository.save(new Student("student-1"));
        Long otherId = studentRepository.save(new Student("student-2"));
        assertNotNull(id);

        // Read one and all
        assertEquals("student-1", studentRepository.find(id).getName());
        assertEquals(List.of("student-1", "student-2"),
                studentRepository.findAll().stream().map(Student::getName).toList());

        // Update
        Student newValues = studentRepository.find(id);
        newValues.setName("renamed");
        studentRepository.update(id, newValues);
        assertEquals("renamed", studentRepository.find(id).getName());

        // Delete
        studentRepository.delete(id);
        assertNull(studentRepository.find(id));
        assertNotNull(studentRepository.find(otherId));
    }

    @Test
    void courseRepositoryCreatesReadsUpdatesAndDeletesCourses() {
        Long id = courseRepository.save(new Course("course-1"));
        assertEquals("course-1", courseRepository.find(id).getName());
        assertEquals(1, courseRepository.findAll().size());

        Course newValues = courseRepository.find(id);
        newValues.setName("renamed");
        courseRepository.update(id, newValues);
        assertEquals("renamed", courseRepository.find(id).getName());

        courseRepository.delete(id);
        assertNull(courseRepository.find(id));
    }

    @Test
    void newStudentIsInactive() {
        Student created = studentService.create("student-1");

        assertFalse(studentRepository.find(created.getId()).isActive());
    }

    @Test
    void newCourseIsInactive() {
        Course created = courseService.create("course-1");

        assertFalse(courseRepository.find(created.getId()).isActive());
    }

    @Test
    void activatingStudentSetsActiveToTrue() {
        Student created = studentService.create("student-1");

        studentService.activate(created.getId());

        assertTrue(studentRepository.find(created.getId()).isActive());
    }

    @Test
    void activatingCourseSetsActiveToTrue() {
        Course created = courseService.create("course-1");

        courseService.activate(created.getId());

        assertTrue(courseRepository.find(created.getId()).isActive());
    }

    @Test
    void findingUnknownStudentOrCourseReturnsNothing() {
        assertTrue(studentService.findById(42).isEmpty());
        assertTrue(courseService.findById(42).isEmpty());
    }

    @Test
    void changingUnknownStudentOrCourseFails() {
        assertThrows(EntityNotFoundException.class, () -> studentService.activate(42));
        assertThrows(EntityNotFoundException.class, () -> studentService.delete(42));
        assertThrows(EntityNotFoundException.class, () -> courseService.activate(42));
        assertThrows(EntityNotFoundException.class, () -> courseService.delete(42));
    }
}
