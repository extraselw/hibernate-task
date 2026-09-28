package edu.epam.fop.web.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

/**
 * A university student. A new student is inactive and does not belong to any department.
 */
@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    private Department department;

    @OneToMany(mappedBy = "student")
    @ToString.Exclude
    private List<Grade> grades = new ArrayList<>();

    /**
     * Average value of the student's grades, or {@code null} if there are no graded disciplines.
     * It is calculated in Java only and never persisted.
     */
    @Transient
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private Double averageGrade;

    public Student(String name) {
        this.name = name;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
        this.averageGrade = calculateAverage(grades);
    }

    /**
     * Hibernate injects the grades directly into the field, bypassing {@link #setGrades(List)}.
     * When the grades were fetched together with the student, they are passed through the setter
     * so that the average grade is up-to-date. Lazy, not yet loaded grades are left untouched.
     */
    @PostLoad
    private void refreshAverageGrade() {
        if (Hibernate.isInitialized(grades)) {
            setGrades(grades);
        }
    }

    private static Double calculateAverage(List<Grade> grades) {
        OptionalDouble average = grades.stream()
                .map(Grade::getValue)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average();
        return average.isPresent() ? average.getAsDouble() : null;
    }

    // the id is read from the lazy proxy without initializing it, so toString() is safe outside of a session
    @ToString.Include(name = "departmentId")
    private Long departmentId() {
        return department == null ? null : department.getId();
    }
}
