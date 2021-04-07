package lebrigand;

import java.io.InputStream;
import java.io.IOException;
import java.util.logging.*;
import java.util.ArrayList;

public class LeLogger {
    public static ArrayList<Handler> handlers = new ArrayList<Handler>();

    public static void setUpLogger() {
		try {
			InputStream stream = LeLogger.class.getClassLoader().getResourceAsStream("logging.properties");
			LogManager.getLogManager().readConfiguration(stream);
			Handler fh = new FileHandler("lebrigand.log", true);
            LeLogger.handlers.add(fh);
			LeLogger.ensureHandlers();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

    private static void ensureHandlers() {
        Logger rootLogger = Logger.getLogger("lebrigand");
        ArrayList<Handler> missingHandlers = new ArrayList<Handler>(LeLogger.handlers);
        for (Handler h : rootLogger.getHandlers()) {
            missingHandlers.remove(h);
        }
        for (Handler h : missingHandlers) {
            rootLogger.addHandler(h);
        }
    }
}
