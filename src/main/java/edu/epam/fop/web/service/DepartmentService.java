package edu.epam.fop.web.service;

import edu.epam.fop.web.jpa.entity.Department;
import edu.epam.fop.web.jpa.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * @return the created department with the generated id
     */
    public Department create(String name) {
        Department department = new Department(name);
        departmentRepository.save(department);
        return department;
    }
}
