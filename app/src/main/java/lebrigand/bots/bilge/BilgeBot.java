package lebrigand.bots.bilge;

import com.samskivert.util.Logger;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import lebrigand.bots.SpyglassBot;
import lebrigand.core.spyglass.LeBrigandActuator;
import lebrigand.core.spyglass.Spyglass;
import lebrigand.core.ui.Messenger;

public class BilgeBot extends SpyglassBot implements BilgeUtils, Runnable {
    private static final Logger logger = Logger.getLogger(BilgeBot.class.getName());
    private static final String BOT_NAME = "BilgeBot";

	public BilgeBot(Spyglass spy, LeBrigandActuator actuator) {
		super(spy, actuator);
	}

	public String getBotName() {
		return BOT_NAME;
	}

	public static int xyToIndex(int x, int y) {
		return BOARD_WIDTH * y + x;
	}

	public static int xyToIndex(Point p) {
		return xyToIndex(p.x, p.y);
	}

	public static Point indexToXY(int index) {
		return new Point(index % BOARD_WIDTH, index / BOARD_WIDTH);
	}

	public static Point xyToScreenXY(Point offset, int x, int y) {
		return new Point(offset.x + (BOARD_WIDTH - 1 - x) * PIECE_WIDTH, offset.y + y * PIECE_HEIGHT);
	}

	public static Point xyToScreenXY(Point offset, Point p) {
		return xyToScreenXY(offset, p.x, p.y);
	}

	@Override
	public void run() {
		try {
			BilgeBot.logger.info("Bilge Bot Starting");
			while (stillAlive()) {
				updateHooks();
				if (dutyReportIsUp()) {
					Thread.sleep(250);
					if (!stillAlive())
						break;
					continue;
				}
				
				int[] bilgeBoard = getBilgeBoard();
				if (movesArePossible(bilgeBoard)) {
					Stack<Entry<Move, Integer>> moves = new Stack<Entry<Move, Integer>>();
					BilgeDecision dec = getBreadthBM(bilgeBoard, false); //TODO: add max depth of 3?
					while (dec != null) {
						moves.push(new SimpleEntry<Move, Integer>(dec.getMove(), dec.getScore()));
						dec = dec.getPrevious();
					}
					
					if (moves.size() == 0) { //Now search under water
						dec = getBreadthBM(bilgeBoard, true);
						while (dec != null) {
							moves.push(new SimpleEntry<Move, Integer>(dec.getMove(), dec.getScore()));
							dec = dec.getPrevious();
						}
					}

					
					if (moves.size() == 0) {
						BilgeBot.logger.info("Bilge bot exhausted of moves. Shutdown imminent.");
						break;
					} else
						Collections.reverse(moves); //We have to reverse since we build the list backwards
					if (moves.size() > 1) {
						BilgeBot.logger.info("Multiple moves to be executed:");
						for (Entry<Move, Integer> m:moves)
							BilgeBot.logger.info(m.toString());
					}
					for (Entry<Move, Integer> m:moves) {
						BilgeBot.logger.info("Executing move %s with score %d", m.getKey().toString(), m.getValue());
						doMove(m.getKey());
						waitUntilAnimStops();
					}
				} else {
					BilgeBot.logger.info("Bilge board invalid or no moves possible. Shutdown imminent.");
					break;
				}
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
			BilgeBot.logger.info("Bilge Bot Kill Command Received?");
		}
	}

	//TODO: Needs to check for 3 in consecutive rows for a vertical clear
	protected boolean movesArePossible(int[] board) {
		if (board == null) return false;
		
		int[][] counts = {new int[7], new int[7], new int[7]};
		int rotate = 0;
		for (int y=0; y<BOARD_HEIGHT; y++) {
			for (int x=0; x<BOARD_WIDTH; x++) {
				int piece = board[xyToIndex(x, y)];
				//				log("Piece (%d,%d) is %d", x, y, piece);
				if (piece == -1)
					return false;
				if (piece >= PUFFER_VAL)
					return true;
				counts[rotate][piece]++;
				if (counts[rotate][piece] >= 3)
					return true;
			}
			if (++rotate == 3) {
				int[] vert_counts = {0, 0, 0, 0, 0, 0};
				if (y + rotate < BOARD_HEIGHT)
					for (int i=0; i<3; i++) {
						for (int j=0; j<6; j++) {
							vert_counts[j] += counts[i][j];
							if (vert_counts[j] >= 3)
								return true;
						}
					}
				counts[0] = Arrays.copyOf(counts[1], 6);
				counts[1] = Arrays.copyOf(counts[2], 6);
				Arrays.fill(counts[2], 0);
				rotate--;
			}
		}
		return false;
	}

	protected void waitUntilAnimStops() throws InterruptedException {
		long time = System.currentTimeMillis() + 200;
		while (System.currentTimeMillis() - time < 300 && !dutyReportIsUp()) {
			if (bilgeBoardIsAnimating())
				time = System.currentTimeMillis();
			Thread.sleep(100);
		}
	}

	protected int getLastDryRow() {
		int wl = getWaterLevel();
		return BOARD_HEIGHT - (int) Math.round((double) wl / 45.0);
	}

	protected Queue<BilgeDecision> buildInitialQueue(int[] board, boolean underwater) {
		return buildDecisionQueue(board, underwater, null, null);
	}

	protected Queue<BilgeDecision> buildDecisionQueue(int[] board, boolean underwater, BilgeDecision previous, Collection<BilgeDecision> toAvoid) {
		Queue<BilgeDecision> init = new ArrayDeque<BilgeDecision>(72);
		int lastDryRow = getLastDryRow();
		for (int y=0; y<(underwater?BOARD_HEIGHT:lastDryRow); y++) {
			for (int x=1; x<BOARD_WIDTH; x++) {
				int left, right;
				left = board[xyToIndex(x, y)];
				right = board[xyToIndex(x-1, y)];
				if (left == right)
					continue;
				Move m = new Move(x, x-1, y, left, right);
				int[] newboard = executeMove(m, board, true);
				BilgeDecision bd = new BilgeDecision(previous, m, getMoveScore(m, newboard), newboard);
				if (toAvoid == null || !toAvoid.contains(bd))
					init.add(bd);
			}
		}
		return init;
	}

	class BilgeDecision {
		BilgeDecision previous;
		int score;
		Move move;
		int[] board;

		public BilgeDecision(BilgeDecision previous, Move move, int score, int[] board) {
			this.previous = previous;
			this.move = move;
			this.score = score;
			this.board = board;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof BilgeDecision) {
				BilgeDecision other = (BilgeDecision) o;
				if (this.move == other.move) {
					for (int i=0; i<board.length; i++)
						if (this.board[i] != other.board[i])
							return false;
					return true;
				}
			}
			return false;
		}

		BilgeDecision getPrevious() {
			return this.previous;
		}

		int[] getBoard() {
			return this.board;
		}

		Move getMove() {
			return this.move;
		}

		int getScore() {
			return this.score;
		}
	}

