package edu.epam.fop.web.demos.task4;

import edu.epam.fop.web.demos.DemoRunner;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.service.DisciplineService;

/**
 * Deletes a discipline, the courses it is taught in are kept. Arguments: {@code <discipline id>}.
 */
public class DeleteDisciplineDemo {

    public static void main(String[] args) {
        DemoRunner.run(sessionFactory -> {
            long disciplineId = Long.parseLong(args[0]);

            DisciplineService disciplineService = new DisciplineService(new DisciplineRepository(sessionFactory));
            disciplineService.delete(disciplineId);
            System.out.println("Deleted discipline with id " + disciplineId);
        });
    }
}
