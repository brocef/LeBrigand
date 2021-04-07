package lebrigand.core.spyglass;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import lebrigand.bots.rigging.RiggingUtils;
import lebrigand.core.spyglass.hooks.ArrayHook;
import lebrigand.core.spyglass.hooks.BooleanHook;
import lebrigand.core.spyglass.hooks.Hook;
import lebrigand.core.spyglass.hooks.IntArrayHook;
import lebrigand.core.spyglass.hooks.IntegerHook;
import lebrigand.core.spyglass.hooks.ObjectRefHook;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.VirtualMachine;


public class GameState {
	private static Logger logger = Logger.getLogger(GameState.class.getName());
	//Critical hook information

	private static final String CLASSNAME_TUTORIAL_PANEL = "com.threerings.piracy.puzzle.client.TutorialPanel";
	private static final String TUTORIAL_PANEL_FIELDS[] = {"_dismiss"};

	private static final String CLASSNAME_YO_USER_OBJECT = "com.threerings.piracy.data.YoUserObject";
	private static final String YO_USER_OJBECT_FIELDS[] = {"gameLoc"};

	private static final String CLASSNAME_DEFAULT_LOGON_PANEL = "com.threerings.yohoho.client.logon.DefaultLogonPanel";

	private static final String CLASSNAME_DUTY_REPORT_VIEW = "com.threerings.yohoho.sea.vessel.client.DutyReportView";
	private static final String DUTY_REPORT_VIEW_FIELDS[] = {"width", "height", "isAddNotifyComplete"};

	private static final String CLASSNAME_BILGE_BOARD_VIEW = "com.threerings.piracy.puzzle.duty.bilge.BilgeBoardView";
	private static final String BILGE_BOARD_VIEW_FIELDS[] = {"_actionSprites", "_actionAnims", "_waterLevel",
		"_actionSprites.size", "_actionAnims.size"};

	private static final String CLASSNAME_BILGE_BOARD = "com.threerings.piracy.puzzle.duty.bilge.BilgeBoard";
	private static final String BILGE_BOARD_FIELDS[] = {"_board"};

	private static final String CLASSNAME_BILGE_PANEL = "com.threerings.piracy.puzzle.duty.bilge.BilgePanel";

	private static final String CLASSNAME_PLAYING_PANEL = "com.threerings.piracy.roister.client.PlayingPanel";
	private static final String PLAYING_PANEL_FIELDS[] = {"_primary"};

	private static final String CLASSNAME_RIGGING_BOARD = "com.threerings.piracy.puzzle.duty.rigging.data.RiggingBoard";
	private static final String RIGGING_BOARD_FIELDS[] = {"_pieces", "_nextPulley"};

	private static final String CLASSNAME_RIGGING_BOARD_VIEW = "com.threerings.piracy.puzzle.duty.rigging.client.RiggingBoardView";
	private static final String RIGGING_BOARD_VIEW_FIELDS[] = {"_curs", "_curs.a", "_curs.b", "_animQueue", "_animQueue.f",
		"_animQueue.f.size", "_wisps", "_wisps.size", "_nextCacheSpot", "_keyDrag"};

	private static final String CLASSNAME_RIGGING_PANEL = "com.threerings.piracy.puzzle.duty.rigging.client.RiggingPanel";
	private static final String RIGGING_PANEL_FIELDS[] = {"_opanel" /*Will be DutyReportView when DRV is up*/,
		"stars", "stars.n", "stars.m", "stars.l", "stars.k", "_controller"};

	private static final String CLASSNAME_RIGGING_CONTROLLER = "com.threerings.piracy.puzzle.duty.rigging.client.RiggingController";
	private static final String RIGGING_CONTROLLER_FIELDS[] = {"H", "j_", "l_"};
	
	private static final String CLASSNAME_DUTY_PERFORMANCE = "com.threerings.piracy.puzzle.duty.util.DutyPerformance";
	private static final String DUTY_PERFORMANCE_FIELDS[] = {"_moves", "_score", "_ticks", "BUCKETS"};

