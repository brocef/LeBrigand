package lebrigand.bots.rigging;

import java.util.ArrayList;

public class RiggingBoard implements RiggingUtils {
	private int[][] _pieces;

	public RiggingBoard(int[][] pieces) {
		this._pieces = pieces;
	}
	
	public RiggingBoard(RiggingBoard original) {
		this._pieces = copyPieces(original._pieces);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RiggingBoard) {
			RiggingBoard rb = (RiggingBoard) o;
			for (int i=0; i<BOARD_ROWS; i++)
				for (int j=0; j<BOARD_AXIS_LENS[i]; j++)
					if (_pieces[i][j] != rb._pieces[i][j])
						return false;
			return true;
		}
		return false;
	}
	
	public void printBoard() {
		for (int i=0; i<BOARD_ROWS; i++) {
			int amt = BOARD_AXIS_LENS[i];
			for (int j=0; j<(BOARD_ROWS-amt); j++) {
				System.out.print(" ");
			}
			for (int j=0; j<amt; j++)
				System.out.print(_pieces[i][j] + " ");
			System.out.println();
		}
	}
	
	public void printBoard(int curRow, int curCol) {
		for (int i=0; i<BOARD_ROWS; i++) {
			int amt = BOARD_AXIS_LENS[i];
			for (int j=0; j<(BOARD_ROWS-amt); j++) {
				System.out.print(" ");
			}
			for (int j=0; j<amt; j++) {
				if (curRow == i && curCol == j)
					System.out.print("[");
				else if (curRow == i && curCol+1 == j)
					System.out.print("]");
				else
					System.out.print(" ");
				System.out.print(_pieces[i][j]);
				
				if (j+1 == amt) {
					if (curRow == i && curCol == j)
						System.out.print("]");
					else
						System.out.print(" ");
				}
			}
			System.out.println();
		}
	}

	private int[][] copyPieces(int[][] source) {
		int[][] pieces = new int[BOARD_ROWS][];
		for (int i=0; i<BOARD_ROWS; i++) {
			pieces[i] = new int[BOARD_AXIS_LENS[i]];
			for (int j=0; j<BOARD_AXIS_LENS[i]; j++) {
				pieces[i][j] = source[i][j];
			}
		}
		return pieces;
	}

	private ArrayList<RiggingIndex> getAdjacentIndicies(int row, int col) {
		ArrayList<RiggingIndex> neighbors = new ArrayList<RiggingIndex>();
		RiggingIndex next = getNextIndex(RiggingAxis.HORIZONTAL, row, col, false);
		if (next != null)
			neighbors.add(next);
		next = getPreviousIndex(RiggingAxis.HORIZONTAL, row, col, false);
		if (next != null)
			neighbors.add(next);
		next = getNextIndex(RiggingAxis.BACKSLASH, row, col, false);
		if (next != null)
			neighbors.add(next);
		next = getPreviousIndex(RiggingAxis.BACKSLASH, row, col, false);
		if (next != null)
			neighbors.add(next);
		next = getNextIndex(RiggingAxis.FORWARDSLASH, row, col, false);
		if (next != null)
			neighbors.add(next);
		next = getPreviousIndex(RiggingAxis.FORWARDSLASH, row, col, false);
		if (next != null)
			neighbors.add(next);
		return neighbors;
	}

	public int evaluateBoardScore(int pulleyIndex) {
		int tugRow = PULLEY_TUG_ROWS[pulleyIndex];
		int tugCol = PULLEY_TUG_COLS[pulleyIndex];
		int ourColor = _pieces[tugRow][tugCol];
		int score = 1;

		ArrayList<RiggingIndex> start = getAdjacentIndicies(tugRow, tugCol);
		ArrayList<RiggingIndex> searched = new ArrayList<RiggingIndex>();
		searched.add(new RiggingIndex(tugRow, tugCol));
//		System.out.println(searched.get(0) + "-" + _pieces[searched.get(0).getRow()][searched.get(0).getCol()]);
		while (!start.isEmpty()) {
			RiggingIndex ind = start.remove(0);
			if (searched.contains(ind)) continue;
			else {
//				System.out.println(ind + "-" + _pieces[ind.getRow()][ind.getCol()]);
				if (_pieces[ind.getRow()][ind.getCol()] == ourColor || _pieces[ind.getRow()][ind.getCol()] == NORMAL_PIECE_COUNT) {
					start.addAll(getAdjacentIndicies(ind.getRow(), ind.getCol()));
					score++;
				} else if (_pieces[ind.getRow()][ind.getCol()] > NORMAL_PIECE_COUNT)
					score +=  5;
				searched.add(ind);
			}
		}
		if (score < 3)
			score = 0;

		return score;
	}

	public int[][] rotateAxis(RiggingAxis axis, int index, int amount, boolean newBoard) {
		int[][] board = null;
		for (int i=0; i<amount; i++)
			board = rotateAxis(axis, index, newBoard);
		return board;
	}
	
	public int[][] rotateAxis(RiggingAxis axis, int index, boolean newBoard) {
		int[][] pieces = newBoard ? copyPieces(_pieces) : _pieces;

		int cur, next, row, col;
		row = 0;
		col = 0;
		if (axis == RiggingAxis.HORIZONTAL) {
			row = HORIZONTAL_STARTING_ROWS[index];
			col = HORIZONTAL_STARTING_COLS[index];
		} else if (axis == RiggingAxis.FORWARDSLASH) {
			row = FORWARDSLASH_STARTING_ROWS[index];
			col = FORWARDSLASH_STARTING_COLS[index];
		} else if (axis == RiggingAxis.BACKSLASH) {
			row = BACKSLASH_STARTING_ROWS[index];
			col = BACKSLASH_STARTING_COLS[index];
		}
		cur = pieces[row][col];
		RiggingIndex nextIndex;
		for (int i=0; i<BOARD_AXIS_LENS[index]; i++) {
			nextIndex = getNextIndex(axis, row, col, true);
			next = pieces[nextIndex.getRow()][nextIndex.getCol()];
			pieces[nextIndex.getRow()][nextIndex.getCol()] = cur;
			cur = next;

			row = nextIndex.getRow();
			col = nextIndex.getCol();
		}

		return pieces;
	}

	public static int indexToAxisIndex(RiggingAxis axis, int row, int col) {
		int axisIndex = -1;
		switch (axis) {
		case HORIZONTAL:
			axisIndex = row;
			break;
		case FORWARDSLASH:
			if (row <= 4)
				axisIndex = col;
			else
				axisIndex = (row - 4) + col;
			break;
		case BACKSLASH:
			if (row >= 4)
				axisIndex = col;
			else
				axisIndex = (4 - row) + col;
			break;
		}
		return axisIndex;
	}

	public RiggingIndex getNextIndex(RiggingAxis axis, int row, int col, boolean wrap) {
		RiggingIndex index = null;
		
		switch (axis) {
		case HORIZONTAL:
			index = new RiggingIndex(row, col + 1);
			if (index.getCol() >= BOARD_AXIS_LENS[row]) {
				if (wrap)
					index.moveTo(row, index.getCol() % BOARD_AXIS_LENS[row]);
				else
					index = null;
				
			}
			break;
		case FORWARDSLASH:
			if (row > 4)
				index = new RiggingIndex(row - 1, col + 1);
			else if (row > 0) {// || (row == 0 && col == 4)) {
				index = new RiggingIndex(row - 1, col);
				if (index.getCol() >= BOARD_AXIS_LENS[index.getRow()]) {
					if (wrap)
						index.moveTo(8, row);
					else
						index = null;
				}
			} else if (wrap && row == 0)// && col < 4)
				index = new RiggingIndex(4 + col, 0);

			break;
		case BACKSLASH:
			if (row < 4)
				index = new RiggingIndex(row + 1, col + 1);
			else if (row < 8) {// || (row == 8 && col == 4)) {
				index = new RiggingIndex(row + 1, col);
				if (index.getCol() >= BOARD_AXIS_LENS[index.getRow()]) {
					if (wrap)
						index.moveTo(0, 8 - row);
					else
						index = null;
				}
			} else if (wrap && row == 8)// && col < 4)
				index = new RiggingIndex(4 - col, 0);
			break;
		}

		return index;
	}

	public RiggingIndex getPreviousIndex(RiggingAxis axis, int row, int col, boolean wrap) {
		RiggingIndex index = null;

		switch (axis) {
		case HORIZONTAL:
			index = new RiggingIndex(row, col - 1);
			if (index.getCol() < 0) {
				if (wrap)
					index.moveTo(row, BOARD_AXIS_LENS[row] - 1);
				else
					index = null;
			}
			break;
		case FORWARDSLASH:
			if (row < 4)
				index = new RiggingIndex(row + 1, col);
			else if (row < 8) {// || (row == 8 && col == 0)) {
				index = new RiggingIndex(row + 1, col - 1);
				if (index.getCol() < 0) {
					if (wrap)
						index.moveTo(0, row - 4);
					else
						index = null;
				}
			} else if (wrap && row == 8)// && col > 0)
				index = new RiggingIndex(col, col + 4);

			break;
		case BACKSLASH:
			if (row > 4)
				index = new RiggingIndex(row - 1, col);
			else if (row > 0) {// || (row == 0 && col == 0)) {
				index = new RiggingIndex(row - 1, col - 1);
				if (index.getCol() < 0) {
					if (wrap)
						index.moveTo(8, 4 - row);
					else
						index = null;
				}
			} else if (wrap && row == 0)// && col > 0)
				index = new RiggingIndex(8 - col, 4 + col);
			break;
		}

		return index;
	}

	public int[][] rotateAxis(RiggingAxis axis, int index) {
		return rotateAxis(axis, index, true);
	}
}
