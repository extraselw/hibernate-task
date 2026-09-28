package edu.epam.fop.web.demos.task5;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.entity.Grading;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.DisciplineService;

/**
 * Prints all disciplines of a course with the given grading.
 * Arguments: {@code <course id> <grading>}, e.g. {@code 1 TEST}.
 */
public class SearchDisciplineByCourseAndGradingDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            Grading grading = Grading.parse(args[1]);

            DisciplineService disciplineService = new DisciplineService(new DisciplineRepository(sessionFactory));
            DemoRunner.printAll(disciplineService.findByCourseAndGrading(courseId, grading),
                    "No disciplines found for course " + courseId + " with grading " + grading);
        });
    }
}
