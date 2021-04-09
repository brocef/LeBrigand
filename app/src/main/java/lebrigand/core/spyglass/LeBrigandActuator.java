/*
 * Don't go off selling software that uses this.
 */
package lebrigand.core.spyglass;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lebrigand.bots.Actuator;

public class LeBrigandActuator implements Actuator {
    private static final Logger logger = Logger.getLogger(LeBrigandActuator.class.getName());
    private final JFrame frame;
    private int currentMouseX, currentMouseY;

    public LeBrigandActuator(JFrame frame) {
        this.frame = frame;
        this.currentMouseX = 0;
        this.currentMouseY = 0;
    }

    private void updateMouseLocation(int x, int y) {
        this.currentMouseX = x;
        this.currentMouseY = y;
    }

    @Override
    public void mouseMove(int x, int y) {
        MouseEvent move = new MouseEvent(this.frame.getContentPane(), MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(), 0, x, y, 0, false);
        pushEventToQueue(move);
        this.updateMouseLocation(x, y);
    }

    @Override
    public void mousePress(int buttons) {
        MouseEvent press = new MouseEvent(this.frame.getContentPane(), MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), buttons, this.currentMouseX, this.currentMouseY, 1, false);
        pushEventToQueue(press);
    }

    @Override
    public void mouseRelease(int buttons) {
        MouseEvent press = new MouseEvent(this.frame.getContentPane(), MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), buttons, this.currentMouseX, this.currentMouseY, 1, false);
        pushEventToQueue(press);
    }

    @Override
    public void mouseClick(int buttons) {
        Component c = SwingUtilities.getDeepestComponentAt(this.frame.getContentPane(), this.currentMouseX, this.currentMouseY);
        if (c instanceof JButton) {
            ((JButton) c).doClick();
            return;
        }
        mousePress(buttons);
        mouseRelease(buttons);
        MouseEvent click = new MouseEvent(this.frame.getContentPane(), MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), buttons, this.currentMouseX, this.currentMouseY, 1, false);
        pushEventToQueue(click);
    }

    @Override
    public void keyPress(int keycode, char c) {
        KeyEvent press = new KeyEvent(this.frame.getContentPane(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, c);
        pushEventToQueue(press);
    }

    @Override
    public void keyPress(int keycode) {
        KeyEvent press = new KeyEvent(this.frame.getContentPane(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED);
        pushEventToQueue(press);
    }

    @Override
    public void keyRelease(int keycode, char c) {
        KeyEvent release = new KeyEvent(this.frame.getContentPane(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, c);
        pushEventToQueue(release);
    }

    @Override
    public void keyRelease(int keycode) {
        KeyEvent release = new KeyEvent(this.frame.getContentPane(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED);
        pushEventToQueue(release);
    }

    private void pushEventToQueue(AWTEvent evt) {
        LeBrigandActuator.logger.log(Level.INFO, "Pusing event to evt queue: {0}", evt.toString());
        SwingUtilities.invokeLater(() -> {
            this.safelyDispatchEvent(evt);
        });
    }
    
    private void safelyDispatchEvent(AWTEvent evt) {
        if (evt instanceof KeyEvent) {
            try {
                KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                Method getD = null;
                for (Method m : KeyboardFocusManager.class.getDeclaredMethods()) {
//					System.out.println(m.getName());
                    if (m.getName().equalsIgnoreCase("getKeyEventDispatchers")) {
                        getD = m;
                    }
                }
                getD.setAccessible(true);

                List<KeyEventDispatcher> disps = (List<KeyEventDispatcher>) getD.invoke(manager);
                for (KeyEventDispatcher d : disps) {
                    if (d instanceof com.threerings.util.m) {
                        Field fField = com.threerings.util.m.class.getDeclaredField("f");
                        fField.setAccessible(true);
                        fField.set(d, true);
                    }
                    d.dispatchKeyEvent((KeyEvent) evt);
                }
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        } else {
            this.frame.dispatchEvent(evt);
        }
//        try {
//                sleep(random(20, 50)); //Minimal but necessary
//        } catch (InterruptedException e) {
//                e.printStackTrace();
//        }
    }

//    private long random(long start, long end) {
//            return (long)(_rand.nextDouble() * (end - start) + start);
//    }

    @Override
    public void keyType(int keycode, char c) {
        keyPress(keycode, c);
        keyRelease(keycode, c);
    }

    @Override
    public void keyType(int keycode) {
        keyPress(keycode);
        keyRelease(keycode);
    }
}
