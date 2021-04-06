package src.bots;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import src.core.ui.Messenger;


public abstract class ActuatorBot extends BrigandBot implements Actuator {
	//	private Robot r;
	private JFrame yppFrame;
	private Container content;
	private int curX, curY;
	private Random _rand;

	public ActuatorBot(JFrame yppFrame, Messenger msger) throws AWTException {
		super(msger);
		this.yppFrame = yppFrame;
		this.content = yppFrame.getContentPane();
		curX = 0;
		curY = 0;
		_rand = new Random();
		//		r = new Robot();
		//		r.setAutoDelay(100);
	}

	public void sleep(int millis) {
		//		r.delay(millis);
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}

	public void sleep(long minTime, long maxTime) {
		try {
			sleep(random(minTime, maxTime));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void mouseMove(Point p) {
		mouseMove(p.x, p.y);
	}

	public void mouseMove(int x, int y) {
		//		r.mouseMove(x, y);
		MouseEvent move = new MouseEvent(content, MouseEvent.MOUSE_MOVED,
				System.currentTimeMillis(), 0, x, y, 0, false);
		pushEventToQueue(move);
		curX = x;
		curY = y;
	}

	public void mousePress(int buttons) {
		//		r.mousePress(buttons);
		MouseEvent press = new MouseEvent(content, MouseEvent.MOUSE_PRESSED,
				System.currentTimeMillis(), buttons, curX, curY, 1, false);
		pushEventToQueue(press);
	}

	public void mouseRelease(int buttons) {
		//		r.mouseRelease(buttons);
		MouseEvent press = new MouseEvent(content, MouseEvent.MOUSE_RELEASED,
				System.currentTimeMillis(), buttons, curX, curY, 1, false);
		pushEventToQueue(press);
	}

	public void mouseClick(int buttons) {
		Component c = SwingUtilities.getDeepestComponentAt(content, curX, curY);
		if (c instanceof JButton) {
			((JButton) c).doClick();
			return;
		}
		mousePress(buttons);
		mouseRelease(buttons);
		MouseEvent click = new MouseEvent(content, MouseEvent.MOUSE_CLICKED,
				System.currentTimeMillis(), buttons, curX, curY, 1, false);
		pushEventToQueue(click);
	}

	public void keyPress(int keycode, char c) {
		//		r.keyPress(keycode);
		KeyEvent press = new KeyEvent(content, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, c);
		pushEventToQueue(press);
	}

	public void keyPress(int keycode) {
		//		r.keyPress(keycode);
		KeyEvent press = new KeyEvent(content, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED);
		pushEventToQueue(press);
	}

	public void keyRelease(int keycode, char c) {
		//		r.keyRelease(keycode);
		KeyEvent release = new KeyEvent(content, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keycode, c);
		pushEventToQueue(release);
	}

	public void keyRelease(int keycode) {
		//		r.keyRelease(keycode);
		KeyEvent release = new KeyEvent(content, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED);
		pushEventToQueue(release);
	}

	private void pushEventToQueue(AWTEvent evt) {
		if (evt instanceof KeyEvent) {
			try {
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
				Method getD = null;
				for (Method m:KeyboardFocusManager.class.getDeclaredMethods()) {
//					System.out.println(m.getName());
					if (m.getName().equalsIgnoreCase("getKeyEventDispatchers"))
						getD = m;
				}
				getD.setAccessible(true);
				
				List<KeyEventDispatcher> disps = (List<KeyEventDispatcher>) getD.invoke(manager);
				for (KeyEventDispatcher d:disps) {
					if (d instanceof com.threerings.util.m) {
						Field fField = com.threerings.util.m.class.getDeclaredField("f");
						fField.setAccessible(true);
						fField.set(d, true);
					}
					d.dispatchKeyEvent((KeyEvent) evt);
				}
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else
			yppFrame.dispatchEvent(evt);
		try {
			sleep(random(20, 50)); //Minimal but necessary
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private long random(long start, long end) {
		return (long)(_rand.nextDouble() * (end - start) + start);
	}

	public void keyType(int keycode, char c) {
		keyPress(keycode, c);
		keyRelease(keycode, c);
	}

	public void keyType(int keycode) {
		keyPress(keycode);
		keyRelease(keycode);
	}
}
