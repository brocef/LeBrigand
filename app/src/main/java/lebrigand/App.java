package lebrigand;

import java.awt.KeyboardFocusManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import lebrigand.core.Bridge;

//import com.melloware.jintellitype.JIntellitype;
import com.sun.java.accessibility.util.EventQueueMonitor;
import com.threerings.yohoho.client.YoApp;

public class App {
	String[] args;
	Bridge bridge;

	public App(String[] args) {
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

        /*
		if (!JIntellitype.isJIntellitypeSupported()) {
			System.err.println("Intellitype not supported");
			System.exit(1);
		}*/
		App c = new App(args);
		c.run(); //Not actually a new thread, just a convenient name for an instance main method
	}

	public void run() {
		bridge.start();
		
		Properties p = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream("jvm.prop");
            p.load(in);
            for (String s : p.stringPropertyNames()) {
                System.setProperty(s, p.getProperty(s));
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
		}

		YoApp.main(args);//This right here is the magic. Calling the main method here will run YPP in our JVM. Fuck yeah.
		try {
			bridge.join();
		} catch (InterruptedException e) {
			//This shouldn't matter, if it even happens
		}
	}
}
