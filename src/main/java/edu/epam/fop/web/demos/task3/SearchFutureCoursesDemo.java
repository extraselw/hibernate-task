package edu.epam.fop.web.demos.task3;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Prints all courses that start in the future.
 */
public class SearchFutureCoursesDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            DemoRunner.printAll(courseService.findFuture(), "No future courses found");
        });
    }
}
