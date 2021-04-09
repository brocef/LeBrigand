package lebrigand;

// import com.samskivert.swing.b.k;
// import com.samskivert.util.Z;
// import com.samskivert.util.aP;
// import com.samskivert.util.aQ;
// import com.samskivert.util.aU;
// import com.samskivert.util.c;
import com.threerings.froth.SteamAPI;
import com.threerings.media.ManagedJFrame;
import com.threerings.media.h;
// import com.threerings.media.util.c;
import com.threerings.piracy.client.ClientPrefs;
// import com.threerings.piracy.client.h;
// import com.threerings.piracy.client.n;
import com.threerings.piracy.swing.plaf.YoLookAndFeel;
// import com.threerings.yohoho.a;
import java.awt.EventQueue;
import com.samskivert.util.Logger;
import javax.swing.RootPaneContainer;
// import org.apache.commons.io.c;
import com.threerings.yohoho.client.YoApp;
import com.threerings.yohoho.client.YoFrame;
import com.threerings.yohoho.client.aE;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import javax.swing.JFrame;
import com.threerings.yohoho.client.YoFrame;

public class WrappedYoApp extends YoApp implements Runnable {

    public static WrappedYoApp singleton = null;

    public YoFrame getYoFrame() {
        try {
            Field yoapp_d = null; //YoApp.class.getField("d");
            for (Field f : YoApp.class.getDeclaredFields()) {
                if (f.getName() == "b") {
                    yoapp_d = f;
                    break;
                }
            }
            yoapp_d.setAccessible(true);
            return (YoFrame) yoapp_d.get(this.singleton);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(WrappedYoApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WrappedYoApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] paramArrayOfString) {
        String str = "";
        String arrayOfString[] = paramArrayOfString;
        Logger logger = Logger.getLogger("yohoho");
        WrappedYoApp yoApp = new WrappedYoApp();
        singleton = yoApp;
        try {
            ManagedJFrame managedJFrame;
            String str1 = str;
            arrayOfString = null;
            WrappedYoApp yoApp1 = yoApp;
            if (!SteamAPI.init()) {
                logger.info("Did not initialize Steam", new Object[0]);
            }
            if (!ClientPrefs.getAntialiasFontsEnabled()) {
                System.setProperty("swing.aatext", "false");
            }
            if (ClientPrefs.getD3DEnabled()) {
                System.setProperty("sun.java2d.d3d", "true");
            }
            if (ClientPrefs.getDPIAwareEnabled()) {
                System.setProperty("sun.java2d.dpiaware", "true");
            }
            logger.info("DPI Aware: " + System.getProperty("sun.java2d.dpiaware"), new Object[0]);
            YoLookAndFeel.a(yoApp1.getClass().getClassLoader());
            managedJFrame = managedJFrame = new ManagedJFrame();
            yoApp1.b = new YoFrame((RootPaneContainer) managedJFrame);
            yoApp1.c = h.a(yoApp1.b);
            Field yoapp_d = null; //YoApp.class.getField("d");
            for (Field f : YoApp.class.getDeclaredFields()) {
                if (f.getName() == "d") {
                    yoapp_d = f;
                    break;
                }
            }
            yoapp_d.setAccessible(true);
            yoapp_d.set(yoApp1, new aE(yoApp1.c, yoApp1.b, str1));
            //   yoApp1.d = new aE(yoApp1.c, yoApp1.b, str1);
            Method yoapp_methods[] = YoApp.class.getDeclaredMethods();
            Method yoapp_c = null;
            for (Method m : yoapp_methods) {
                logger.info("Found method " + m.getName());
                if (m.getName() == "c") {
                    yoapp_c = m;
                    break;
                }
            }
            yoapp_c.setAccessible(true);
            yoapp_c.invoke(yoApp1);
            //   YoApp.c();
        } catch (Exception iOException) {
            logger.warning("Error initializing application.", new Object[]{iOException});
        }
        EventQueue.invokeLater(yoApp);
    }
}
