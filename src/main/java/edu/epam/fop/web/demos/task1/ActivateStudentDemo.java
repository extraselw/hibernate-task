package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Sets {@code active} of a student to {@code true}. Arguments: {@code <student id>}.
 */
public class ActivateStudentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            System.out.println("Activated student: " + studentService.activate(studentId));
        });
    }
}
