package edu.epam.fop.web.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Composite primary key of {@link DisciplineCourse}: {@code primary key(discipline_id, course_id)}.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DisciplineCourseId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "discipline_id")
    private Long disciplineId;

    @Column(name = "course_id")
    private Long courseId;
}
