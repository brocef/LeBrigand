package lebrigand.bots;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BrigandBot implements Runnable {

    private static final Logger logger = Logger.getLogger(BrigandBot.class.getName());

    private final Object killMutex;
    private boolean kill;

    public BrigandBot() {
        this.kill = false;
        this.killMutex = new Object();
    }

    public abstract String getBotName();

    @Override
    public abstract void run();

    public void killBot() {
        synchronized (this.killMutex) {
            this.kill = true;
        }
    }

    public boolean stillAlive() {
        synchronized (this.killMutex) {
            return !this.kill;
        }
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            BrigandBot.logger.log(Level.WARNING, null, ex);
            this.killBot();
        }
    }
}
