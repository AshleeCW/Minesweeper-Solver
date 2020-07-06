import java.io.UncheckedIOException;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {

		Minefield mineField = new Minefield(13, "1 12    ", "1   3  2", "  3  5 3", "1    5  ", "23 3   1", "  2122  ",
				"2    1  ", "  1   0 ");

		mineField.backtrackSolve();
		System.out.println(mineField.isConsistent());

		System.out.println(mineField.toString());

	}

	public static class Minefield {
		int mineCount;
		int width;
		int height;
		int[][] numbers; // grid of numbers for field
		Boolean[][] isMine; // record of each cell true = mine, false = not mine, null = unsure

		public Minefield(int mineCount, String... clues) {
			width = clues[0].length();
			height = clues.length;

			numbers = new int[width][height];

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					char c = clues[i].charAt(j);
					if (c == ' ') {
						numbers[j][i] = 9; // making any empty space into a 9 to make an int grid
					} else {
						numbers[j][i] = Integer.parseInt(new String(new char[] { c })); // take char from string and
																						// insert number into grid if
																						// number
					}
				}
			}

			isMine = new Boolean[width][height]; // for setting boolean at position of cell

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (numbers[j][i] != 9) {
						isMine[j][i] = false;
					}

				}

			}
		}

		// create clone
		public Minefield(Minefield toCopy) {
			mineCount = toCopy.mineCount;
			width = toCopy.width;
			height = toCopy.height;
			numbers = new int[width][height];
			isMine = new Boolean[width][height];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					numbers[j][i] = toCopy.numbers[j][i];
					isMine[j][i] = toCopy.isMine[j][i];
				}
			}
		}

		// check to see if the simplesolve results are consistant with what
		public boolean isConsistent() {
			int totalMineCount = 0;
			int totalSpaceCount = 0;

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {

//					if (isMine[j][i] != null) {
//						if (isMine[j][i]) {
//							totalMineCount += 1;
//						} else {
//							totalSpaceCount += 1;
//						}
//					}

					int numberOfSurroundingMines = numbers[j][i];
					if (numberOfSurroundingMines == 9) { // 9 means hasn't been looked at yet, end this method and keep
															// checking for 9s in
						continue;
					}

					int definitelyMines = 0;
					int definitelySpaces = 0;
					// looking around the cell in question. Maybe use the numberOfSurroundingMines
					// here to check for if any other search has tagged the cell?
					for (int dI = -1; dI <= 1; dI++) {
						for (int dJ = -1; dJ <= 1; dJ++) {
							try {
								if (isMine[j + dJ][i + dI] != null) {
									if (isMine[j + dJ][i + dI]) {
										definitelyMines += 1;
									} else {
										definitelySpaces += 1;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								definitelySpaces += 1;
							}
						}
					}

					int minimumMines = definitelyMines;
					int maximumMines = 9 - definitelySpaces;

					if (numberOfSurroundingMines < minimumMines || numberOfSurroundingMines > maximumMines) {
						return false;
					}

				}
			}

			return true; // totalMinimumMines <= mineCount && mineCount <= totalMaximumMines;
		}

		/*
		 * solve minefield and then go over with a throwaway board and compare
		 * alternative settings. If throwaway cells becomes consistant then change main
		 * field and call simplesolve again to see how it alters neighbouring fields.
		 */
		public void backtrackSolve() {
			simpleSolve();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (isMine[j][i] != null) {
						continue;
					}

					// create new minefield to compare alternative isMine settings without altering
					// main fieldS
					Minefield throwaway = new Minefield(this);
					throwaway.isMine[j][i] = false;
					throwaway.simpleSolve();
					if (!throwaway.isConsistent()) {
						isMine[j][i] = true;
						simpleSolve();
						continue;
					}

					throwaway = new Minefield(this);
					throwaway.isMine[j][i] = true;
					// throwaway.simpleSolve();
					if (!throwaway.isConsistent()) {
						isMine[j][i] = false;
						simpleSolve();
						continue;
					}
				}
			}
		}

		// method to mark cells as mines
		public void simpleSolve() {
			boolean madeProgress = true;
			while (madeProgress) {
				madeProgress = false;

				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (isMine[j][i] != null) {
							continue;
						} else { 
							isMine[j][i] = false;
							if (!isConsistent()) {
								isMine[j][i] = true;
								madeProgress = true;
								continue;
							}

							isMine[j][i] = true;
							if (!isConsistent()) {
								isMine[j][i] = false;
								madeProgress = true;
								continue;
							}

							isMine[j][i] = null;
						}
					}
				}
			}
		}

		// builds minefield into an ordered grid for printing to console
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (numbers[j][i] != 9) {
						stringBuilder.append(numbers[j][i]);
					} else {
						// if the cell is null leave it blank for next iteration otherwise check if its
						// a mine again, if yes mark M otherwise mark .
						stringBuilder.append(isMine[j][i] == null ? ' ' : isMine[j][i] ? 'M' : '.');
					}
					stringBuilder.append(' ');

				}
				stringBuilder.append('\n');
			}
			return stringBuilder.toString();
		}
	}

}
