package edu.epam.fop.web.requirements;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 0: tables, columns, keys and constraints created by the Flyway migrations.
 */
class Task0SchemaTest extends DatabaseTest {

    @Test
    void migrationScriptsAreNamedVdddAndAllOfThemAreApplied() throws URISyntaxException, IOException {
        Path migrations = Path.of(Objects.requireNonNull(getClass().getResource("/db/migration")).toURI());
        List<String> scripts;
        try (Stream<Path> files = Files.list(migrations)) {
            scripts = files.map(file -> file.getFileName().toString()).sorted().toList();
        }

        assertFalse(scripts.isEmpty());
        scripts.forEach(script -> assertTrue(script.matches("V\\d{3}__\\w+\\.sql"), script));

        List<String> scriptVersions = scripts.stream().map(script -> script.substring(1, 4)).toList();
        assertEquals(scriptVersions, selectStrings(
                "select version from flyway_schema_history where success order by installed_rank"));
    }

    @Test
    void allTablesExist() {
        assertEquals(
                Set.of("student", "course", "department", "discipline", "discipline_course", "grade",
                        "flyway_schema_history"),
                Set.copyOf(selectStrings(
                        "select table_name from information_schema.tables where table_schema = current_schema()")));
    }

    @Test
    void columnsHaveTheSpecifiedTypes() {
        assertEquals(List.of("active boolean", "department_id bigint", "id bigint", "name character varying"),
                columns("student"));
        assertEquals(List.of("active boolean", "end_at timestamp with time zone", "id bigint",
                        "name character varying", "start_at timestamp with time zone"),
                columns("course"));
        assertEquals(List.of("id bigint", "name character varying"), columns("department"));
        assertEquals(List.of("id bigint", "name character varying"), columns("discipline"));
        assertEquals(List.of("course_id bigint", "discipline_id bigint", "grading character varying"),
                columns("discipline_course"));
        assertEquals(List.of("discipline_id bigint", "student_id bigint", "value integer"), columns("grade"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"student", "course", "department", "discipline"})
    void idIsBigserialPrimaryKey(String table) {
        assertEquals(List.of("id"), primaryKey(table));
        String idDefault = selectStrings("""
                select column_default from information_schema.columns
                where table_schema = current_schema() and table_name = ?1 and column_name = 'id'""", table).get(0);
        assertTrue(idDefault.startsWith("nextval("), idDefault);
    }

    @Test
    void relationTablesHaveCompositePrimaryKeys() {
        assertEquals(List.of("discipline_id", "course_id"), primaryKey("discipline_course"));
        assertEquals(List.of("student_id", "discipline_id"), primaryKey("grade"));
    }

    @Test
    void relationColumnsAreNotNullAndGradeValueIsNullable() {
        assertEquals(List.of("course_id NO", "discipline_id NO", "grading YES"), nullability("discipline_course"));
        assertEquals(List.of("discipline_id NO", "student_id NO", "value YES"), nullability("grade"));
    }

    @Test
    void gradeReferencesStudentAndDiscipline() {
        assertEquals(List.of("discipline_id -> discipline(id)", "student_id -> student(id)"), foreignKeys("grade"));
    }

    @Test
    void gradeValueIsLimitedFrom0To100InTheDatabase() {
        execute("insert into student (name) values ('student')");
        execute("insert into discipline (name) values ('discipline-1'), ('discipline-2'), ('discipline-3')");

        assertDoesNotThrow(() -> execute("insert into grade values (1, 1, 0), (1, 2, 100), (1, 3, null)"));
        assertThrows(PersistenceException.class, () -> execute("update grade set value = 101 where discipline_id = 1"));
        assertThrows(PersistenceException.class, () -> execute("update grade set value = -1 where discipline_id = 1"));
    }

    private List<String> columns(String table) {
        return selectStrings("""
                select column_name || ' ' || data_type from information_schema.columns
                where table_schema = current_schema() and table_name = ?1
                order by column_name""", table);
    }

    private List<String> nullability(String table) {
        return selectStrings("""
                select column_name || ' ' || is_nullable from information_schema.columns
                where table_schema = current_schema() and table_name = ?1
                order by column_name""", table);
    }

    private List<String> primaryKey(String table) {
        return selectStrings("""
                select kcu.column_name
                from information_schema.table_constraints tc
                join information_schema.key_column_usage kcu
                    on kcu.constraint_name = tc.constraint_name and kcu.table_schema = tc.table_schema
                where tc.table_schema = current_schema() and tc.table_name = ?1 and tc.constraint_type = 'PRIMARY KEY'
                order by kcu.ordinal_position""", table);
    }

    private List<String> foreignKeys(String table) {
        return selectStrings("""
                select kcu.column_name || ' -> ' || ccu.table_name || '(' || ccu.column_name || ')'
                from information_schema.table_constraints tc
                join information_schema.key_column_usage kcu
                    on kcu.constraint_name = tc.constraint_name and kcu.table_schema = tc.table_schema
                join information_schema.constraint_column_usage ccu
                    on ccu.constraint_name = tc.constraint_name and ccu.table_schema = tc.table_schema
                where tc.table_schema = current_schema() and tc.table_name = ?1 and tc.constraint_type = 'FOREIGN KEY'
                order by 1""", table);
    }
}