	private Spyglass spy;

	private VirtualMachine vm;

	//	//OTHER VARS
	//	private JFrame yppFrame;
	//	private JButton logonButton;
	//	private JTextField logonNameField;
	//	private JPasswordField logonPassField;
	//
	//	//GENERIC PUZZLE VARS
	//	//	private JPanel dutyReportView;
	//	private IntegerHook dutyReportViewWidthHook, dutyReportViewHeightHook;	private int dutyReportViewWidth, dutyReportViewHeight;
	//	private BooleanHook isAddNotifyCompleteHook;							private boolean dutyReportViewIsUp, isAddNotifyComplete;
	//	private IntegerHook bilgeActionAnimsHook, bilgeActionSpritesHook;		private int bilgeActionAnims, bilgeActionSprites;
	//	private ObjectRefHook playingPanelHook;									private String activePlayingPanel, activePlayingPanelShort;
	//
	//	//BILGING VARS
	//	private JComponent bilgeBoardView;
	//	private boolean bilgeBoardIsActive;
	//	private boolean bilgeBoardIsAnimating;
	//
	//	private IntArrayHook bilgeBoardHook;			private int[] bilgeBoard;
	//	private IntegerHook waterLevelHook;				private int waterLevel;
	//
	//	//RIGGING VARS
	//	private boolean riggingBoardIsActive;
	//	private ArrayHook riggingBoardHook;							private int[][] riggingBoard;
	//	private IntegerHook riggingCurRowHook, riggingCurColHook;	private int riggingCurRow, riggingCurCol;
	//	private IntegerHook riggingPulleyHook;						private int riggingPulley;

	private HashMap<String, Hook> hookMap;

	//OTHER VARS
	private JFrame yppFrame;
	private JButton logonButton;
	private JTextField logonNameField;
	private JPasswordField logonPassField;

	//USER VARS
	private ObjectRefHook yoUserObjectHook;
	private IntegerHook yoUserObjectGameLocHook;

	//GENERIC PUZZLE VARS
	private JPanel tutorialPanel;
	private JButton tutorialPanelDismiss;

	//	private JPanel dutyReportView;
	private ObjectRefHook dutyReportViewHook;
	private IntegerHook dutyReportViewWidthHook, dutyReportViewHeightHook;
	private BooleanHook isAddNotifyCompleteHook;
	private ObjectRefHook playingPanelHook, primaryPlayingPanelHook;

	//BILGING VARS
	private JComponent bilgeBoardView;
	private ObjectRefHook bilgeBoardViewHook, bilgeBoardHook;
	private ObjectRefHook bilgeActionAnimsHook, bilgeActionSpritesHook;
	private IntArrayHook bilgeBoardArrHook;
	private IntegerHook waterLevelHook, bilgeActionAnimsSizeHook, bilgeActionSpritesSizeHook;

	//RIGGING VARS
	private WeakReference<JComponent> riggingBoardView;
	private ObjectRefHook riggingBoardViewHook, riggingBoardHook;
	private ArrayHook riggingBoardArrHook;
	private ObjectRefHook riggingCurHook;
	private IntegerHook riggingCurRowHook, riggingCurColHook;
	private IntegerHook riggingPulleyHook;
	private ObjectRefHook riggingAnimQueueHook;
	private ObjectRefHook riggingAnimQueueFHook;
	private IntegerHook riggingAnimQueueFSizeHook;
	private ObjectRefHook riggingWispsHook;
	private IntegerHook riggingWispsSizeHook;
	private IntegerHook riggingNextCacheSpotHook;
	private BooleanHook riggingKeyDragHook;
	private ObjectRefHook riggingControllerHook;
	private ObjectRefHook riggingControllerHHook;
	private ObjectRefHook riggingControllerjHook;
	private ObjectRefHook riggingControllerlHook;

