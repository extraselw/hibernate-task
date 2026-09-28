#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

TASK='task6'

function run_script() {
  local -r log_msg="$1"
  local -r demo="$2"
  shift 2

  echo $log_msg
  bash "${SCRIPT_DIR}/exec_task.sh" "${TASK}" $demo $@
}

ADDING_GRADE_DEMO='AddingOrUpdatingGradeDemo'
run_script 'Adding grade for student-1 discipline-1 50' $ADDING_GRADE_DEMO 1 1 50
run_script 'Adding grade for student-2 discipline-2 30' $ADDING_GRADE_DEMO 2 2 30

UPDATING_GRADE_DEMO='AddingOrUpdatingGradeDemo'
run_script 'Updating grade for student-1 discipline-1 to 100' $UPDATING_GRADE_DEMO 1 1 100

DELETING_GRADE_DEMO='DeletingGradeDemo'
run_script 'Deleting grade for student-2 discipline-2' $DELETING_GRADE_DEMO 2 2
