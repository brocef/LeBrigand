package lebrigand.core;

import com.sun.java.accessibility.util.EventQueueMonitor;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JFrame;

import com.sun.java.accessibility.util.GUIInitializedListener;
import com.sun.java.accessibility.util.TopLevelWindowListener;
import com.threerings.media.ManagedJFrame;

import lebrigand.bots.SpyglassBot;
import lebrigand.bots.bilge.BilgeBot;
import lebrigand.bots.rigging.RiggingBot;
import lebrigand.core.spyglass.Spyglass;
import lebrigand.core.ui.LeBrigandFrame;
import lebrigand.WrappedYoApp;
import com.threerings.yohoho.client.YoFrame;
import java.awt.Window;
import java.lang.ref.WeakReference;
import java.util.List;
import lebrigand.LeLogger;
import lebrigand.core.spyglass.ComponentManager;
import lebrigand.core.spyglass.LeBrigandActuator;
import lebrigand.core.spyglass.LeBrigandBindingManager;
import lebrigand.core.ui.GlassPane;
import lebrigand.core.ui.NewLeBrigandFrame;

//import com.melloware.jintellitype.HotkeyListener;
//import com.melloware.jintellitype.IntellitypeListener;
//import com.melloware.jintellitype.JIntellitype;
public class Bridge implements ActionListener, TopLevelWindowListener, GUIInitializedListener {

    private static final Logger logger = Logger.getLogger(Bridge.class.getName());

    //UI Stuff
    NewLeBrigandFrame df;

    //Core classes
    Spyglass spy;
    LeBrigandActuator actuator;
    BotManager botManager;
    GlassPane glassPane;

    public Bridge() {
        this.glassPane = null;
        this.actuator = null;
        this.botManager = null;
        this.spy = null;
        
        EventQueueMonitor.addTopLevelWindowListener(this);
        EventQueueMonitor.addGUIInitializedListener(this);
        /*
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().addIntellitypeListener(this);
		JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'V'); //KILL CURRENT BOT
		JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'C'); //Run Rigging Bot
		JIntellitype.getInstance().registerHotKey(3, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, (int)'D'); //Run Test Bot
         */
    }

    private void initialize(Window w) {
        JFrame frame = (JFrame) w;
//			KeyboardFocusManager.setCurrentKeyboardFocusManager(
//					new TheFucker(yppFrame, KeyboardFocusManager.getCurrentKeyboardFocusManager()));
        LeLogger.setUpLogger();
        logger.log(Level.INFO, "Hooked into {0}", frame.toString());
        this.glassPane = new GlassPane(frame.getContentPane());
        frame.setGlassPane(this.glassPane);
        this.glassPane.setVisible(true);
        this.glassPane.setEnabled(true);
        this.glassPane.setOpaque(false);
        frame.validate();
        
        this.spy = new Spyglass(frame);
        this.actuator = new LeBrigandActuator(frame);
        this.botManager = new BotManager(this.spy, this.actuator);
        this.df = new NewLeBrigandFrame(this.botManager);
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
        this.initialize(w);
    }

    @Override
    public void topLevelWindowDestroyed(Window w) {
        //msger.log("Window %s destroyed\n", w.toString());
    }
    
    public void start() {
        Bridge.logger.info("Starting bridge");
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
//        if (a.getActionCommand().equals(LeBrigandFrame.AC_INIT_VM)) {
//        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_START_BILGE_BOT)) {
//            try {
//                if (currentBot != null && currentBot.isAlive()) {
//                    currentBot.killBot();
//                } else {
//                    currentBot = new BilgeBot(spy, spy.updateGameState().getYPPFrame(), df);
//                    currentBot.start();
//                }
//            } catch (AWTException e) {
//                this.logger.log(Level.SEVERE, e.getMessage(), e);
//            }
//        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_INPUT)) {
//            WrappedYoApp app = WrappedYoApp.singleton;
//            JFrame frame = spy.yppFrame;
//            ComponentManager compMgr = new ComponentManager();
//            compMgr.setRoot(frame);
//            List<WeakReference<Component>> allComps = compMgr.findComponents(null);
//            for (WeakReference<Component> c: allComps) {
//                Bridge.logger.info(c.get().toString());
//            }
//
//            YoFrame yoframe = app.getYoFrame();
//            LeBrigandBindingManager mgr = new LeBrigandBindingManager();
//            mgr.setRootObject(yoframe);
//            mgr.buildMappings();
//            
//            LeBrigandBindingManager uiBindings = new LeBrigandBindingManager();
//            uiBindings.setRootObject(frame);
//            uiBindings.buildMappings();
//            
////            Bridge.logger.info("YoFrame class field size: "+mgr.classFieldMap.size());
////            Bridge.logger.info("YoFrame derivative map size: "+mgr.derivativesMap.size());
////            Bridge.logger.info("JFrame class field size: "+uiBindings.classFieldMap.size());
////            Bridge.logger.info("JFrame derivative map size: "+uiBindings.derivativesMap.size());
//
////			try {
////				if (currentBot != null && currentBot.isAlive()) {
////					currentBot.killBot();
////				} else {
////					currentBot = new TestBot(spy, spy.updateGameState().getYPPFrame(), df);
////					currentBot.start();
////				}
////			} catch (AWTException e) {
////				this.logger.log(Level.SEVERE, e.getMessage(), e);
////			}
//        } else if (a.getActionCommand().equals(LeBrigandFrame.AC_START_RIGGING_BOT)) {
//            try {
//                if (currentBot != null && currentBot.isAlive()) {
//                    currentBot.killBot();
//                } else {
//                    currentBot = new RiggingBot(spy, spy.updateGameState().getYPPFrame(), df);
//                    currentBot.start();
//                }
//            } catch (AWTException e) {
//                Bridge.logger.log(Level.SEVERE, e.getMessage(), e);
//            }
//        }
    }
}
