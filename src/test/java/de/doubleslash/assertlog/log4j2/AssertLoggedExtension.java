package de.doubleslash.assertlog.log4j2;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.AssertionFailedError;

/**
 * JUnit extension to assert log statements for Log4j2.<p>
 * Usage Example:
 * <pre>
 * {@code
 *
 * @RegisterExtension
 * AssertLoggedExtension assertLogged = new AssertLoggedExtension();
 *
 * MyApp testee = new MyApp();
 *
 * @Test
 * public void shouldHaveLogged() {
 *    testee.run();
 *
 *    assertLogged.info("Running");
 * }
 * }
 * </pre>
 */
public class AssertLoggedExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private final ListAppender listAppender = new ListAppender();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(listAppender);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.removeAppender(listAppender);
        listAppender.clearLogEvents();
    }

    public void trace(String expectecMessage) {
        assertLogged(expectecMessage, Level.TRACE);
    }

    public void debug(String expectedMessage) {
        assertLogged(expectedMessage, Level.DEBUG);
    }

    public void info(String expectedMessage) {
        assertLogged(expectedMessage, Level.INFO);
    }

    public void warn(String expectedMessage) {
        assertLogged(expectedMessage, Level.WARN);
    }

    public void error(String expectedMessage) {
        assertLogged(expectedMessage, Level.ERROR);
    }

    public void eventMatching(Predicate<LogEvent> logEventPredicate) {
        listAppender.getLoggedEvents().stream()
                .filter(logEventPredicate)
                .findAny()
                .orElseThrow(() -> new AssertionFailedError("Log message matching predicate was expected, but not logged."));
    }

    private void assertLogged(String message, Level level) {
        listAppender.getLoggedEvents().stream()
                .filter(e -> e.getLevel().equals(level))
                .filter(e -> e.getMessage().getFormattedMessage().equals(message))
                .findAny()
                .orElseThrow(() -> new AssertionFailedError("Log message '" + message + "' with level '" + level + "' was expected, but not logged."));
    }

}

/**
 * An appender that captures all received events in a list.
 */
class ListAppender extends AbstractAppender {

    private final List<LogEvent> events = new ArrayList<>();

    protected ListAppender() {
        super("ListAppender", null, null, false, null);
        setStarted();
    }

    @Override
    public void append(LogEvent event) {
        events.add(event.toImmutable());
    }

    public List<LogEvent> getLoggedEvents() {
        return unmodifiableList(events);
    }

    public void clearLogEvents() {
        events.clear();
    }
}
