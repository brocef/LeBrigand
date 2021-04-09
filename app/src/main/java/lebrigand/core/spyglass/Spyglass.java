package lebrigand.core.spyglass;

import java.awt.Window;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import lebrigand.core.ui.Messenger;
import lebrigand.LeLogger;
import lebrigand.core.ui.GlassPane;

import com.threerings.media.ManagedJFrame;
import java.util.logging.Level;

public class Spyglass {

    private static final Logger logger = Logger.getLogger(Spyglass.class.getName());

    //Game state data/hooks
    GameState game;
    private JFrame yppFrame;

    public Spyglass(JFrame frame) {
        game = null;
        this.yppFrame = frame;
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
//        this.glassPane.game = this.game;
    }
}
