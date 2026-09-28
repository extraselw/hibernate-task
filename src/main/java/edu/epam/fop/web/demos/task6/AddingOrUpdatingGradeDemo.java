package edu.epam.fop.web.demos.task6;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.GradeRepository;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.GradeService;
import edu.epam.fop.web.service.StudentService;

/**
 * Grades a student in a discipline, or updates the existing grade, and prints the student's average grade.
 * Arguments: {@code <student id> <discipline id> <value>}, the value must be in [0, 100].
 */
public class AddingOrUpdatingGradeDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);
            long disciplineId = Long.parseLong(args[1]);
            int value = Integer.parseInt(args[2]);

            GradeService gradeService = new GradeService(new GradeRepository(sessionFactory));
            System.out.println("Saved grade: " + gradeService.addOrUpdate(studentId, disciplineId, value));

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            studentService.findWithGrades(studentId).ifPresent(student -> System.out.println(
                    "Average grade of " + student.getName() + ": " + student.getAverageGrade()));
        });
    }
}
