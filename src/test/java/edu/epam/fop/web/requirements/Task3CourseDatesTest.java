package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Course;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Task 3: start and end of courses, their validation, and the search for active and future courses.
 */
class Task3CourseDatesTest extends DatabaseTest {

    private static final OffsetDateTime JAN_2025 = OffsetDateTime.parse("2025-01-01T00:00:00+01:00");
    private static final OffsetDateTime JAN_2026 = OffsetDateTime.parse("2026-01-01T00:00:00+01:00");

    private CourseRepository courseRepository;
    private CourseService courseService;

    @BeforeEach
    void createServices() {
        courseRepository = new CourseRepository(sessionFactory);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void newCourseHasNoStartAndNoEnd() {
        Course stored = courseRepository.find(courseService.create("course-1").getId());

        assertNull(stored.getStartAt());
        assertNull(stored.getEndAt());
    }

    @Test
    void startAndEndAreUpdated() {
        long id = courseService.create("course-1").getId();

        courseService.updateStartAt(id, JAN_2025);
        courseService.updateEndAt(id, JAN_2026);

        Course stored = courseRepository.find(id);
        assertEquals(JAN_2025.toInstant(), stored.getStartAt().toInstant());
        assertEquals(JAN_2026.toInstant(), stored.getEndAt().toInstant());
    }

    @Test
    void endOfDraftCourseIsNotValidated() {
        long id = courseService.create("draft").getId();

        courseService.updateEndAt(id, JAN_2025);

        assertEquals(JAN_2025.toInstant(), courseRepository.find(id).getEndAt().toInstant());
    }

    @Test
    void endBeforeOrEqualToStartIsRejected() {
        long id = courseService.create("course-1").getId();
        courseService.updateStartAt(id, JAN_2026);

        assertThrows(IllegalArgumentException.class, () -> courseService.updateEndAt(id, JAN_2025));
        assertThrows(IllegalArgumentException.class, () -> courseService.updateEndAt(id, JAN_2026));
        assertNull(courseRepository.find(id).getEndAt());
    }

    @Test
    void startAfterExistingEndIsRejected() {
        long id = courseService.create("course-1").getId();
        courseService.updateStartAt(id, JAN_2025);
        courseService.updateEndAt(id, JAN_2026);

        assertThrows(IllegalArgumentException.class,
                () -> courseService.updateStartAt(id, JAN_2026.plusDays(1)));
        assertEquals(JAN_2025.toInstant(), courseRepository.find(id).getStartAt().toInstant());
    }

    @Test
    void activeCoursesHaveStartInThePastAndNoEndOrEndInTheFuture() {
        createCourses();

        assertEquals(Set.of("started, no deadline", "started, ends later"), names(courseService.findActive()));
    }

    @Test
    void futureCoursesHaveStartInTheFuture() {
        createCourses();

        assertEquals(Set.of("starts later", "starts later, has deadline"), names(courseService.findFuture()));
    }

    private void createCourses() {
        OffsetDateTime now = OffsetDateTime.now();
        createCourse("draft", null, null);
        createCourse("draft with deadline", null, now.plusDays(10));
        createCourse("started, no deadline", now.minusDays(10), null);
        createCourse("started, ends later", now.minusDays(10), now.plusDays(10));
        createCourse("finished", now.minusDays(20), now.minusDays(10));
        createCourse("starts later", now.plusDays(10), null);
        createCourse("starts later, has deadline", now.plusDays(10), now.plusDays(20));
    }

    private void createCourse(String name, OffsetDateTime startAt, OffsetDateTime endAt) {
        long id = courseService.create(name).getId();
        if (startAt != null) {
            courseService.updateStartAt(id, startAt);
        }
        if (endAt != null) {
            courseService.updateEndAt(id, endAt);
        }
    }

    private static Set<String> names(List<Course> courses) {
        return courses.stream().map(Course::getName).collect(Collectors.toSet());
    }
}
