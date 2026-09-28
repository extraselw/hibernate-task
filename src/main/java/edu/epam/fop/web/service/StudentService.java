package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.Student;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * Creates an inactive student that does not belong to any department.
     *
     * @return the created student with the generated id
     */
    public Student create(String name) {
        Student student = new Student(name);
        studentRepository.save(student);
        return student;
    }

    public Optional<Student> findById(long id) {
        return Optional.ofNullable(studentRepository.find(id));
    }

    /**
     * @return the activated student
     * @throws EntityNotFoundException if there is no student with such id
     */
    public Student activate(long id) {
        Student student = getById(id);
        student.setActive(true);
        return studentRepository.update(id, student);
    }

    /**
     * Deletes the student together with the student's grades.
     *
     * @throws EntityNotFoundException if there is no student with such id
     */
    public void delete(long id) {
        studentRepository.delete(id);
    }

    /**
     * @return the moved student
     * @throws EntityNotFoundException if either the student or the department does not exist
     */
    public Student moveToDepartment(long studentId, long departmentId) {
        return studentRepository.moveToDepartment(studentId, departmentId);
    }

    public List<Student> findByDepartment(long departmentId) {
        return studentRepository.findByDepartmentId(departmentId);
    }

    /**
     * Finds the student together with the grades, so that the average grade is calculated.
     */
    public Optional<Student> findWithGrades(long id) {
        return Optional.ofNullable(studentRepository.findWithGrades(id));
    }

    private Student getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Student with id " + id + " not found"));
    }
}
