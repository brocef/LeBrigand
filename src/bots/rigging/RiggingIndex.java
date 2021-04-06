package src.bots.rigging;

public class RiggingIndex implements RiggingUtils {
	private int row, col;
	
	public RiggingIndex(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RiggingIndex) {
			RiggingIndex ind = (RiggingIndex) o;
			return ind.getRow() == getRow() && ind.getCol() == getCol();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("<[%d][%d]>", row, col);
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void moveTo(int newRow, int newCol) {
		this.row = newRow;
		this.col = newCol;
	}
}
