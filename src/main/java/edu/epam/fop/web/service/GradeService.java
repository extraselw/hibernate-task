package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.Grade;
import edu.epam.fop.web.jpa.entity.GradeId;
import edu.epam.fop.web.jpa.repository.GradeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

/**
 * Operations on grades of students in disciplines.
 * Grade values are validated by Hibernate against the {@code @Min}/{@code @Max} constraints of {@link Grade}.
 */
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    /**
     * @return the created grade
     * @throws EntityNotFoundException      if either the student or the discipline does not exist
     * @throws EntityExistsException        if the student is already graded in the discipline
     * @throws ConstraintViolationException if the value is out of the allowed range
     */
    public Grade add(long studentId, long disciplineId, int value) {
        return gradeRepository.create(studentId, disciplineId, value);
    }

    /**
     * @return the updated grade
     * @throws EntityNotFoundException      if the student is not graded in the discipline
     * @throws ConstraintViolationException if the value is out of the allowed range
     */
    public Grade update(long studentId, long disciplineId, int value) {
        GradeId id = new GradeId(studentId, disciplineId);
        Grade grade = gradeRepository.find(id);
        if (grade == null) {
            throw new EntityNotFoundException(
                    "Student " + studentId + " has no grade in discipline " + disciplineId);
        }
        grade.setValue(value);
        return gradeRepository.update(id, grade);
    }

    /**
     * Updates the grade of the student in the discipline, or adds it if the student is not graded yet.
     *
     * @return the created or updated grade
     */
    public Grade addOrUpdate(long studentId, long disciplineId, int value) {
        boolean graded = gradeRepository.find(new GradeId(studentId, disciplineId)) != null;
        return graded
                ? update(studentId, disciplineId, value)
                : add(studentId, disciplineId, value);
    }

    /**
     * @throws EntityNotFoundException if the student is not graded in the discipline
     */
    public void delete(long studentId, long disciplineId) {
        gradeRepository.delete(new GradeId(studentId, disciplineId));
    }
}
