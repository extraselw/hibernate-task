package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Creates a new inactive course. Arguments: {@code <course name>}.
 */
public class CreateCourseDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            String name = args[0];

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            System.out.println("Created course: " + courseService.create(name));
        });
    }
}
