package edu.epam.fop.web.demos.task3;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

import java.time.OffsetDateTime;

/**
 * Updates {@code end_at} of a course.
 * Arguments: {@code <course id> <end_at>}, e.g. {@code 2 2026-12-03T10:15:30+01:00}.
 */
public class UpdateCourseEndAtDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            OffsetDateTime endAt = OffsetDateTime.parse(args[1]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            System.out.println("Updated course: " + courseService.updateEndAt(courseId, endAt));
        });
    }
}
