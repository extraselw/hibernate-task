-- Tasks 4-5: disciplines and their many-to-many relation with courses.
-- Deleting a course or a discipline removes only the relation rows, never the entity on the other side.

create table discipline
(
    id   bigserial primary key,
    name varchar not null
);

create table discipline_course
(
    discipline_id bigint not null,
    course_id     bigint not null,
    grading       varchar,
    constraint pk_discipline_course primary key (discipline_id, course_id),
    constraint fk_discipline_course_discipline
        foreign key (discipline_id) references discipline (id) on delete cascade,
    constraint fk_discipline_course_course
        foreign key (course_id) references course (id) on delete cascade
);

-- the primary key already covers lookups by discipline_id, this one covers lookups by course_id
create index idx_discipline_course_course_id on discipline_course (course_id);