	//RIGGING BOARD VARS
	private ObjectRefHook riggingPanelHook;
	private ObjectRefHook riggingPanelStarsHook, riggingPanelOPanelHook;
	private IntegerHook riggingPanelStarsNHook, riggingPanelStarsMHook, riggingPanelStarsLHook, riggingPanelStarsKHook;

	// DUTY PERFORMANCE VARS
	private ObjectRefHook dutyPerformanceHook;
	private IntArrayHook dutyPerformanceMovesHook;
	private IntArrayHook dutyPerformanceScoreHook;
	private IntegerHook dutyPerformanceTicksHook;
	private IntegerHook dutyPerformanceBucketsHook;

	public GameState(Spyglass spy, JFrame yppFrame, VirtualMachine vm) {
		this.yppFrame = yppFrame;
		this.vm = vm;
		this.spy = spy;

		hookMap = new HashMap<String, Hook>();
		setUpHooks();
	}

	//  !!Trying to not expose hooks to game components, lest a bot dev fucks with them!!
	public JFrame getYPPFrame() {
		return yppFrame;
	}

	//  !!Trying to not expose hooks to game components, lest a bot dev fucks with them!!
	public JComponent getBilgeBoardView() {
		updateUIHooks();
		return bilgeBoardView;
	}

	public void updateUIHooks() {
		if (yppFrame.getContentPane().getComponentCount() > 0)
			diveIntoPanel((JPanel) yppFrame.getContentPane().getComponent(0)); //Next, we get the UI hooks
		else
			logger.log(Level.SEVERE, "Critical failure to retreive window components");
	}

	public JButton getLogonButton() {
		updateUIHooks();
		return logonButton;
	}

	public JTextField getLogonNameField() {
		updateUIHooks();
		return logonNameField;
	}

	public JPasswordField getLogonPassField() {
		updateUIHooks();
		return logonPassField;
	}

	public boolean isTutorialPanelShowing() {
		updateUIHooks();
		return tutorialPanel != null && tutorialPanel.isShowing();
	}
	
	public Rectangle getTutorialPanelDismissArea() {
		updateUIHooks();
		Point tl = tutorialPanelDismiss.getLocation();
		return new Rectangle(tl.x, tl.y, tl.x + tutorialPanelDismiss.getWidth(), tl.y + tutorialPanelDismiss.getY());
	}

	public Point getBilgeBoardViewLocation() {
		updateUIHooks();
		return bilgeBoardView.getLocation();
	}

	public Point getRiggingBoardViewLocation() {
		JComponent cachedBoardView = this.riggingBoardView.get();
		if (cachedBoardView == null) {
			updateUIHooks();
		}
		JComponent boardView = this.riggingBoardView.get();
		return boardView.getLocation();
	}

	public int[] getBilgeBoard() {
		//		if (bilgeBoardHook == null)
		return bilgeBoardArrHook.getIntArray(true);
	}

	//  !!Trying to not expose hooks to game components, lest a bot dev fucks with them!!
	//	public JPanel getDutyReportView() {
	//		return dutyReportView;
	//	}

	public boolean getBilgeBoardIsActive() {
		return getActivePlayingPanel() == CLASSNAME_BILGE_PANEL;
	}

	public boolean getBilgeBoardIsAnimating() {
		return getBilgeActionSprites() > 0 || getBilgeActionAnims() > 0;
	}

	public boolean getRiggingKeyDrag() {
		return riggingKeyDragHook.getValue();
	}
	
	public String getRiggingPanelOverlay() {
		return riggingPanelOPanelHook.getTypeName();
	}

	public String getReadibleRiggingPanelOverlay() {
		String s = getRiggingPanelOverlay();
		if (s == null)
			return "n/a";
		return s.substring(s.lastIndexOf(".")+1);
	}

	public int getRiggingCoilCount() {
		return riggingNextCacheSpotHook.getValue();
	}

	//Number of stars?
	public int getRiggingPanelStarsN() {
		return riggingPanelStarsNHook.getValue();
	}

	//Progress, [0,100]
	public int getRiggingPanelStarsM() {
		return riggingPanelStarsMHook.getValue();
	}

