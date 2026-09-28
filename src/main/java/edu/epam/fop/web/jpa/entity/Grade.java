package edu.epam.fop.web.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Grade of a student in a discipline. The value is optional, but when present it must be within
 * [{@value #MIN_VALUE}, {@value #MAX_VALUE}].
 */
@Entity
@Table(name = "grade")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Grade {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 100;
    private static final String VALUE_RANGE_MESSAGE = "must be between " + MIN_VALUE + " and " + MAX_VALUE;

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private GradeId id;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter(AccessLevel.NONE)
    private Student student;

    @MapsId("disciplineId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter(AccessLevel.NONE)
    private Discipline discipline;

    @Min(value = MIN_VALUE, message = VALUE_RANGE_MESSAGE)
    @Max(value = MAX_VALUE, message = VALUE_RANGE_MESSAGE)
    @Column(name = "value")
    @ToString.Include(rank = 1)
    private Integer value;

    public Grade(Student student, Discipline discipline, Integer value) {
        this.id = new GradeId(student.getId(), discipline.getId());
        this.student = student;
        this.discipline = discipline;
        this.value = value;
    }

    // ids are printed instead of the lazy associations, so toString() is safe outside of a session
    @ToString.Include(name = "studentId", rank = 3)
    private Long studentId() {
        return id.getStudentId();
    }

    @ToString.Include(name = "disciplineId", rank = 2)
    private Long disciplineId() {
        return id.getDisciplineId();
    }
}
