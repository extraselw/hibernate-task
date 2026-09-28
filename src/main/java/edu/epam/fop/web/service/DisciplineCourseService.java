package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.DisciplineCourse;
import edu.epam.fop.web.jpa.entity.DisciplineCourseId;
import edu.epam.fop.web.jpa.entity.Grading;
import edu.epam.fop.web.jpa.repository.DisciplineCourseRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Operations on the relation between courses and disciplines.
 */
@RequiredArgsConstructor
public class DisciplineCourseService {

    private final DisciplineCourseRepository disciplineCourseRepository;

    /**
     * Relates the course and the discipline. The grading of a new relation is not defined.
     *
     * @return the created relation
     * @throws EntityNotFoundException if either the course or the discipline does not exist
     * @throws EntityExistsException   if the course and the discipline are already related
     */
    public DisciplineCourse link(long courseId, long disciplineId) {
        return disciplineCourseRepository.create(courseId, disciplineId);
    }

    /**
     * Removes the relation, the course and the discipline themselves are kept.
     *
     * @throws EntityNotFoundException if the course and the discipline are not related
     */
    public void unlink(long courseId, long disciplineId) {
        disciplineCourseRepository.delete(new DisciplineCourseId(disciplineId, courseId));
    }

    /**
     * @return the updated relation
     * @throws EntityNotFoundException if the course and the discipline are not related
     */
    public DisciplineCourse updateGrading(long courseId, long disciplineId, Grading grading) {
        DisciplineCourseId id = new DisciplineCourseId(disciplineId, courseId);
        DisciplineCourse relation = disciplineCourseRepository.find(id);
        if (relation == null) {
            throw new EntityNotFoundException(
                    "Course " + courseId + " is not linked with discipline " + disciplineId);
        }
        relation.setGrading(grading);
        return disciplineCourseRepository.update(id, relation);
    }
}