	//Star capacity/value?
	public int getRiggingPanelStarsL() {
		return riggingPanelStarsLHook.getValue();
	}

	//Progress, [0,100]
	public int getRiggingPanelStarsK() {
		return riggingPanelStarsKHook.getValue();
	}

	public int getRiggingStarProgress() {
		return getRiggingPanelStarsK();
	}

	public boolean getRiggingBoardIsActive() {
		return getActivePlayingPanel() == CLASSNAME_RIGGING_PANEL;
	}

	public int getRiggingWisps() {
		return riggingWispsSizeHook.getValue();
	}

	public boolean getRiggingBoardIsAnimating() {
		return getRiggingActionAnims() > 0;
	}

	public int getRiggingActionAnims() {
		return riggingAnimQueueFSizeHook.getValue();
	}

	public int getRiggingCursorCol() {
		return riggingCurColHook.getValue();
	}

	public int getRiggingCursorRow() {
		return riggingCurRowHook.getValue();
	}

	public int getRiggingPulley() {
		return riggingPulleyHook.getValue();
	}

	public int[][] getRiggingBoard() {
		ArrayReference piecesarr = riggingBoardArrHook.getValue();

		int[][] riggingBoard = new int[RiggingUtils.BOARD_ROWS][];
		for (int i=0; i<RiggingUtils.BOARD_ROWS; i++)
			riggingBoard[i] = new int[RiggingUtils.BOARD_AXIS_LENS[i]];

		for (int i=0; i<RiggingUtils.BOARD_ROWS; i++) {
			ArrayReference row = (ArrayReference) piecesarr.getValue(i);
			for (int j=0; j<RiggingUtils.BOARD_AXIS_LENS[i]; j++) {
				riggingBoard[i][j] = ((IntegerValue) row.getValue(j)).intValue();
			}
		}

		return riggingBoard;
	}

	public String getActivePlayingPanel() {
		return primaryPlayingPanelHook.getTypeName();
	}

	public String getReadibleActivePlayingPanel() {
		String s = getActivePlayingPanel();
		if (s == null)
			return "n/a";
		return s.substring(s.lastIndexOf(".")+1);
	}

	public int getBilgeActionSprites() {
		return bilgeActionSpritesSizeHook.getValue();
	}

	public int getBilgeActionAnims() {
		return bilgeActionAnimsSizeHook.getValue();
	}

	public boolean isAddNotifyComplete() {
		return isAddNotifyCompleteHook.getValue();
	}

	public int getDutyReportViewWidth() {
		return dutyReportViewWidthHook.getValue();
	}

	public int getDutyReportViewHeight() {
		return dutyReportViewHeightHook.getValue();
	}

	public boolean getDutyReportIsUp() {
		return isAddNotifyComplete() && getDutyReportViewWidth() > 0 && getDutyReportViewHeight() > 0;
	}

	public int[] getDutyPerformanceScore() {
		return this.dutyPerformanceScoreHook.getIntArray();
	}

	public int[] getDutyPerformanceMoves() {
		return this.dutyPerformanceMovesHook.getIntArray();
	}

	public int getDutyPerformanceTicks() {
		return this.dutyPerformanceTicksHook.getValue();
	}

	public int getDutyPerformanceBuckets() {
		return this.dutyPerformanceBucketsHook.getValue();
	}

	public int getWaterLevel() {
		return waterLevelHook.getValue();
	}

	private void populateLogonPanelHooks(JPanel logonPanel) {
		Component[] comps = logonPanel.getComponents();
		logonButton = null;
		logonNameField = null;
		logonPassField = null;
		for (Component c:comps) {
			if (c instanceof JButton) {
				if (((JButton) c).getText().equalsIgnoreCase("Logon"))
					logonButton = (JButton) c;
			} else if (c instanceof JTextField) {
				logonNameField = (JTextField) c;
			} else if (c instanceof JPasswordField) {
				logonPassField = (JPasswordField) c;
			}
		}
	}

