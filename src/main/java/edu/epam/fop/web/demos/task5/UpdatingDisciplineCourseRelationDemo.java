package edu.epam.fop.web.demos.task5;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.entity.Grading;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import edu.epam.fop.web.service.DisciplineCourseService;

/**
 * Updates the grading of an existing course-discipline relation.
 * Arguments: {@code <course id> <discipline id> <grading>}, e.g. {@code 1 1 TEST}.
 */
public class UpdatingDisciplineCourseRelationDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            long disciplineId = Long.parseLong(args[1]);
            Grading grading = Grading.parse(args[2]);

            DisciplineCourseService disciplineCourseService =
                    new DisciplineCourseService(new DisciplineCourseRepository(sessionFactory));
            System.out.println("Updated relation: "
                    + disciplineCourseService.updateGrading(courseId, disciplineId, grading));
        });
    }
}
