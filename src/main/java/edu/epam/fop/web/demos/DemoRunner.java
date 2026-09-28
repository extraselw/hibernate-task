package edu.epam.fop.web.demos;

import edu.epam.fop.web.jpa.SessionFactoryProvider;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.SessionFactory;

import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Common infrastructure of the demo classes.
 */
public final class DemoRunner {

    private DemoRunner() {
    }

    /**
     * Runs the demo with a new session factory and closes the factory afterwards.
     * Expected failures (invalid arguments, missing or duplicate entities, constraint violations)
     * are printed as a short message instead of a stack trace.
     */
    public static void run(Consumer<SessionFactory> demo) {
        try (SessionFactory sessionFactory = SessionFactoryProvider.createSessionFactory()) {
            demo.accept(sessionFactory);
        } catch (IllegalArgumentException | DateTimeParseException
                 | EntityNotFoundException | EntityExistsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ConstraintViolationException e) {
            System.out.println("Validation failed: " + describe(e));
        }
    }

    /**
     * Prints every item on its own line, or the message if there are no items.
     */
    public static void printAll(Collection<?> items, String messageIfEmpty) {
        if (items.isEmpty()) {
            System.out.println(messageIfEmpty);
        } else {
            items.forEach(System.out::println);
        }
    }

    private static String describe(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(DemoRunner::describe)
                .collect(Collectors.joining("; "));
    }

    private static String describe(ConstraintViolation<?> violation) {
        return violation.getRootBeanClass().getSimpleName() + "." + violation.getPropertyPath()
                + " " + violation.getMessage() + " (was " + violation.getInvalidValue() + ")";
    }
}