	private void getDismissButtion(JPanel tut) {
		Component[] comps = tut.getComponents();
		for (Component c:comps) {
			if (c instanceof JButton) {
				JButton btn = (JButton) c;
				if (btn.getText().equalsIgnoreCase("Play!")) {
					tutorialPanelDismiss = btn;
					break;
				}
			}
			if (c instanceof JPanel)
				getDismissButtion((JPanel) c);
		}
	}

	private void diveIntoPanel(JPanel panel) {
		Component[] comps = panel.getComponents();
		for (Component c:comps) {
			String classname = c.getClass().getName();
			if (classname.equalsIgnoreCase(CLASSNAME_BILGE_BOARD_VIEW)) {
				bilgeBoardView = (JComponent) c;
			} else if (classname.equalsIgnoreCase(CLASSNAME_RIGGING_BOARD_VIEW)) {
				riggingBoardView = new WeakReference<JComponent>((JComponent) c);
			} else if (classname.equalsIgnoreCase(CLASSNAME_DEFAULT_LOGON_PANEL)) {
				populateLogonPanelHooks((JPanel) c);
			} else if (classname.equals(CLASSNAME_TUTORIAL_PANEL)) {
				tutorialPanel = (JPanel) c;
				getDismissButtion(tutorialPanel);
			}
			if (c instanceof JButton) {
				if (((JButton) c).getText().equals("Logon"))
					logonButton = (JButton) c;
			} else if (c instanceof JPasswordField)
				logonPassField = (JPasswordField) c;
			else if (c instanceof JTextField) {
				if (c.getX() == 94 && c.getY() == 2 && c.getWidth() == 139 && c.getHeight() == 19)
					logonNameField = (JTextField) c;
			}
			if (c instanceof JPanel)
				diveIntoPanel((JPanel) c);
		}
	}

	public boolean isGameWindowFocused() {
		return yppFrame.isFocused();
	}

	public void bringYPPFrameToFront() {
		yppFrame.toFront();
	}

	private ObjectRefHook getRegisteredObjRefHook(String className, String fieldName) {
		return getRegisteredObjRefHook(String.format("%s.%s", className, fieldName));
	}

	private ObjectRefHook getRegisteredObjRefHook(String lookupName) {
		return (ObjectRefHook) hookMap.get(lookupName);
	}

	private ObjectRefHook registerNewObjRefHook(String className, String fieldName) {
		ObjectRefHook hook;
		if (fieldName.contains(".")) {
			int lastPeriod = fieldName.lastIndexOf(".");
			String truncatedField = fieldName.substring(0, lastPeriod);
			ObjectRefHook owner = getRegisteredObjRefHook(className, truncatedField);
			hook = new ObjectRefHook(vm, owner, fieldName.substring(lastPeriod+1));
		} else {
			ObjectRefHook owner = getRegisteredObjRefHook(className);
			hook = new ObjectRefHook(vm, owner, fieldName);
		}
		String hookName = String.format("%s.%s", className, fieldName);
		hookMap.put(hookName, hook);
		this.logger.info("Added new hook: "+hookName);
		return hook;
	}

	private ObjectRefHook registerNewObjRefHook(String className) {
		ObjectRefHook hook = new ObjectRefHook(vm, className);
		hookMap.put(String.format("%s", className), hook);
		return hook;
	}

	private IntegerHook registerNewIntHook(String className, String fieldName) {
		IntegerHook hook;
		if (fieldName.contains(".")) {
			int lastPeriod = fieldName.lastIndexOf(".");
			String truncatedField = fieldName.substring(0, lastPeriod);
			ObjectRefHook owner = getRegisteredObjRefHook(className, truncatedField);
			hook = new IntegerHook(vm, owner, fieldName.substring(lastPeriod+1));
		} else {
			ObjectRefHook owner = getRegisteredObjRefHook(className);
			hook = new IntegerHook(vm, owner, fieldName);
		}
		hookMap.put(String.format("%s.%s", className, fieldName), hook);
		return hook;
	}

