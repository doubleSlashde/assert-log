package de.doubleslash.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * My great App!
 */
public class App {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Run the app. Do some logging that we can test using our JUnit Jupiter Extension.
     */
    public void run() {
        LOG.info("Running");

        LOG.warn("We have a situation here...");

        try {
            doSomethingDangerous();
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private void doSomethingDangerous() {
        throw new IllegalStateException("Something baaad happened!!");
    }

}
