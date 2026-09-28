package edu.epam.fop.web.demos.task2;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DepartmentRepository;
import edu.epam.fop.web.service.DepartmentService;

/**
 * Creates a new department. Arguments: {@code <department name>}.
 */
public class CreateDepartmentDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            String name = args[0];

            DepartmentService departmentService = new DepartmentService(new DepartmentRepository(sessionFactory));
            System.out.println("Created department: " + departmentService.create(name));
        });
    }
}
