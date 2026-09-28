package edu.epam.fop.web.jpa.repository;

import edu.epam.fop.web.jpa.entity.Department;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

/**
 * Departments are immutable, so only creating and reading are supported.
 */
@RequiredArgsConstructor
public class DepartmentRepository implements JpaRepository<Department, Long> {

    @Getter
    private final SessionFactory sessionFactory;

    @Override
    public Class<Department> getEntityClass() {
        return Department.class;
    }

    @Override
    public Department update(Long id, Department newValues) {
        throw new UnsupportedOperationException("Departments are immutable and cannot be updated");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Departments are immutable and cannot be deleted");
    }
}
