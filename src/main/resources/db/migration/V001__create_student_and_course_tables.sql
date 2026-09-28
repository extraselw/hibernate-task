-- Task 1: students and courses. Both are inactive by default.

create table student
(
    id            bigserial primary key,
    name          varchar not null,
    active        boolean not null default false,
    department_id bigint
);

create table course
(
    id       bigserial primary key,
    name     varchar not null,
    active   boolean not null default false,
    start_at timestamptz,
    end_at   timestamptz,
    -- a draft course (no start_at) or a course without a deadline (no end_at) is always valid
    constraint chk_course_end_at_after_start_at
        check (start_at is null or end_at is null or end_at > start_at)
);
