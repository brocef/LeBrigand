package src.bots.rigging;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JFrame;

import src.bots.SpyglassBot;
import src.core.spyglass.Spyglass;
import src.core.ui.Messenger;

public class RiggingBot extends SpyglassBot implements RiggingUtils {

	public RiggingBot(Spyglass spy, JFrame yppFrame, Messenger msger) throws AWTException {
		super(spy, yppFrame, msger);
	}

	@Override
	public String getBotName() {
		return "Rigging Bot";
	}

	@Override
	public void run() {
		log("Rigging Bot Starting");
		updateBotStatus("Running");

		try {
			while (stillAlive()) {
				updateHooks();
				if (isTutorialPanelShowing())
					closeTutorial();
				if (!gameBoardIsActive()) {
					//					if (!getRiggingPanelOverlay().equals("n/a") && getRiggingPanelOverlay().equals("TutorialPanel"))
					//						closeTutorial();
					sleep(250);
					if (!stillAlive())
						break;
					continue;
				}
				RiggingBoard r = new RiggingBoard(getRiggingBoard());
				Entry<Rotation,Integer> rot = null;
				boolean nextMoveWillClear = true;
				for (int j=0; j<3; j++) {
					int pulley = (getRiggingPulley() + j) % PULLEY_COUNT;
					rot = getBestRotation(r, pulley);
					if (rot.getValue() > 3) {
						log("Best rotation was %s clearing %s pieces", rot.getKey().toString(), rot.getValue().toString());
						if (rot.getValue() > 5) {
							break;
						} else {
							log("However that's not enough!");
						}
					} else {
						nextMoveWillClear = false;
						log("No immediate point-yeilding rotation found. Prepping next pulley");
					}
				}
				if (rot == null) {
					log("No valid moves could be found.");
					break;
				}
				boolean extraDelay = rot.getValue() + getRiggingCoilCount() >= 20;
				int before = getRiggingStarProgress();
				doRotation(rot.getKey(), nextMoveWillClear, extraDelay);
				if (nextMoveWillClear && getRiggingStarProgress() < before)
					Thread.sleep(3000); //Cleared the board
			}
		} catch(InterruptedException e) {
			log("Rigging Bot Interrupted Exception Caught");
		}
		updateBotStatus("Terminated");
	}

	private Point randomPointInRect(Rectangle r) {
		return new Point(r.x + (int)(Math.random() * (r.width - r.x)),
				r.y + (int)(Math.random() * (r.height - r.y)));
	}

	private void closeTutorial() throws InterruptedException {
		Rectangle tutDismiss = getTutorialPanelDismissArea();
		Point random = randomPointInRect(tutDismiss);
		mouseMove(random);
		mouseClick(MouseEvent.BUTTON1_DOWN_MASK);
		Thread.sleep(500);
	}

