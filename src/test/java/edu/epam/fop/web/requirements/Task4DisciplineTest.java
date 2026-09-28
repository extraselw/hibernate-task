package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Course;
import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.CourseService;
import edu.epam.fop.web.service.DisciplineCourseService;
import edu.epam.fop.web.service.DisciplineService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 4: disciplines and their relations with courses. Deleting one side never deletes the other side.
 */
class Task4DisciplineTest extends DatabaseTest {

    private DisciplineRepository disciplineRepository;
    private CourseRepository courseRepository;
    private DisciplineService disciplineService;
    private CourseService courseService;
    private DisciplineCourseService disciplineCourseService;

    private long course1;
    private long course2;
    private long discipline1;
    private long discipline2;

    @BeforeEach
    void createServicesAndData() {
        disciplineRepository = new DisciplineRepository(sessionFactory);
        courseRepository = new CourseRepository(sessionFactory);
        disciplineService = new DisciplineService(disciplineRepository);
        courseService = new CourseService(courseRepository);
        disciplineCourseService = new DisciplineCourseService(new DisciplineCourseRepository(sessionFactory));

        course1 = courseService.create("course-1").getId();
        course2 = courseService.create("course-2").getId();
        discipline1 = disciplineService.create("discipline-1").getId();
        discipline2 = disciplineService.create("discipline-2").getId();
    }

    @Test
    void disciplineIsCreatedAndDeleted() {
        assertEquals("discipline-1", disciplineRepository.find(discipline1).getName());

        disciplineService.delete(discipline1);

        assertNull(disciplineRepository.find(discipline1));
    }

    @Test
    void linkedCourseAndDisciplineAreFoundFromBothSides() {
        disciplineCourseService.link(course1, discipline1);
        disciplineCourseService.link(course1, discipline2);
        disciplineCourseService.link(course2, discipline2);

        assertEquals(List.of("discipline-1", "discipline-2"), disciplineNames(course1));
        assertEquals(List.of("discipline-2"), disciplineNames(course2));
        assertEquals(List.of("course-1"), courseNames(discipline1));
        assertEquals(List.of("course-1", "course-2"), courseNames(discipline2));
    }

    @Test
    void unlinkingDeletesOnlyTheRelation() {
        disciplineCourseService.link(course1, discipline1);
        disciplineCourseService.link(course1, discipline2);

        disciplineCourseService.unlink(course1, discipline2);

        assertEquals(List.of("discipline-1"), disciplineNames(course1));
        assertNotNull(courseRepository.find(course1));
        assertNotNull(disciplineRepository.find(discipline2));
    }

    @Test
    void deletingDisciplineKeepsItsCourses() {
        disciplineCourseService.link(course1, discipline1);
        disciplineCourseService.link(course2, discipline1);

        disciplineService.delete(discipline1);

        assertNotNull(courseRepository.find(course1));
        assertNotNull(courseRepository.find(course2));
        assertTrue(disciplineNames(course1).isEmpty());
    }

    @Test
    void deletingCourseKeepsItsDisciplines() {
        disciplineCourseService.link(course1, discipline1);
        disciplineCourseService.link(course1, discipline2);

        courseService.delete(course1);

        assertNotNull(disciplineRepository.find(discipline1));
        assertNotNull(disciplineRepository.find(discipline2));
        assertTrue(courseNames(discipline1).isEmpty());
    }

    @Test
    void linkingTwiceFails() {
        disciplineCourseService.link(course1, discipline1);

        assertThrows(EntityExistsException.class, () -> disciplineCourseService.link(course1, discipline1));
    }

    @Test
    void linkingUnknownCourseOrDisciplineFails() {
        assertThrows(EntityNotFoundException.class, () -> disciplineCourseService.link(42, discipline1));
        assertThrows(EntityNotFoundException.class, () -> disciplineCourseService.link(course1, 42));
    }

    @Test
    void unlinkingNotLinkedCourseAndDisciplineFails() {
        assertThrows(EntityNotFoundException.class, () -> disciplineCourseService.unlink(course1, discipline1));
    }

    private List<String> disciplineNames(long courseId) {
        return disciplineService.findByCourse(courseId).stream().map(Discipline::getName).toList();
    }

    private List<String> courseNames(long disciplineId) {
        return courseService.findByDiscipline(disciplineId).stream().map(Course::getName).toList();
    }
}
