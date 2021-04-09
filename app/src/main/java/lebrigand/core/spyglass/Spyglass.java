package lebrigand.core.spyglass;

import java.awt.Window;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import lebrigand.core.ui.Messenger;
import lebrigand.LeLogger;
import lebrigand.core.ui.GlassPane;

import com.sun.java.accessibility.util.GUIInitializedListener;
import com.sun.java.accessibility.util.TopLevelWindowListener;
import com.threerings.media.ManagedJFrame;
import java.util.logging.Level;

public class Spyglass implements TopLevelWindowListener, GUIInitializedListener {

    private static final Logger logger = Logger.getLogger(Spyglass.class.getName());
    Messenger msger;

    private final ArrayList<GameStateSubscriber> stateSubscribers;

    //Game state data/hooks
    GameState game;
    public JFrame yppFrame;
    GlassPane glassPane;

    public Spyglass(Messenger msger) {
        game = null;
        yppFrame = null;
        this.msger = msger;
        stateSubscribers = new ArrayList<>();
    }

    public void bringGameToFront() {
        yppFrame.toFront();
        yppFrame.getContentPane().requestFocus();
    }

    public boolean isSpyglassReady() {
        return game != null;
    }

    public synchronized GameState updateGameState() {
        if (this.game != null) {
            return this.game.updateGameState();
        }
        return null;
    }

    public GameState getOldGameState() {
        return this.game;
    }

    private void intializeGameState() {
        this.game = new GameState(this, yppFrame);
        this.glassPane.game = this.game;
    }

    public void addStateSubscriber(GameStateSubscriber subscriber) {
        stateSubscribers.add(subscriber);
    }

    public void removeStateSubscriber(GameStateSubscriber subscriber) {
        stateSubscribers.remove(subscriber);
    }

    protected void broadcastGameState(GameState state) {
        // for (GameStateSubscriber subscriber:stateSubscribers)
        // subscriber.updateGameData(state);
    }

    @Override
    public void guiInitialized() {
        //msger.log("GUI initialized");
        //Perhaps not necessary to have this, but will keep around for later
        // LeLogger.setUpLogger();
    }

    @Override
    public void topLevelWindowCreated(Window w) {
//		msger.log("Window %s created\n", w.toString());
        if (w instanceof ManagedJFrame) {
            yppFrame = (JFrame) w;
//			KeyboardFocusManager.setCurrentKeyboardFocusManager(
//					new TheFucker(yppFrame, KeyboardFocusManager.getCurrentKeyboardFocusManager()));
            LeLogger.setUpLogger();
            logger.log(Level.INFO, "Hooked into {0}", yppFrame.toString());
            this.glassPane = new GlassPane(yppFrame.getContentPane());
            yppFrame.setGlassPane(this.glassPane);
            this.glassPane.setVisible(true);
            this.glassPane.setEnabled(true);
            this.glassPane.setOpaque(false);
            yppFrame.validate();
        }
    }

    @Override
    public void topLevelWindowDestroyed(Window w) {
        //msger.log("Window %s destroyed\n", w.toString());
    }
}
