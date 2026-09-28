-- Task 6: grades of students in disciplines.

create table grade
(
    student_id    bigint not null,
    discipline_id bigint not null,
    value         int,
    constraint pk_grade primary key (student_id, discipline_id),
    constraint fk_grade_student
        foreign key (student_id) references student (id) on delete cascade,
    constraint fk_grade_discipline
        foreign key (discipline_id) references discipline (id) on delete cascade,
    constraint chk_grade_value_range check (value between 0 and 100)
);

-- the primary key already covers lookups by student_id, this one covers lookups by discipline_id
create index idx_grade_discipline_id on grade (discipline_id);