	private BooleanHook registerNewBoolHook(String className, String fieldName) {
		BooleanHook hook;
		if (fieldName.contains(".")) {
			int lastPeriod = fieldName.lastIndexOf(".");
			String truncatedField = fieldName.substring(0, lastPeriod);
			ObjectRefHook owner = getRegisteredObjRefHook(className, truncatedField);
			hook = new BooleanHook(vm, owner, fieldName.substring(lastPeriod+1));
		} else {
			ObjectRefHook owner = getRegisteredObjRefHook(className);
			hook = new BooleanHook(vm, owner, fieldName);
		}
		hookMap.put(String.format("%s.%s", className, fieldName), hook);
		return hook;
	}

	private ArrayHook registerNewArrayHook(String className, String fieldName, boolean refresh) {
		ArrayHook hook;
		if (fieldName.contains(".")) {
			int lastPeriod = fieldName.lastIndexOf(".");
			String truncatedField = fieldName.substring(0, lastPeriod);
			ObjectRefHook owner = getRegisteredObjRefHook(className, truncatedField);
			hook = new ArrayHook(vm, owner, fieldName.substring(lastPeriod+1), refresh);
		} else {
			ObjectRefHook owner = getRegisteredObjRefHook(className);
			hook = new ArrayHook(vm, owner, fieldName, refresh);
		}
		hookMap.put(String.format("%s.%s", className, fieldName), hook);
		return hook;
	}

	private IntArrayHook registerNewIntArrHook(String className, String fieldName) {
		return registerNewIntArrHook(className, fieldName, false);
	}

	private IntArrayHook registerNewIntArrHook(String className, String fieldName, boolean refresh) {
		IntArrayHook hook;
		if (fieldName.contains(".")) {
			int lastPeriod = fieldName.lastIndexOf(".");
			String truncatedField = fieldName.substring(0, lastPeriod);
			ObjectRefHook owner = getRegisteredObjRefHook(className, truncatedField);
			hook = new IntArrayHook(vm, owner, fieldName.substring(lastPeriod+1), refresh);
		} else {
			ObjectRefHook owner = getRegisteredObjRefHook(className);
			hook = new IntArrayHook(vm, owner, fieldName, refresh);
		}
		hookMap.put(String.format("%s.%s", className, fieldName), hook);
		return hook;
	}