	protected BilgeDecision getBreadthBM(int[] board, boolean underwater) {
		BilgeDecision best = null;

		int bscore = 0;
		int depth = 1;

		Queue<BilgeDecision> toCheck = buildInitialQueue(board, underwater);
		Queue<BilgeDecision> visited = new ArrayDeque<BilgeDecision>();
		Queue<BilgeDecision> nextLevel = new ArrayDeque<BilgeDecision>();
		while (!toCheck.isEmpty()) {
			BilgeDecision toTest = toCheck.remove();
			if (depth == 1 && toTest.getScore() >= bscore
					|| depth > 1 && toTest.getScore() > bscore) {
				bscore = toTest.getScore();
				best = toTest;
			}
			if (!visited.contains(toTest))
				nextLevel.add(toTest);
			visited.add(toTest);
			if (toCheck.isEmpty()) {
				if (bscore == 0) {
					depth++;
					while (!nextLevel.isEmpty()) {
						BilgeDecision bd = nextLevel.remove();
						int[] newboard = bd.getBoard();
						toCheck.addAll(this.buildDecisionQueue(newboard, underwater, bd, visited));
					}
				} else
					break;
			}
		}

		return best;
	}

	protected int getMoveScore(Move m, int[] board) {
		if (m.getLeft() == PUFFER_VAL || m.getRight() == PUFFER_VAL)
			return 1;
		if (m.getLeft() == CRAB_VAL || m.getRight() == CRAB_VAL)
			return -1;
		if (m.getLeft() == JELLY_VAL || m.getRight() == JELLY_VAL)
			return 2;

		int[] after = board;//executeMove(m, board, true);
		int leftscore = 0;
		int rightscore = 0;

		int y_count = 1;
		int x_count = 1;
		for (int y_off = 1; 
				y_off < 3 && m.getY() + y_off < BOARD_HEIGHT; 
				y_off++) {
			int nextindex = xyToIndex(m.getLeftX(), m.getY() + y_off);
			int next = after[nextindex];
			//			log("Next %d index %d is at location (%d,%d)", next, nextindex, m.getLeftX(), m.getY() + y_off);
			if (m.getRight() == next)
				//			if (m.getRight() == after[xyToIndex(m.getLeftX(), m.getY() + y_off)])
				y_count++;
			else
				break;
		}
		for (int y_off = -1; 
				y_off > -3 && m.getY() + y_off >= 0; 
				y_off--) {
			if (m.getRight() == after[xyToIndex(m.getLeftX(), m.getY() + y_off)])
				y_count++;
			else
				break;
		}
		for (int x_off = Move.getLeftXOffset(); 
				Math.abs(x_off) < 3 && m.getLeftX() + x_off < BOARD_WIDTH; 
				x_off += Move.getLeftXOffset()) {
			if (m.getRight() == after[xyToIndex(m.getLeftX() + x_off, m.getY())])
				x_count++;
			else
				break;
		}
		if (y_count > 2)
			leftscore += y_count;
		if (x_count > 2)
			leftscore += x_count;
		if (x_count > 2 && y_count > 2)
			leftscore *= 2;

		y_count = 1;
		x_count = 1;

		for (int y_off = 1; 
				y_off < 3 && m.getY() + y_off < BOARD_HEIGHT; 
				y_off++) {
			if (m.getLeft() == after[xyToIndex(m.getRightX(), m.getY() + y_off)])
				y_count++;
			else
				break;
		}
		for (int y_off = -1; 
				y_off > -3 && m.getY() + y_off >= 0; 
				y_off--) {
			if (m.getLeft() == after[xyToIndex(m.getRightX(), m.getY() + y_off)])
				y_count++;
			else
				break;
		}
		for (int x_off = Move.getRightXOffset(); 
				Math.abs(x_off) < 3 && m.getRightX() + x_off >= 0; 
				x_off += Move.getRightXOffset()) {
			if (m.getLeft() == after[xyToIndex(m.getRightX() + x_off, m.getY())])
				x_count++;
			else
				break;
		}
		
		if (y_count > 2)
			rightscore += y_count;
		if (x_count > 2)
			rightscore += x_count;
		if (x_count > 2 && y_count > 2)
			rightscore *= 2;

		int score = leftscore + rightscore;
		if (leftscore > 0 && rightscore > 0)
			score *= 2;

		return score;
	}

