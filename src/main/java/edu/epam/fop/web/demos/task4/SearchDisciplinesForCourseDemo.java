package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.DisciplineService;

/**
 * Prints all disciplines taught in a course. Arguments: {@code <course id>}.
 */
public class SearchDisciplinesForCourseDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);

            DisciplineService disciplineService = new DisciplineService(new DisciplineRepository(sessionFactory));
            DemoRunner.printAll(disciplineService.findByCourse(courseId),
                    "No disciplines found for course " + courseId);
        });
    }
}
