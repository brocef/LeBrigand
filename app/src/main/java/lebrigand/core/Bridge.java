package lebrigand.core;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import java.util.logging.Level;

import lebrigand.LeLogger;
import lebrigand.bots.SpyglassBot;
import lebrigand.bots.bilge.BilgeBot;
import lebrigand.bots.rigging.RiggingBot;
import lebrigand.bots.testing.TestBot;
import lebrigand.core.spyglass.Spyglass;
import lebrigand.core.spyglass.VMInitializationFailure;
import lebrigand.core.ui.LeBrigandFrame;

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
		this.logger.info("ActionPerformed: "+a.toString());
		if (a.getActionCommand().equals(LeBrigandFrame.AC_INIT_VM)) {
			this.logger.info("Initializing VM");
			try {
				if (!spy.isSpyglassReady())
					spy.initializeSpyglass();
			} catch(VMInitializationFailure e) {
				this.logger.log(Level.SEVERE, "Fatal Error: VM Failed to initialize!");
				this.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} else if (a.getActionCommand().equals(LeBrigandFrame.AC_START_BILGE_BOT)) {
			try {
				if (currentBot != null && currentBot.isAlive())
					currentBot.killBot();
				else {
					currentBot = new BilgeBot(spy, spy.updateGameState().getYPPFrame(), df);
					currentBot.start();
				}
			} catch (AWTException e) {
				this.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} else if (a.getActionCommand().equals(LeBrigandFrame.AC_INPUT)) {
			try {
				if (currentBot != null && currentBot.isAlive()) {
					currentBot.killBot();
				} else {
					currentBot = new TestBot(spy, spy.updateGameState().getYPPFrame(), df);
					currentBot.start();
				}
			} catch (AWTException e) {
				this.logger.log(Level.SEVERE, e.getMessage(), e);
			}
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
