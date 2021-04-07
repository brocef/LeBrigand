package lebrigand.core.spyglass;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.JFrame;

import lebrigand.core.ui.Messenger;
import lebrigand.LeLogger;
import lebrigand.core.ui.GlassPane;

import com.sun.java.accessibility.util.GUIInitializedListener;
import com.sun.java.accessibility.util.TopLevelWindowListener;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.threerings.media.ManagedJFrame;


public class Spyglass implements TopLevelWindowListener, GUIInitializedListener {
	private static Logger logger = Logger.getLogger(Spyglass.class.getName());
	Messenger msger;

	private ArrayList<GameStateSubscriber> stateSubscribers;
	
	//Game state data/hooks
	VirtualMachine vm;
	GameState game;
	JFrame yppFrame;
	GlassPane glassPane;

	public Spyglass(Messenger msger) {
		vm = null;
		game = null;
		yppFrame = null;
		this.msger = msger;
		stateSubscribers = new ArrayList<GameStateSubscriber>();
	}

	public void bringGameToFront() {
		yppFrame.toFront();
		yppFrame.getContentPane().requestFocus();
	}
	
	public boolean isSpyglassReady() {
		return game != null;
	}
	
	public void initializeSpyglass() throws VMInitializationFailure {
		initVM();
	}
	
	protected void initVM() throws VMInitializationFailure {
		if (vm != null)
			return;
		
		VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
		List<AttachingConnector> cxns = vmm.attachingConnectors();
		AttachingConnector cxn;

		for (AttachingConnector c:cxns) {
			msger.log("%s --- %s\n", c.name(), c.toString());
			if (c.name().equalsIgnoreCase("com.sun.jdi.SocketAttach")) {
				cxn = c;
				Map<String, Argument> arg = cxn.defaultArguments();
				for (Entry<String, Argument> e:arg.entrySet())
					msger.log("%s - %s\n", e.getKey(), e.getValue().toString());
				arg.get("hostname").setValue("localhost");
				arg.get("port").setValue("6500");
				try {
					vm = cxn.attach(arg);
					break;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalConnectorArgumentsException e) {
					e.printStackTrace();
				}
				vm = null;
			}
		}
		if (vm == null) {
			msger.log("Fatal error. VM failed to initialize.");
			throw new VMInitializationFailure();
		} else if (yppFrame != null)
			this.intializeGameState();
	}

	public synchronized GameState updateGameState() {
		if (this.game != null)
			return this.game.updateGameState();
		return null;
	}

	public GameState getOldGameState() {
		return this.game;
	}

	private void intializeGameState() {
		this.game = new GameState(this, yppFrame, vm);
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
			if (vm != null)
				this.intializeGameState();
			LeLogger.setUpLogger();
			logger.info("Hooked into "+yppFrame.toString());
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
