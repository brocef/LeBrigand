/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package lebrigand;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testAppHasAGreeting() {
        String[] args = new String[0];
        App classUnderTest = new App(args);
        assertNotNull("App bridge not initialized", classUnderTest.bridge);
    }
}