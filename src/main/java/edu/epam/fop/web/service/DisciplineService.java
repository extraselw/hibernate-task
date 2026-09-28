package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.Grading;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DisciplineService {

    private final DisciplineRepository disciplineRepository;

    /**
     * @return the created discipline with the generated id
     */
    public Discipline create(String name) {
        Discipline discipline = new Discipline(name);
        disciplineRepository.save(discipline);
        return discipline;
    }

    /**
     * Deletes the discipline, its relations with courses and its grades. Courses and students are kept.
     *
     * @throws EntityNotFoundException if there is no discipline with such id
     */
    public void delete(long id) {
        disciplineRepository.delete(id);
    }

    public List<Discipline> findByCourse(long courseId) {
        return disciplineRepository.findByCourseId(courseId);
    }

    public List<Discipline> findByCourseAndGrading(long courseId, Grading grading) {
        return disciplineRepository.findByCourseIdAndGrading(courseId, grading);
    }
}
