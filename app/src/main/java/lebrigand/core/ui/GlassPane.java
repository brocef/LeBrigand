package lebrigand.core.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import lebrigand.core.spyglass.GameState;

public class GlassPane extends JComponent {

    public GameState game;
    Container contentPane;
    Timer repaintTimer;

    @Override
    protected void paintComponent(Graphics g) {
        Color orig = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString("LeBrigand is running!", 330, 20);
        g.setColor(orig);
        if (this.game != null) {
            this.game.paint(g, this);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        Component[] components = getComponents();
        for (Component component : components) {
            Point containerPoint = SwingUtilities.convertPoint(
                    this,
                    x, y,
                    component);
            if (component.contains(containerPoint)) {
                return true;
            }
        }
        return false;
    }

    public GlassPane(Container contentPane) {
        this.contentPane = contentPane;
        this.game = null;

        this.repaintTimer = new Timer(20, (ActionEvent e) -> {
            repaint();
        });
        this.repaintTimer.start();
    }
}