	protected void setUpRiggingBoardHooks() {
		String className = CLASSNAME_RIGGING_BOARD;
		riggingBoardHook = registerNewObjRefHook(className);
		for (int i=0; i<RIGGING_BOARD_FIELDS.length; i++) {
			String fieldName = RIGGING_BOARD_FIELDS[i];
			switch (i) {
			case 0:
				riggingBoardArrHook = registerNewArrayHook(className, fieldName, true);
				break;
			case 1:
				riggingPulleyHook = registerNewIntHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpRiggingBoardViewHooks() {
		String className = CLASSNAME_RIGGING_BOARD_VIEW;
		riggingBoardViewHook = registerNewObjRefHook(className);
		for (int i=0; i<RIGGING_BOARD_VIEW_FIELDS.length; i++) {
			String fieldName = RIGGING_BOARD_VIEW_FIELDS[i];
			switch (i) {
			case 0:
				riggingCurHook = registerNewObjRefHook(className, fieldName);
				break;
			case 1:
				riggingCurColHook = registerNewIntHook(className, fieldName);
				break;
			case 2:
				riggingCurRowHook = registerNewIntHook(className, fieldName);
				break;
			case 3:
				riggingAnimQueueHook = registerNewObjRefHook(className, fieldName);
				break;
			case 4:
				riggingAnimQueueFHook = registerNewObjRefHook(className, fieldName);
				break;
			case 5:
				riggingAnimQueueFSizeHook = registerNewIntHook(className, fieldName);
				break;
			case 6:
				riggingWispsHook = registerNewObjRefHook(className, fieldName);
				break;
			case 7:
				riggingWispsSizeHook = registerNewIntHook(className, fieldName);
				break;
			case 8:
				riggingNextCacheSpotHook = registerNewIntHook(className, fieldName);
				break;
			case 9:
				riggingKeyDragHook = registerNewBoolHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpRiggingPanelHooks() {
		String className = CLASSNAME_RIGGING_PANEL;
		riggingPanelHook = registerNewObjRefHook(className);
		for (int i=0; i<RIGGING_PANEL_FIELDS.length; i++) {
			String fieldName = RIGGING_PANEL_FIELDS[i];
			switch(i) {
			case 0:
				riggingPanelOPanelHook = registerNewObjRefHook(className, fieldName);
				break;
			case 1:
				riggingPanelStarsHook = registerNewObjRefHook(className, fieldName);
				break;
			case 2:
				riggingPanelStarsNHook = registerNewIntHook(className, fieldName);
				break;
			case 3:
				riggingPanelStarsMHook = registerNewIntHook(className, fieldName);
				break;
			case 4:
				riggingPanelStarsLHook = registerNewIntHook(className, fieldName);
				break;
			case 5:
				riggingPanelStarsKHook = registerNewIntHook(className, fieldName);
				break;
			case 6:
				riggingControllerHook = registerNewObjRefHook(className, fieldName);
				hookMap.put(CLASSNAME_RIGGING_CONTROLLER, riggingControllerHook);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpRiggingControllerHooks() {
		String className = CLASSNAME_RIGGING_CONTROLLER;
		for (int i=0; i<RIGGING_CONTROLLER_FIELDS.length; i++) {
			String fieldName = RIGGING_CONTROLLER_FIELDS[i];
			switch(i) {
			case 0:
				riggingControllerHHook = registerNewObjRefHook(className, fieldName);
				break;
			case 1:
				riggingControllerjHook = registerNewObjRefHook(className, fieldName);
				break;
			case 2:
				riggingControllerlHook = registerNewObjRefHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpPlayingPanelHooks() {
		String className = CLASSNAME_PLAYING_PANEL;
		playingPanelHook = registerNewObjRefHook(className);
		for (int i=0; i<PLAYING_PANEL_FIELDS.length; i++) {
			String fieldName = PLAYING_PANEL_FIELDS[i];
			switch (i) {
			case 0:
				primaryPlayingPanelHook = registerNewObjRefHook(className, fieldName);
				break;
			}
		}
	}

	protected void setUpBilgeBoardHooks() {
		String className = CLASSNAME_BILGE_BOARD;
		bilgeBoardHook = registerNewObjRefHook(className);
		for (int i=0; i<BILGE_BOARD_FIELDS.length; i++) {
			String fieldName = BILGE_BOARD_FIELDS[i];
			switch (i) {
			case 0:
				bilgeBoardArrHook = registerNewIntArrHook(className, fieldName, true);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpBilgeBoardViewHooks() {
		String className = CLASSNAME_BILGE_BOARD_VIEW;
		bilgeBoardViewHook = registerNewObjRefHook(className);
		for (int i=0; i<BILGE_BOARD_VIEW_FIELDS.length; i++) {
			String fieldName = BILGE_BOARD_VIEW_FIELDS[i];
			switch (i) {
			case 0:
				bilgeActionSpritesHook = registerNewObjRefHook(className, fieldName);
				break;
			case 1:
				bilgeActionAnimsHook = registerNewObjRefHook(className, fieldName);
				break;
			case 2:
				waterLevelHook = registerNewIntHook(className, fieldName);
				break;
			case 3:
				bilgeActionSpritesSizeHook = registerNewIntHook(className, fieldName);
				break;
			case 4:
				bilgeActionAnimsSizeHook = registerNewIntHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpDutyReportViewHooks() {
		String className = CLASSNAME_DUTY_REPORT_VIEW;
		dutyReportViewHook = registerNewObjRefHook(className);
		for (int i=0; i<DUTY_REPORT_VIEW_FIELDS.length; i++) {
			String fieldName = DUTY_REPORT_VIEW_FIELDS[i];
			switch (i) {
			case 0:
				dutyReportViewWidthHook = registerNewIntHook(className, fieldName);
				break;
			case 1:
				dutyReportViewHeightHook = registerNewIntHook(className, fieldName);
				break;
			case 2:
				isAddNotifyCompleteHook = registerNewBoolHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	protected void setUpDutyPerformanceHooks() {
		String className = CLASSNAME_DUTY_PERFORMANCE;
		dutyPerformanceHook = registerNewObjRefHook(className);
		for (int i=0; i<DUTY_PERFORMANCE_FIELDS.length; i++) {
			String fieldName = DUTY_PERFORMANCE_FIELDS[i];
			switch (i) {
			case 0:
				dutyPerformanceMovesHook = registerNewIntArrHook(className, fieldName);
				break;
			case 1:
				dutyPerformanceScoreHook = registerNewIntArrHook(className, fieldName);
				break;
			case 2:
				dutyPerformanceTicksHook = registerNewIntHook(className, fieldName);
				break;
			case 3:
				dutyPerformanceBucketsHook = registerNewIntHook(className, fieldName);
				break;
			default:
				throw new RuntimeException("Failed to register all hooks!");
			}
		}
	}

	public void setUpHooks() {
		try {
			setUpDutyReportViewHooks();
			setUpBilgeBoardViewHooks();
			setUpBilgeBoardHooks();
			setUpRiggingBoardViewHooks();
			setUpRiggingBoardHooks();
			setUpPlayingPanelHooks();
			setUpRiggingPanelHooks();
			setUpRiggingControllerHooks();
			setUpDutyPerformanceHooks();
		} catch (Exception ex) {
			this.logger.log(Level.SEVERE, "Failed to initialize all game hooks!", ex);
		}
	}

	public void paint(Graphics g, Container glassPane) {
		Component btn = (Component) this.getLogonButton();
		if (btn != null) {
			this.drawDebugBox(g, glassPane, btn, "Logon Button");
		}
		if (this.tutorialPanel != null) {
			this.drawDebugBox(g, glassPane, this.tutorialPanel, "Tutorial Panel");
		}
		if (this.tutorialPanelDismiss != null) {
			this.drawDebugBox(g, glassPane, this.tutorialPanelDismiss, "Tutorial Panel Dismiss");
		}
		if (this.riggingBoardView.get() != null) {
			this.drawDebugBox(g, glassPane, this.riggingBoardView.get(), "Rigging Board View");
			Color orig = g.getColor();
			g.setColor(Color.RED);
			String score = "Rigging score: ";
			int[] score_arr = this.getDutyPerformanceScore();
			if (score_arr == null) {
				score += "NULL";
			} else {
				for (int i=0; i<score_arr.length; i++) {
					score += score_arr[i]+",";
				}
			}
			g.drawString(score, 20, 100);
			String moves = "Rigging moves: ";
			int[] moves_arr = this.getDutyPerformanceMoves();
			if (moves_arr == null) {
				moves += "NULL";
			} else {
				for (int i=0; i<moves_arr.length; i++) {
					moves += moves_arr[i]+",";
				}
			}
			g.drawString(moves, 20, 120);
			g.drawString("Ticks: "+this.getDutyPerformanceTicks(), 20, 140);
			g.setColor(orig);
		}
	}

	private void drawDebugBox(Graphics g, Container glassPane, Component comp, String label) {
		Rectangle bounds = SwingUtilities.convertRectangle(comp.getParent(), comp.getBounds(), glassPane);
		Color orig = g.getColor();
		g.setColor(Color.RED);
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		if (bounds.y - 10 > 0) {
			g.drawString(label, bounds.x, bounds.y - 10);
		} else {
			g.drawString(label, bounds.x, bounds.y + 10);
		}
		g.setColor(orig);
	}

	public GameState updateGameState() {
		spy.broadcastGameState(this);
		return this;
	}
}
