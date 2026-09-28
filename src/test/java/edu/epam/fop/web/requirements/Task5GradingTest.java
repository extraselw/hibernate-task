package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.DisciplineCourseId;
import edu.epam.fop.web.jpa.entity.Grading;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.CourseService;
import edu.epam.fop.web.service.DisciplineCourseService;
import edu.epam.fop.web.service.DisciplineService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 5: grading of course-discipline relations and the search of disciplines by course and grading.
 */
class Task5GradingTest extends DatabaseTest {

    private DisciplineCourseRepository disciplineCourseRepository;
    private DisciplineCourseService disciplineCourseService;
    private DisciplineService disciplineService;
    private CourseService courseService;

    @BeforeEach
    void createServices() {
        disciplineCourseRepository = new DisciplineCourseRepository(sessionFactory);
        disciplineCourseService = new DisciplineCourseService(disciplineCourseRepository);
        disciplineService = new DisciplineService(new DisciplineRepository(sessionFactory));
        courseService = new CourseService(new CourseRepository(sessionFactory));
    }

    @Test
    void newRelationHasNoGrading() {
        long course = courseService.create("course-1").getId();
        long discipline = disciplineService.create("discipline-1").getId();

        assertNull(disciplineCourseService.link(course, discipline).getGrading());
    }

    @Test
    void gradingOfRelationIsUpdated() {
        long course = courseService.create("course-1").getId();
        long discipline = disciplineService.create("discipline-1").getId();
        disciplineCourseService.link(course, discipline);

        disciplineCourseService.updateGrading(course, discipline, Grading.EXAM);

        assertEquals(Grading.EXAM,
                disciplineCourseRepository.find(new DisciplineCourseId(discipline, course)).getGrading());
        assertEquals(List.of("EXAM"), selectStrings("select grading from discipline_course"));
    }

    @Test
    void disciplinesAreSearchedByCourseAndGrading() {
        long course1 = courseService.create("course-1").getId();
        long course2 = courseService.create("course-2").getId();
        long discipline1 = disciplineService.create("discipline-1").getId();
        long discipline2 = disciplineService.create("discipline-2").getId();
        long discipline3 = disciplineService.create("discipline-3").getId();
        linkWithGrading(course1, discipline1, Grading.TEST);
        linkWithGrading(course1, discipline2, Grading.EXAM);
        linkWithGrading(course1, discipline3, Grading.TEST);
        linkWithGrading(course2, discipline1, Grading.EXAM);

        assertEquals(List.of("discipline-1", "discipline-3"), disciplineNames(course1, Grading.TEST));
        assertEquals(List.of("discipline-2"), disciplineNames(course1, Grading.EXAM));
        assertTrue(disciplineNames(course1, Grading.REPORT).isEmpty());
        assertEquals(List.of("discipline-1"), disciplineNames(course2, Grading.EXAM));
    }

    @Test
    void updatingGradingOfMissingRelationFails() {
        long course = courseService.create("course-1").getId();
        long discipline = disciplineService.create("discipline-1").getId();

        assertThrows(EntityNotFoundException.class,
                () -> disciplineCourseService.updateGrading(course, discipline, Grading.TEST));
    }

    @Test
    void gradingIsParsedIgnoringCaseAndUnknownGradingIsRejected() {
        assertEquals(Grading.REPORT, Grading.parse("report"));
        assertThrows(IllegalArgumentException.class, () -> Grading.parse("NONE"));
    }

    private void linkWithGrading(long courseId, long disciplineId, Grading grading) {
        disciplineCourseService.link(courseId, disciplineId);
        disciplineCourseService.updateGrading(courseId, disciplineId, grading);
    }

    private List<String> disciplineNames(long courseId, Grading grading) {
        return disciplineService.findByCourseAndGrading(courseId, grading).stream().map(Discipline::getName).toList();
    }
}
