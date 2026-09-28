package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.Course;
import edu.epam.fop.web.jpa.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Course operations.
 * <p>
 * Whether a course is ongoing is determined by its dates only, the {@code active} flag is ignored:
 * <ul>
 *     <li>a course without {@code start_at} is a draft;</li>
 *     <li>a course without {@code end_at} has no deadline;</li>
 *     <li>an active course has {@code start_at} in the past and {@code end_at} either absent or in the future;</li>
 *     <li>a future course has {@code start_at} in the future.</li>
 * </ul>
 */
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * Creates an inactive draft course.
     *
     * @return the created course with the generated id
     */
    public Course create(String name) {
        Course course = new Course(name);
        courseRepository.save(course);
        return course;
    }

    public Optional<Course> findById(long id) {
        return Optional.ofNullable(courseRepository.find(id));
    }

    /**
     * @return the activated course
     * @throws EntityNotFoundException if there is no course with such id
     */
    public Course activate(long id) {
        Course course = getById(id);
        course.setActive(true);
        return courseRepository.update(id, course);
    }

    /**
     * Deletes the course and its relations with disciplines, the disciplines themselves are kept.
     *
     * @throws EntityNotFoundException if there is no course with such id
     */
    public void delete(long id) {
        courseRepository.delete(id);
    }

    /**
     * @param startAt new start of the course, {@code null} turns the course into a draft
     * @return the updated course
     * @throws EntityNotFoundException  if there is no course with such id
     * @throws IllegalArgumentException if the course has an end that is not after the new start
     */
    public Course updateStartAt(long id, OffsetDateTime startAt) {
        Course course = getById(id);
        validateDates(startAt, course.getEndAt());
        course.setStartAt(startAt);
        return courseRepository.update(id, course);
    }

    /**
     * @param endAt new end of the course, {@code null} removes the deadline
     * @return the updated course
     * @throws EntityNotFoundException  if there is no course with such id
     * @throws IllegalArgumentException if the course has a start and the new end is not after it
     */
    public Course updateEndAt(long id, OffsetDateTime endAt) {
        Course course = getById(id);
        validateDates(course.getStartAt(), endAt);
        course.setEndAt(endAt);
        return courseRepository.update(id, course);
    }

    public List<Course> findActive() {
        return courseRepository.findActive(OffsetDateTime.now());
    }

    public List<Course> findFuture() {
        return courseRepository.findFuture(OffsetDateTime.now());
    }

    public List<Course> findByDiscipline(long disciplineId) {
        return courseRepository.findByDisciplineId(disciplineId);
    }

    private Course getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Course with id " + id + " not found"));
    }

    private static void validateDates(OffsetDateTime startAt, OffsetDateTime endAt) {
        // a draft course (no start) and a course without a deadline (no end) need no validation
        if (startAt != null && endAt != null && !endAt.isAfter(startAt)) {
            throw new IllegalArgumentException(
                    "Course end_at (" + endAt + ") must be after start_at (" + startAt + ")");
        }
    }
}
