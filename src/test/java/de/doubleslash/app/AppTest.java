package de.doubleslash.app;

import static org.apache.logging.log4j.Level.WARN;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.doubleslash.assertlog.log4j2.AssertLoggedExtension;

public class AppTest {

    @RegisterExtension
    static final AssertLoggedExtension assertLogged = new AssertLoggedExtension();

    // subject under test
    private final App app = new App();

    @Test
    public void shouldHaveLogged() {
        app.run();

        assertLogged.info("Running");
    }

    @Test
    void shouldHaveLoggedAlso() {
        app.run();

        assertLogged.eventMatching(ev -> ev.getLevel() == WARN &&
                ev.getMessage().getFormattedMessage().equals("We have a situation here..."));
    }

    @Test
    void shouldHaveLoggedException() {
        app.run();

        assertLogged.error("java.lang.IllegalStateException: Something baaad happened!!");
    }

}