	public void doMove(Move m) throws InterruptedException {
//		bringGameToFront();

		Point tl = SwingUtilities.convertPoint(getBilgeBoardView(), 0, 0, getYPPFrame().getContentPane());
		//Point br = new Point(tl.x + game.getBilgeBoardView().getWidth(), tl.y + game.getBilgeBoardView().getHeight());
		BilgeBot.logger.info("Offset is %s", tl.toString());
		swapTiles(xyToScreenXY(tl, m.getRightX(), m.getY()), xyToScreenXY(tl, m.getLeftX(), m.getY()));
//		swapTiles(new Point (m.getRightX(), m.getY()), new Point(m.getLeftX(), m.getY()));
	}

	public void swapTiles(Point T0, Point T1) throws InterruptedException {
		Random rand = new Random();
		int x = 23 + rand.nextInt(44) + Math.min(T0.x, T1.x);
		int y = rand.nextInt(40) + Math.min(T0.y, T1.y)+2+45;
		BilgeBot.logger.info("Trying to click tiles at around (%d,%d)", x, y);
		mouseMove(x, y);
		sleep(200L);
//		mousePress(MouseEvent.BUTTON1_DOWN_MASK);
//		sleep(50);
//		mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
		mouseClick(MouseEvent.BUTTON1_DOWN_MASK);
		sleep(100);
	}

	public int[] executeMove(Move m, int[] board, boolean newboard) {
		int[] after = newboard ? Arrays.copyOf(board, BilgeUtils.BOARD_LEN) : board;
		int left = after[m.getLeftIndex()];
		int right = after[m.getRightIndex()];
		after[m.getLeftIndex()] = right;
		after[m.getRightIndex()] = left;
		return after;
	}
}
