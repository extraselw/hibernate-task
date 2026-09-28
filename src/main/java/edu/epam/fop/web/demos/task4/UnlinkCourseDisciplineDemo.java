package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import edu.epam.fop.web.service.DisciplineCourseService;

/**
 * Deletes the relation between a course and a discipline, both of them are kept.
 * Arguments: {@code <course id> <discipline id>}.
 */
public class UnlinkCourseDisciplineDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            long disciplineId = Long.parseLong(args[1]);

            DisciplineCourseService disciplineCourseService =
                    new DisciplineCourseService(new DisciplineCourseRepository(sessionFactory));
            disciplineCourseService.unlink(courseId, disciplineId);
            System.out.println("Deleted relation between course " + courseId + " and discipline " + disciplineId);
        });
    }
}
