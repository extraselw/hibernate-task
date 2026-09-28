-- Task 2: departments. A student may belong to at most one department.

create table department
(
    id   bigserial primary key,
    name varchar not null
);

alter table student
    add constraint fk_student_department
        foreign key (department_id) references department (id);

create index idx_student_department_id on student (department_id);
