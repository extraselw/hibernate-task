package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Prints a course. Arguments: {@code <course id>}.
 */
public class FindCourseDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            courseService.findById(courseId).ifPresentOrElse(
                    course -> System.out.println("Found course: " + course),
                    () -> System.out.println("Course with id " + courseId + " not found"));
        });
    }
}
