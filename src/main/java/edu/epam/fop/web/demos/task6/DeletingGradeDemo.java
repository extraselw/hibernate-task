package edu.epam.fop.web.demos.task6;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.GradeRepository;
import edu.epam.fop.web.service.GradeService;

/**
 * Deletes the grade of a student in a discipline. Arguments: {@code <student id> <discipline id>}.
 */
public class DeletingGradeDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);
            long disciplineId = Long.parseLong(args[1]);

            GradeService gradeService = new GradeService(new GradeRepository(sessionFactory));
            gradeService.delete(studentId, disciplineId);
            System.out.println("Deleted grade of student " + studentId + " in discipline " + disciplineId);
        });
    }
}
