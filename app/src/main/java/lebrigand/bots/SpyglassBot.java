package lebrigand.bots;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import lebrigand.core.spyglass.LeBrigandActuator;

import lebrigand.core.spyglass.Spyglass;

public abstract class SpyglassBot extends ActuatorBot {
	//Hide all the things from bots
	private Spyglass spy;
	
	public SpyglassBot(Spyglass spy, LeBrigandActuator actuator) {
		super(actuator);
		this.spy = spy;
		this.spy.updateGameState().setUpHooks();
	}
	
	public void updateHooks() {
		if (spy == null) return;
		spy.updateGameState().setUpHooks();
	}
	
	public void updateUIHooks() {
		if (spy == null) return;
		spy.updateGameState().updateUIHooks();
	}
	
	public JComponent getBilgeBoardView() {
		return spy.updateGameState().getBilgeBoardView();
	}
	
	public JPasswordField getLogonPassField() {
		return spy.updateGameState().getLogonPassField();
	}
	
	public JButton getLogonButton() {
		return spy.updateGameState().getLogonButton();
	}
	
	public JFrame getYPPFrame() {
		return spy.updateGameState().getYPPFrame();
	}
	
	public boolean isGameWindowFocused() {
		return spy.updateGameState().isGameWindowFocused();
	}
	
	public void bringGameToFront() {
		spy.bringGameToFront();
	}
	
	public boolean getRiggingKeyDrag() {
		return spy.updateGameState().getRiggingKeyDrag();
	}
	
	public Point getRiggingBoardViewLocation() {
		return spy.updateGameState().getRiggingBoardViewLocation();
	}
	
	public Point getBilgeBoardViewLocation() {
		return spy.updateGameState().getBilgeBoardViewLocation();
	}
	
	public int[] getBilgeBoard() {
		return spy.updateGameState().getBilgeBoard();
	}

	public boolean bilgeBoardIsActive() {
		return spy.updateGameState().getBilgeBoardIsActive();
	}

	public boolean bilgeBoardIsAnimating() {
		return spy.updateGameState().getBilgeBoardIsAnimating();
	}
	
	public boolean isTutorialPanelShowing() {
		return spy.updateGameState().isTutorialPanelShowing();
	}

	public Rectangle getTutorialPanelDismissArea() {
		return spy.updateGameState().getTutorialPanelDismissArea();
	}
	
	public String getRiggingPanelOverlay() {
		return spy.updateGameState().getReadibleRiggingPanelOverlay();
	}
	
	public boolean getRiggingBoardIsActive() {
		return spy.updateGameState().getRiggingBoardIsActive();
	}
	
	public int getRiggingCoilCount() {
		return spy.updateGameState().getRiggingCoilCount();
	}
	
	public int getRiggingStarProgress() {
		return spy.updateGameState().getRiggingStarProgress();
	}
	
	public boolean getRiggingBoardIsAnimating() {
		return spy.updateGameState().getRiggingBoardIsAnimating();
	}
	
	public int getRiggingCursorCol() {
		return spy.updateGameState().getRiggingCursorCol();
	}

	public int getRiggingCursorRow() {
		return spy.updateGameState().getRiggingCursorRow();
	}

	public int getRiggingPulley() {
		return spy.updateGameState().getRiggingPulley();
	}

	public int[][] getRiggingBoard() {
		return spy.updateGameState().getRiggingBoard();
	}
	
	public String getActivePlayingPanel() {
		return spy.updateGameState().getReadibleActivePlayingPanel();
	}

	public boolean dutyReportIsUp() {
		return spy.updateGameState().getDutyReportIsUp();
	}

	public int getWaterLevel() {
		return spy.updateGameState().getWaterLevel();
	}
}
