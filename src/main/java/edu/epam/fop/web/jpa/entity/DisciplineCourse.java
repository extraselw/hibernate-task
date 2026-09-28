package edu.epam.fop.web.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Relation between a course and a discipline taught in it, together with the way the discipline is graded.
 * <p>
 * Deleting a course or a discipline removes the relation in the database ({@code on delete cascade}),
 * but never the entity on the other side of the relation.
 */
@Entity
@Table(name = "discipline_course")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class DisciplineCourse {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private DisciplineCourseId id;

    @MapsId("disciplineId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter(AccessLevel.NONE)
    private Discipline discipline;

    @MapsId("courseId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter(AccessLevel.NONE)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading")
    @ToString.Include(rank = 1)
    private Grading grading;

    public DisciplineCourse(Discipline discipline, Course course) {
        this.id = new DisciplineCourseId(discipline.getId(), course.getId());
        this.discipline = discipline;
        this.course = course;
    }

    // ids are printed instead of the lazy associations, so toString() is safe outside of a session
    @ToString.Include(name = "courseId", rank = 3)
    private Long courseId() {
        return id.getCourseId();
    }

    @ToString.Include(name = "disciplineId", rank = 2)
    private Long disciplineId() {
        return id.getDisciplineId();
    }
}
