package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Deletes a course. Arguments: {@code <course id>}.
 */
public class DeleteCourseDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            courseService.delete(courseId);
            System.out.println("Deleted course with id " + courseId);
        });
    }
}
