package edu.epam.fop.web.demos.task1;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Deletes a student. Arguments: {@code <student id>}.
 */
public class DeleteStudentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            studentService.delete(studentId);
            System.out.println("Deleted student with id " + studentId);
        });
    }
}
