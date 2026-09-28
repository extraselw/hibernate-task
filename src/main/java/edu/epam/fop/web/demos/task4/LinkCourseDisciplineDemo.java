package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import edu.epam.fop.web.service.DisciplineCourseService;

/**
 * Creates a relation between a course and a discipline. Arguments: {@code <course id> <discipline id>}.
 */
public class LinkCourseDisciplineDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long courseId = Long.parseLong(args[0]);
            long disciplineId = Long.parseLong(args[1]);

            DisciplineCourseService disciplineCourseService =
                    new DisciplineCourseService(new DisciplineCourseRepository(sessionFactory));
            System.out.println("Created relation: " + disciplineCourseService.link(courseId, disciplineId));
        });
    }
}
