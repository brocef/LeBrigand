package lebrigand.bots.testing;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import lebrigand.bots.SpyglassBot;
import lebrigand.core.spyglass.Spyglass;
import lebrigand.core.ui.Messenger;

public class TestBot extends SpyglassBot {

	public TestBot(Spyglass spy, JFrame yppFrame, Messenger msger) throws AWTException {
		super(spy, yppFrame, msger);
	}

	@Override
	public String getBotName() {
		return "TestBot";
	}
	
	@Override
	public void run() {
		bringGameToFront();
		updateUIHooks();
		JFrame yppFrame = getYPPFrame();
		Container content = yppFrame.getContentPane();
//		for (int x=0; x<content.getWidth(); x+=25) {
//			for (int y=0; y<content.getHeight(); y+=25) {
//				Component c = SwingUtilities.getDeepestComponentAt(content, x, y);
//				log("Comp at (%d,%d) is %s", x, y, c.toString());
//			}
//		}

		Component root = content.getComponent(0);
		//		Point p = SwingUtilities.convertPoint(getLogonButton(), 0, 0, yppFrame.getContentPane());
//		log(p.toString());
//		JPasswordField pass = getLogonPassField();//dlp.password;
//		AccessibleJTextComponent access = (AccessibleJTextComponent) pass.getAccessibleContext();
//		access.insertTextAtIndex(0, "lurker");
//		
		JButton login = getLogonButton();
		Point p = SwingUtilities.convertPoint(login, 5, 5, content);
		mouseMove(p);
		mouseClick(MouseEvent.BUTTON1_DOWN_MASK);
//		login.doClick();
//		pass.setName("Password!");
//		pass.requestFocus();
	}

}
