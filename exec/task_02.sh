#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

TASK='task2'

function run_script() {
  local -r log_msg="$1"
  local -r demo="$2"
  shift 2

  echo $log_msg
  bash "${SCRIPT_DIR}/exec_task.sh" "${TASK}" $demo $@
}

CREATE_DEPARTMENT_DEMO='CreateDepartmentDemo'
run_script 'Creating department-1' $CREATE_DEPARTMENT_DEMO 'department-1'
run_script 'Creating department-2' $CREATE_DEPARTMENT_DEMO 'department-2'
run_script 'Creating department-3' $CREATE_DEPARTMENT_DEMO 'department-3'

MOVE_STUDENT_TO_DEPARTMENT_DEMO='MoveStudentToDepartmentDemo'
run_script 'Moving student-1 to department-1' $MOVE_STUDENT_TO_DEPARTMENT_DEMO 1 1
run_script 'Moving student-3 to department-2' $MOVE_STUDENT_TO_DEPARTMENT_DEMO 3 2
run_script 'Moving student-2 to department-2' $MOVE_STUDENT_TO_DEPARTMENT_DEMO 2 2

SEARCH_STUDENTS_BY_DEPARTMENT='SearchStudentsByDepartmentDemo'
run_script 'Searching students in department-1' $SEARCH_STUDENTS_BY_DEPARTMENT 1
run_script 'Searching students in department-2' $SEARCH_STUDENTS_BY_DEPARTMENT 2
run_script 'Searching students in department-3' $SEARCH_STUDENTS_BY_DEPARTMENT 3