	private void doRotation(Rotation rot, boolean willClearAnything, boolean extraDelay) {
		navigateToAxis(rot.getAxis(), rot.getAxisIndex());

		if (!getRiggingKeyDrag()) {
			keyPress(KeyEvent.VK_S);
			keyRelease(KeyEvent.VK_S);
		}
		sleep(50);
		int rotAmt = rot.getRotationAmount();
		boolean back = false;
		if (rotAmt > (int)Math.ceil(0.5 * BOARD_AXIS_LENS[rot.getAxisIndex()])) {
			rotAmt = BOARD_AXIS_LENS[rot.getAxisIndex()] - rotAmt;
			back = true;
		}
		for (int j=0; j<rotAmt; j++) {
			if (!gameBoardIsActive())
				return;
			if (back)
				keyPress(rot.getAxis().getBackwardKey());
			else
				keyPress(rot.getAxis().getForwardKey());
			sleep(25L, 50L);
			if (back)
				keyRelease(rot.getAxis().getBackwardKey());
			else
				keyRelease(rot.getAxis().getForwardKey());
			sleep(75L, 100L);
		}
		if (getRiggingKeyDrag()) {
			keyPress(KeyEvent.VK_S);
			keyRelease(KeyEvent.VK_S);
		}
//		sleep(150);
		try {
			if (willClearAnything) {
				waitUntilAnimStops();
				if (extraDelay)
					sleep(1300);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected boolean gameBoardIsActive() {
		return !dutyReportIsUp();// && getRiggingPanelOverlay().equals("n/a");
	}

	protected void waitUntilAnimStops() throws InterruptedException {
		long time = System.currentTimeMillis() + 300;
		while (!dutyReportIsUp()) {
			long now = System.currentTimeMillis();
			if (now - time > 300)
				break;
			if (getRiggingBoardIsAnimating() && now > time)
				time = now;
			sleep(200);
		}
	}

	private Entry<Rotation,Integer> getBestRotation(RiggingBoard current, int pulley) {
		Rotation best = new Rotation(RiggingAxis.HORIZONTAL,RiggingBoard.indexToAxisIndex
				(RiggingAxis.HORIZONTAL, PULLEY_TUG_ROWS[pulley], PULLEY_TUG_COLS[pulley]), 1);
		int bscore = 0;//current.evaluateBoardScore(pulley);
		for (int i=0; i<BOARD_AXIS_LENS.length; i++) {
			for (int j=1; j<BOARD_AXIS_LENS[i]; j++) {
				for (int k=0; k<3; k++) {
					Rotation r = new Rotation(RiggingAxis.values()[k], i, j);
					RiggingBoard newboard = r.executeRotation(current);
					int score = newboard.evaluateBoardScore(pulley);
					//					System.out.println(r.toString() + " yeilded new score of " + score);
					if (score > bscore) {
						bscore = score;
						best = r;
					}
				}
			}
		}

		return new SimpleEntry<Rotation,Integer>(best, bscore);
	}

	private void navigateToAxis(RiggingAxis axis, int axisIndex) {
		if (getRiggingCursorRow() == -1 || getRiggingCursorCol() == -1) {
			log("Something failed. Cursor not initialized.");
			return;
		}
		ArrayList<Integer> path = getCursorPath(getRiggingCursorRow(), getRiggingCursorCol(), axis, axisIndex);
		//		if (!isGameWindowFocused()) {
		//			mouseMove(super.getRiggingBoardViewLocation());
		//			sleep(50);
		//			mousePress(MouseEvent.BUTTON1_MASK);
		//			sleep(50);
		//			mouseRelease(MouseEvent.BUTTON1_MASK);
		//		}
		if (getRiggingKeyDrag()) {
			keyPress(KeyEvent.VK_S);
			keyRelease(KeyEvent.VK_S);
		}
		for (Integer keycode:path) {
			//			bringGameToFront();
			if (dutyReportIsUp())
				break;
			keyPress(keycode);
			//			pause(50);
			keyRelease(keycode);
			sleep(50);
		}
	}

	class Rotation {
		private int axisIndex;
		private RiggingAxis axis;
		private int amount;

		Rotation(RiggingAxis axis, int axisIndex, int amount) {
			this.axis = axis;
			this.axisIndex = axisIndex;
			this.amount = amount;
		}

		@Override
		public String toString() {
			return String.format("Axis %s index %d for amount %d", axis.name(), axisIndex, amount);
		}

		RiggingAxis getAxis() {
			return this.axis;
		}

		int getAxisIndex() {
			return this.axisIndex;
		}

		int getRotationAmount() {
			return this.amount;
		}

		RiggingBoard executeRotation(RiggingBoard board) {
			RiggingBoard newBoard = new RiggingBoard(board);
			newBoard.rotateAxis(axis, axisIndex, amount, false);
			return newBoard;
		}
	}

	//This method assumes that the curAxisIndex is normalized to a semi-tangential axis:
	//HORIZONTAL -> FORWARDSLASH
	//FORWARDSLASH -> BACKSLASH
	//BACKSLASH -> FORWARDSLASH
	private int vectorKeyFromAxis(RiggingAxis targetAxis, int targetAxisIndex, int curAxisIndex, boolean increase) {
		//		System.out.printf("Target %s %d from %d and increase: %b\n", targetAxis.name(), targetAxisIndex, curAxisIndex, increase);
		if (increase) {
			switch (targetAxis) {
			case HORIZONTAL:
				if (curAxisIndex <= 3 && curAxisIndex >= 0)
					return KeyEvent.VK_X;
				else if (curAxisIndex >= 4 && curAxisIndex <= 8)
					return KeyEvent.VK_Z;
				break;
			case FORWARDSLASH:
				if (curAxisIndex <= 3 && curAxisIndex >= 0)
					return KeyEvent.VK_D;
				else if (curAxisIndex >= 4 && curAxisIndex <= 8)
					return KeyEvent.VK_X;
				break;
			case BACKSLASH:
				if (curAxisIndex <= 3 && curAxisIndex >= 0)
					return KeyEvent.VK_D;
				else if (curAxisIndex >= 4 && curAxisIndex <= 8)
					return KeyEvent.VK_E;
				break;
			}
		} else {
			switch (targetAxis) {
			case HORIZONTAL:
				if (curAxisIndex <= 4 && curAxisIndex >= 0)
					return KeyEvent.VK_E;
				else if (curAxisIndex >= 5 && curAxisIndex <= 8)
					return KeyEvent.VK_W;
				break;
			case FORWARDSLASH:
				if (curAxisIndex <= 4 && curAxisIndex >= 0)
					return KeyEvent.VK_W;
				else if (curAxisIndex >= 5 && curAxisIndex <= 8)
					return KeyEvent.VK_A;
				break;
			case BACKSLASH:
				if (curAxisIndex <= 4 && curAxisIndex >= 0)
					return KeyEvent.VK_Z;
				else if (curAxisIndex >= 5 && curAxisIndex <= 8)
					return KeyEvent.VK_A;
				break;
			}
		}
		return 0;
	}

	private int getVectorKey(RiggingAxis targetAxis, int axisIndex, int curRow, int curCol) {
		int keycode = 0;
		//		RiggingBoard.indexToAxisIndex(RiggingAxis.FORWARDSLASH, curRow, curCol);
		RiggingAxis curAxis = targetAxis;
		int curAxisIndex = RiggingBoard.indexToAxisIndex(curAxis, curRow, curCol);
		//		System.out.printf("Cursor [%d][%d] is at %d on %s\n", curRow, curCol, curAxisIndex, curAxis.name());
		boolean increase = curAxisIndex < axisIndex;
		switch (targetAxis) {
		case HORIZONTAL:
			curAxis = RiggingAxis.FORWARDSLASH;
			curAxisIndex = RiggingBoard.indexToAxisIndex(curAxis, curRow, curCol);
			keycode = vectorKeyFromAxis(targetAxis, axisIndex, curAxisIndex, increase);
			break;
		case FORWARDSLASH:
			curAxis = RiggingAxis.BACKSLASH;
			curAxisIndex = RiggingBoard.indexToAxisIndex(curAxis, curRow, curCol);
			keycode = vectorKeyFromAxis(targetAxis, axisIndex, curAxisIndex, increase);
			break;
		case BACKSLASH:
			curAxis = RiggingAxis.FORWARDSLASH;
			curAxisIndex = RiggingBoard.indexToAxisIndex(curAxis, curRow, curCol);
			keycode = vectorKeyFromAxis(targetAxis, axisIndex, curAxisIndex, increase);
			break;
		}
		if (keycode == 0) {
			System.out.printf("Somehow no keycode found for axis %s index %d cur [%d][%d]",
					targetAxis.name(), axisIndex, curRow, curCol);
		}
		return keycode;
	}

	private ArrayList<Integer> getCursorPath(int curRow, int curCol, RiggingAxis targetAxis, int axisIndex) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		int currentAxis = RiggingBoard.indexToAxisIndex(targetAxis, curRow, curCol);
		for (int i=0; i<Math.abs(axisIndex-currentAxis); i++) {
			int key = getVectorKey(targetAxis, axisIndex, curRow, curCol);
			switch (key) {
			case KeyEvent.VK_A:
				curCol--;
				break;
			case KeyEvent.VK_D:
				curCol++;
				break;
			case KeyEvent.VK_E:
				if (curRow > 4)
					curCol++;
				curRow--;
				break;
			case KeyEvent.VK_W:
				if (curRow < 4)
					curCol--;
				curRow--;
				break;
			case KeyEvent.VK_X:
				if (curRow < 4)
					curCol++;
				curRow++;
				break;
			case KeyEvent.VK_Z:
				if (curRow > 4)
					curCol--;
				curRow++;
				break;
			}
			path.add(key);
		}

		return path;
	}

}
