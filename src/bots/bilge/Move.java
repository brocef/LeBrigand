package src.bots.bilge;

public class Move {
	private int y;
	private int leftx, rightx;
	private int leftIndex, rightIndex;
	private int leftPiece, rightPiece;

	public Move(int leftx, int rightx, int y, int left, int right) {
		this.leftx = leftx;
		this.rightx = rightx;
		this.y = y;
		this.leftPiece = left;
		this.rightPiece = right;
		this.leftIndex = BilgeBot.xyToIndex(leftx, y);
		this.rightIndex = BilgeBot.xyToIndex(rightx, y);
	}

	@Override
	public String toString() {
		return String.format("[%d @ (%d,%d) <--> %d (%d,%d)]", leftPiece, leftx, y, rightPiece, rightx, y);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Move) {
			Move other = (Move) o;
			return (leftPiece == other.leftPiece && rightPiece == other.rightPiece &&
					leftx == other.leftx && rightx == other.rightx && y == other.y);
		}
		return false;
	}
	
	public int getLeftIndex() {
		return this.leftIndex;
	}
	
	public int getRightIndex() {
		return this.rightIndex;
	}

	public int getLeft() {
		return leftPiece;
	}

	public int getRight() {
		return rightPiece;
	}

	public int getY() {
		return y;
	}

	public int getLeftX() {
		return leftx;
	}

	public int getRightX() {
		return rightx;
	}

	public static int getLeftXOffset() {
		return 1;
	}

	public static int getRightXOffset() {
		return -1;
	}
}
