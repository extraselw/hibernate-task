package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Sets {@code active} of a course to {@code true}. Arguments: {@code <course id>}.
 */
public class ActivateCourseDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            System.out.println("Activated course: " + courseService.activate(courseId));
        });
    }
}
