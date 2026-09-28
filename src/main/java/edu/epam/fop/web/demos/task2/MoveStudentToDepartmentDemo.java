package edu.epam.fop.web.demos.task2;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Moves a student to a department. Arguments: {@code <student id> <department id>}.
 */
public class MoveStudentToDepartmentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long studentId = Long.parseLong(args[0]);
            long departmentId = Long.parseLong(args[1]);

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            System.out.println("Moved student: " + studentService.moveToDepartment(studentId, departmentId));
        });
    }
}
