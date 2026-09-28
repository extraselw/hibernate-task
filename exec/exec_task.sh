#!/usr/bin/env bash

#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Get the root directory of the project (assuming 'exec' is a subdirectory of the root)
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Define the path to the JAR file
JAR_FILE="$PROJECT_ROOT/target/hibernate-task-0.0.1-SNAPSHOT.jar"

CLASSPATH="$JAR_FILE:$(sh "$PROJECT_ROOT/mvnw" -f "$PROJECT_ROOT/pom.xml" dependency:build-classpath -Dmdep.outputFile="$PROJECT_ROOT/cp.txt" -q && cat "$PROJECT_ROOT/cp.txt")"

# Define the prefix and task

PREFIX="edu.epam.fop.web.demos"
TASK="$1"
DEMO_CLASS="$2"

shift 2

MAIN_CLASS="${PREFIX}.${TASK}.${DEMO_CLASS}"

java -Djava.util.logging.config.file="$PROJECT_ROOT/src/main/resources/logging.properties" -cp "$CLASSPATH" $MAIN_CLASS "$@"