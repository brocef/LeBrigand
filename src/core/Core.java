package lebrigand.core;

import java.awt.KeyboardFocusManager;

import com.melloware.jintellitype.JIntellitype;
import com.sun.java.accessibility.util.EventQueueMonitor;
import com.threerings.yohoho.client.YoApp;

public class Core {
	String[] args;
	Bridge bridge;

	public Core(String[] args) {
		bridge = new Bridge();

		EventQueueMonitor.addTopLevelWindowListener(bridge.getSpyglass());
		EventQueueMonitor.addGUIInitializedListener(bridge.getSpyglass());

		this.args = args;
	}

	public static void main(String[] args) {
		//This is returning the wrong value for some reason. Not sure why.
//		if (JIntellitype.checkInstanceAlreadyRunning("YPP Debugger")) {
//			System.err.println("Instance already running");
//			System.exit(1);
//		}

		if (!JIntellitype.isJIntellitypeSupported()) {
			System.err.println("Intellitype not supported");
			System.exit(1);
		}

		Core c = new Core(args);
		c.run(); //Not actually a new thread, just a convenient name for an instance main method
	}

	public void run() {
		bridge.start();
		
		System.setProperty("com.threerings.getdown" , "true");
		System.setProperty("java.library.path" , "C:\\Users\\yesman\\AppData\\Roaming\\Three Rings Design\\Puzzle Pirates\\./native");
		System.setProperty("resource_dir" , "C:\\Users\\yesman\\AppData\\Roaming\\Three Rings Design\\Puzzle Pirates\\./rsrc");
		System.setProperty("devclient", "false");
		System.setProperty("appdir" , "C:\\Users\\yesman\\AppData\\Roaming\\Three Rings Design\\Puzzle Pirates\\.");
		System.setProperty("sun.awt.font.advancecache" , "off");
		System.setProperty("sun.java2d.d3d" , "false");
		System.setProperty("swing.aatext" , "true");
		YoApp.main(args);//This right here is the magic. Calling the main method here will run YPP in our JVM. Fuck yeah.
		try {
			bridge.join();
		} catch (InterruptedException e) {
			//This shouldn't matter, if it even happens
		}
	}
}
