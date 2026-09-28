package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.DisciplineService;

/**
 * Creates a new discipline. Arguments: {@code <discipline name>}.
 */
public class CreateDisciplineDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            String name = args[0];

            DisciplineService disciplineService = new DisciplineService(new DisciplineRepository(sessionFactory));
            System.out.println("Created discipline: " + disciplineService.create(name));
        });
    }
}
