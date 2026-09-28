package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import edu.epam.fop.web.service.CourseService;

/**
 * Prints all courses in which a discipline is taught. Arguments: {@code <discipline id>}.
 */
public class SearchCoursesOfDisciplineDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long disciplineId = Long.parseLong(args[0]);

            CourseService courseService = new CourseService(new CourseRepository(sessionFactory));
            DemoRunner.printAll(courseService.findByDiscipline(disciplineId),
                    "No courses found for discipline " + disciplineId);
        });
    }
}
