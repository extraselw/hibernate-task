package edu.epam.fop.web.requirements;

import edu.epam.fop.web.jpa.entity.Discipline;
import edu.epam.fop.web.jpa.entity.Grade;
import edu.epam.fop.web.jpa.entity.GradeId;
import edu.epam.fop.web.jpa.entity.Student;
import edu.epam.fop.web.jpa.repository.DisciplineRepository;
import edu.epam.fop.web.jpa.repository.GradeRepository;
import edu.epam.fop.web.jpa.repository.StudentRepository;
import edu.epam.fop.web.service.DisciplineService;
import edu.epam.fop.web.service.GradeService;
import edu.epam.fop.web.service.StudentService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 6: grades of students in disciplines and the average grade of a student.
 */
class Task6GradeTest extends DatabaseTest {

    private GradeRepository gradeRepository;
    private GradeService gradeService;
    private StudentRepository studentRepository;
    private StudentService studentService;
    private DisciplineRepository disciplineRepository;

    private long student;
    private long discipline1;
    private long discipline2;

    @BeforeEach
    void createServicesAndData() {
        gradeRepository = new GradeRepository(sessionFactory);
        gradeService = new GradeService(gradeRepository);
        studentRepository = new StudentRepository(sessionFactory);
        studentService = new StudentService(studentRepository);
        disciplineRepository = new DisciplineRepository(sessionFactory);
        DisciplineService disciplineService = new DisciplineService(disciplineRepository);

        student = studentService.create("student-1").getId();
        discipline1 = disciplineService.create("discipline-1").getId();
        discipline2 = disciplineService.create("discipline-2").getId();
    }

    @Test
    void gradeValueHasMinAndMaxAnnotations() throws NoSuchFieldException {
        var value = Grade.class.getDeclaredField("value");

        assertEquals(0, value.getAnnotation(Min.class).value());
        assertEquals(100, value.getAnnotation(Max.class).value());
    }

    @Test
    void studentHasOneToManyGradesAndTransientAverageGrade() throws NoSuchFieldException {
        assertNotNull(Student.class.getDeclaredField("grades").getAnnotation(OneToMany.class));
        assertNotNull(Student.class.getDeclaredField("averageGrade").getAnnotation(Transient.class));
    }

    @Test
    void averageGradeIsUpdatedByGradesSetter() {
        Student newStudent = new Student("student");
        Discipline discipline = new Discipline("discipline");

        newStudent.setGrades(List.of(
                new Grade(newStudent, discipline, 80),
                new Grade(newStudent, discipline, 91),
                new Grade(newStudent, discipline, null)));

        assertEquals(85.5, newStudent.getAverageGrade());
    }

    @Test
    void averageGradeIsNotPersisted() {
        assertEquals(List.of("active", "department_id", "id", "name"), selectStrings(
                "select column_name from information_schema.columns"
                        + " where table_schema = current_schema() and table_name = 'student'"
                        + " order by column_name"));
    }

    @Test
    void averageGradeIsCalculatedForStudentLoadedWithGrades() {
        gradeService.add(student, discipline1, 50);
        gradeService.add(student, discipline2, 71);

        assertEquals(60.5, studentService.findWithGrades(student).orElseThrow().getAverageGrade());
    }

    @Test
    void gradeIsAddedUpdatedAndDeleted() {
        GradeId id = new GradeId(student, discipline1);

        gradeService.add(student, discipline1, 50);
        assertEquals(50, gradeRepository.find(id).getValue());

        gradeService.update(student, discipline1, 100);
        assertEquals(100, gradeRepository.find(id).getValue());

        gradeService.delete(student, discipline1);
        assertNull(gradeRepository.find(id));
        assertNotNull(studentRepository.find(student));
        assertNotNull(disciplineRepository.find(discipline1));
    }

    @Test
    void addOrUpdateCreatesGradeFirstAndThenUpdatesIt() {
        gradeService.addOrUpdate(student, discipline1, 50);
        gradeService.addOrUpdate(student, discipline1, 75);

        assertEquals(75, gradeRepository.find(new GradeId(student, discipline1)).getValue());
        assertEquals(1, gradeRepository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void boundaryValuesAreAccepted(int value) {
        gradeService.add(student, discipline1, value);

        assertEquals(value, gradeRepository.find(new GradeId(student, discipline1)).getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void valueOutOfRangeIsRejectedOnAdd(int value) {
        assertThrows(ConstraintViolationException.class, () -> gradeService.add(student, discipline1, value));
        assertTrue(gradeRepository.findAll().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void valueOutOfRangeIsRejectedOnUpdate(int value) {
        gradeService.add(student, discipline1, 50);

        assertThrows(ConstraintViolationException.class, () -> gradeService.update(student, discipline1, value));
        assertEquals(50, gradeRepository.find(new GradeId(student, discipline1)).getValue());
    }

    @Test
    void gradingTwiceOrGradingUnknownStudentFails() {
        gradeService.add(student, discipline1, 50);

        assertThrows(EntityExistsException.class, () -> gradeService.add(student, discipline1, 60));
        assertThrows(EntityNotFoundException.class, () -> gradeService.add(42, discipline1, 60));
        assertThrows(EntityNotFoundException.class, () -> gradeService.update(student, discipline2, 60));
    }

    @Test
    void deletingStudentOrDisciplineDeletesTheirGrades() {
        gradeService.add(student, discipline1, 50);
        gradeService.add(student, discipline2, 60);

        new DisciplineService(disciplineRepository).delete(discipline1);
        assertEquals(1, gradeRepository.findAll().size());

        studentService.delete(student);
        assertTrue(gradeRepository.findAll().isEmpty());
    }
}
