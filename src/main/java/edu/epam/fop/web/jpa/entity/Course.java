package edu.epam.fop.web.jpa.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * A university course, stored in the second-level cache.
 * <p>
 * A course without {@code startAt} is a draft, and a course without {@code endAt} has no deadline.
 * A new course is inactive and has neither date.
 */
@Entity
@Table(name = "course")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "start_at")
    @Setter(AccessLevel.NONE)
    private OffsetDateTime startAt;

    @Column(name = "end_at")
    @Setter(AccessLevel.NONE)
    private OffsetDateTime endAt;

    public Course(String name) {
        this.name = name;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = toUtc(startAt);
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = toUtc(endAt);
    }

    // timestamptz keeps the instant but not the original offset, and the database returns it in UTC,
    // so dates are kept in UTC to look the same before and after they are saved
    private static OffsetDateTime toUtc(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
