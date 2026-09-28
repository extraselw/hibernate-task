package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Prints a student. Arguments: {@code <student id>}.
 */
public class FindStudentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            studentService.findById(studentId).ifPresentOrElse(
                    student -> System.out.println("Found student: " + student),
                    () -> System.out.println("Student with id " + studentId + " not found"));
        });
    }
}
