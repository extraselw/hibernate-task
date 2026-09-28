package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Creates a new inactive student. Arguments: {@code <student name>}.
 */
public class CreateStudentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            String name = args[0];

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            System.out.println("Created student: " + studentService.create(name));
        });
    }
}
