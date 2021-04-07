package lebrigand.core.ui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import lebrigand.core.spyglass.GameState;


public class GlassPane extends JComponent {
    public GameState game;
    Container contentPane;
    Timer repaintTimer;
	Point point;

    protected void paintComponent(Graphics g) {
        if (point != null) {
            g.setColor(Color.red);
            g.fillOval(point.x - 4, point.y - 4, 8, 8);
        }
        if (this.game != null) {
            this.game.paint(g, this);
        }
    }

    public void setPoint(Point p) {
        point = p;
    }

    @Override
    public boolean contains(int x, int y) {
        Component[] components = getComponents();
        for(int i = 0; i < components.length; i++)
        {
            Component component = components[i];
            Point containerPoint = SwingUtilities.convertPoint(
                this,
                x, y,
                component);
            if(component.contains(containerPoint))
            {
                return true;
            }
        }
        return false;
    }

    public GlassPane(Container contentPane) {
		this.setPoint(new Point(50, 50));
        this.contentPane = contentPane;
        this.game = null;

		this.repaintTimer = new Timer(20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		this.repaintTimer.start();
    }
}
