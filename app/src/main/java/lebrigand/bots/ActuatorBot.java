package lebrigand.bots;

import java.awt.Point;
import lebrigand.core.spyglass.LeBrigandActuator;

public abstract class ActuatorBot extends BrigandBot implements Actuator {
    private final LeBrigandActuator actuator;

    public ActuatorBot(LeBrigandActuator actuator) {
        super();
        this.actuator = actuator;
    }

    public void mouseMove(Point p) {
        this.actuator.mouseMove(p.x, p.y);
    }

    @Override
    public void mouseMove(int x, int y) {
        this.actuator.mouseMove(x, y);
    }

    @Override
    public void mousePress(int buttons) {
        this.actuator.mousePress(buttons);
    }

    @Override
    public void mouseRelease(int buttons) {
        this.actuator.mouseRelease(buttons);
    }

    @Override
    public void mouseClick(int buttons) {
        this.actuator.mouseClick(buttons);
    }

    @Override
    public void keyPress(int keycode, char c) {
        this.actuator.keyPress(keycode, c);
    }

    @Override
    public void keyPress(int keycode) {
        this.actuator.keyPress(keycode);
    }

    @Override
    public void keyRelease(int keycode, char c) {
        this.actuator.keyRelease(keycode, c);
    }

    @Override
    public void keyRelease(int keycode) {
        this.actuator.keyRelease(keycode);
    }

    @Override
    public void keyType(int keycode, char c) {
        this.actuator.keyType(keycode, c);
    }

    @Override
    public void keyType(int keycode) {
        this.actuator.keyType(keycode);
    }
}
