package edu.epam.fop.web.demos.task3;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

import java.time.OffsetDateTime;

/**
 * Updates {@code start_at} of a course.
 * Arguments: {@code <course id> <start_at>}, e.g. {@code 1 2024-12-03T10:15:30+01:00}.
 */
public class UpdateCourseStartAtDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            OffsetDateTime startAt = OffsetDateTime.parse(args[1]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            System.out.println("Updated course: " + courseService.updateStartAt(courseId, startAt));
        });
    }
}
