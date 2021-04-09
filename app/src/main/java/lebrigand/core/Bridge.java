package lebrigand.core;

import com.google.common.collect.Lists;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JFrame;

import lebrigand.LeLogger;
import lebrigand.bots.SpyglassBot;
import lebrigand.bots.bilge.BilgeBot;
import lebrigand.bots.rigging.RiggingBot;
import lebrigand.bots.testing.TestBot;
import lebrigand.core.spyglass.Spyglass;
import lebrigand.core.spyglass.VMInitializationFailure;
import lebrigand.core.ui.LeBrigandFrame;
import lebrigand.WrappedYoApp;
import com.threerings.yohoho.client.YoFrame;
import java.lang.reflect.Field;

//import com.melloware.jintellitype.HotkeyListener;
//import com.melloware.jintellitype.IntellitypeListener;
//import com.melloware.jintellitype.JIntellitype;
public class Bridge extends Thread implements ActionListener {

    private static Logger logger = Logger.getLogger(Bridge.class.getName());

    //UI Stuff
    LeBrigandFrame df;

    //Core classes
    Spyglass spy;

    //Current bots
    SpyglassBot currentBot;

    public Bridge() {
        super();
        df = new LeBrigandFrame(this);
        spy = new Spyglass(df);
        spy.addStateSubscriber(df);
        currentBot = null;

        /*
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().addIntellitypeListener(this);
		JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'V'); //KILL CURRENT BOT
		JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'C'); //Run Rigging Bot
		JIntellitype.getInstance().registerHotKey(3, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'D'); //Run Test Bot
         */
    }

    public Spyglass getSpyglass() {
        return this.spy;
    }

    public String getActiveBotName() {
        return currentBot == null ? "None" : currentBot.getBotName();
    }

    public void run() {
        this.logger.info("Starting bridge");
        df.setVisible(true);

        //		try {
        //			while (df.isDisplayable()) {
        //				if (spy.isSpyglassReady())
        //					spy.updateGameState();
        //				Thread.sleep(250);
        //			}
        //		} catch(InterruptedException e) {
        //			e.printStackTrace();
        //		}
    }

    /*
	@Override
	public void onIntellitype(int command) {

	}

	@Override
	public void onHotKey(int id) {
		if (id == 1) {
			df.log("Kill command received");
			if (currentBot != null) {
				currentBot.killBot();
				currentBot = null;
			}
		} else if (id == 2) {
			try {
				if (currentBot != null && currentBot.stillAlive())
					currentBot.killBot();
				currentBot = new RiggingBot(spy, spy.updateGameState().getYPPFrame(), df);
				currentBot.start();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		} else if (id == 3) {
			try {
				if (currentBot != null && currentBot.stillAlive())
					currentBot.killBot();
				currentBot = new TestBot(spy, spy.updateGameState().getYPPFrame(), df);
				currentBot.start();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
     */

    @Override
    public void actionPerformed(ActionEvent a) {
        this.logger.info("ActionPerformed: " + a.toString());
        if (a.getActionCommand().equals(LeBrigandFrame.AC_INIT_VM)) {
            this.logger.info("Initializing VM");
            try {
                if (!spy.isSpyglassReady()) {
                    spy.initializeSpyglass();
                }
            } catch (VMInitializationFailure e) {
                this.logger.log(Level.SEVERE, "Fatal Error: VM Failed to initialize!");
                this.logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_START_BILGE_BOT)) {
            try {
                if (currentBot != null && currentBot.isAlive()) {
                    currentBot.killBot();
                } else {
                    currentBot = new BilgeBot(spy, spy.updateGameState().getYPPFrame(), df);
                    currentBot.start();
                }
            } catch (AWTException e) {
                this.logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_INPUT)) {
            WrappedYoApp app = WrappedYoApp.singleton;
            JFrame frame = spy.yppFrame;
            ArrayList<Component> compsToProcess = new ArrayList<Component>();
            compsToProcess.add(frame);
            while (!compsToProcess.isEmpty()) {
                Component c = compsToProcess.remove(0);
                if (Container.class.isInstance(c)) {
                    Container cont = (Container) c;
                    compsToProcess.addAll(Arrays.asList(cont.getComponents()));
                }
                this.logger.info(c.toString());
            }

            YoFrame yoframe = app.getYoFrame();
            ArrayList<Object> objsToProcess = new ArrayList<Object>();
            ArrayList<Object> seenObjs = new ArrayList<Object>();
            objsToProcess.add(yoframe);
            int iters = 0;
            while (!objsToProcess.isEmpty()) {
                iters++;
                if (iters > 5000)
                    break;
                Object o = objsToProcess.remove(0);
                seenObjs.add(o);
                for (Field f : o.getClass().getDeclaredFields()) {
                    try {
                        boolean is_accessible = f.isAccessible();
                        f.setAccessible(true);
                        Class t = f.getType();
                        if (t == Boolean.TYPE) {
                            this.logger.info(String.format("%s.%s: %b", o.getClass().getName(), f.getName(), f.getBoolean(o)));
                        } else if (t == Integer.TYPE) {
                            this.logger.info(String.format("%s.%s: %d", o.getClass().getName(), f.getName(), f.getInt(o)));
                        } else if (t == Short.TYPE) {
                            this.logger.info(String.format("%s.%s: %d", o.getClass().getName(), f.getName(), f.getShort(o)));
                        } else if (t == Long.TYPE) {
                            this.logger.info(String.format("%s.%s: %d", o.getClass().getName(), f.getName(), f.getLong(o)));
                        } else if (t == Double.TYPE) {
                            this.logger.info(String.format("%s.%s: %f", o.getClass().getName(), f.getName(), f.getDouble(o)));
                        } else if (t == Float.TYPE) {
                            this.logger.info(String.format("%s.%s: %f", o.getClass().getName(), f.getName(), f.getFloat(o)));
                        } else if (t.getName().startsWith("com.threerings")) {
                            Object child = f.get(o);
                            if (child != null){
                                this.logger.info(String.format("%s.%s: %s", o.getClass().getName(), f.getName(), child.getClass().getName()));
                                if (!seenObjs.contains(child)) {
                                    objsToProcess.add(child);
                                }
                            }
                        }
                        f.setAccessible(is_accessible);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(Bridge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Bridge.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            this.logger.info("Obj process count: "+iters);

//			try {
//				if (currentBot != null && currentBot.isAlive()) {
//					currentBot.killBot();
//				} else {
//					currentBot = new TestBot(spy, spy.updateGameState().getYPPFrame(), df);
//					currentBot.start();
//				}
//			} catch (AWTException e) {
//				this.logger.log(Level.SEVERE, e.getMessage(), e);
//			}
        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_START_RIGGING_BOT)) {
            try {
                if (currentBot != null && currentBot.isAlive()) {
                    currentBot.killBot();
                } else {
                    currentBot = new RiggingBot(spy, spy.updateGameState().getYPPFrame(), df);
                    currentBot.start();
                }
            } catch (AWTException e) {
                this.logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
