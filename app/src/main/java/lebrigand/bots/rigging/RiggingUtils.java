package lebrigand.bots.rigging;
import java.awt.event.KeyEvent;

public interface RiggingUtils {
	public enum RiggingAxis {
		HORIZONTAL(KeyEvent.VK_D, KeyEvent.VK_A),
		FORWARDSLASH(KeyEvent.VK_E, KeyEvent.VK_Z),
		BACKSLASH(KeyEvent.VK_X, KeyEvent.VK_W);
		
		int forward, back;
		RiggingAxis(int forward, int back) {
			this.forward = forward;
			this.back = back;
		}
		
		public int getForwardKey() {
			return this.forward;
		}
		
		public int getBackwardKey() {
			return this.back;
		}
	};
	public static final int[] HORIZONTAL_STARTING_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
	public static final int[] HORIZONTAL_STARTING_COLS = {0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final int[] FORWARDSLASH_STARTING_ROWS = {4, 5, 6, 7, 8, 8, 8, 8, 8};
	public static final int[] FORWARDSLASH_STARTING_COLS = {0, 0, 0, 0, 0, 1, 2, 3, 4};
	public static final int[] BACKSLASH_STARTING_ROWS = {4, 3, 2, 1, 0, 0, 0, 0, 0};
	public static final int[] BACKSLASH_STARTING_COLS = {0, 0, 0, 0, 0, 1, 2, 3, 4};
	
	public static final int BOARD_ROWS = 9;
	public static final int[] BOARD_AXIS_LENS = {5, 6, 7, 8, 9, 8, 7, 6, 5};
	public static final int NORMAL_PIECE_COUNT = 8;
	public static final int SPECIAL_PIECE_COUNT = 4;
	public static final int PULLEY_COUNT = 6;
	public static final int[] PULLEY_TUG_ROWS = {0, 2, 6, 8, 6, 2};
	public static final int[] PULLEY_TUG_COLS = {2, 6, 6, 2, 0, 0};
}
