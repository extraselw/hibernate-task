package edu.epam.fop.web.demos.task2;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.StudentService;

/**
 * Prints all students of a department. Arguments: {@code <department id>}.
 */
public class SearchStudentsByDepartmentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long departmentId = Long.parseLong(args[0]);

            StudentService studentService = new StudentService(new StudentRepository(sessionFactory));
            DemoRunner.printAll(studentService.findByDepartment(departmentId),
                    "No students found in department " + departmentId);
        });
    }
}
