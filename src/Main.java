import java.io.UncheckedIOException;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		 
		Minefield mineField = new Minefield(13, "1 12    ",
				 "1   3  2",
				 "  3  5 3",
				 "1    5  ",
				 "23 3   1",
				 "  2122  ",
				 "2    1  ",
				 "  1   0 ");

		mineField.backtrackSolve();
		System.out.println(mineField.isConsistent());

		System.out.println(mineField);

	}

	public static class Minefield {
		int mineCount;
		int width;
		int height;
		int[][] numbers;
		Boolean[][] isMine;

		public Minefield(int mineCount, String... clues) {
			width = clues[0].length();
			height = clues.length;

			numbers = new int[width][height];

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					char c = clues[i].charAt(j);
					if (c == ' ') {
						numbers[j][i] = 9;
					} else {
						numbers[j][i] = Integer.parseInt(new String(new char[] { c }));
					}
				}

			}

			isMine = new Boolean[width][height];

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (numbers[j][i] != 9) {
						isMine[j][i] = false;
					}

				}

			}
		}

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

		public boolean isConsistent() {
			int totalMineCount = 0;
			int totalSpaceCount = 0;

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {

					if (isMine[j][i] != null) {
						if (isMine[j][i]) {
							totalMineCount += 1;
						} else {
							totalSpaceCount += 1;
						}
					}

					int clue = numbers[j][i];
					if (clue == 9) {
						continue;
					}

					int definitelyMines = 0;
					int definitelySpaces = 0;

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

					if (clue < minimumMines || clue > maximumMines) {
						return false;
					}

				}
			}

			int totalMinimumMines = totalMineCount;
			int totalMaximumMines = width * height - totalSpaceCount;

			return true; // totalMinimumMines <= mineCount && mineCount <= totalMaximumMines;
		}

		public void backtrackSolve() {
			simpleSolve();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (!isConsistent()) {
						assert isConsistent();
						throw new AssertionError("inconsistent");
					}

					if (isMine[j][i] != null) {
						continue;
					}

					Minefield throwaway;

					throwaway = new Minefield(this);
					throwaway.isMine[j][i] = false;
					throwaway.simpleSolve();
					if (!throwaway.isConsistent()) {
						isMine[j][i] = true;
						simpleSolve();
						continue;
					}

					throwaway = new Minefield(this);
					throwaway.isMine[j][i] = true;
					throwaway.simpleSolve();
					if (!throwaway.isConsistent()) {
						isMine[j][i] = false;
						simpleSolve();
						continue;
					}
				}
			}
		}

		public void simpleSolve() {
			boolean madeProgress = true;
			while (madeProgress) {
				madeProgress = false;

				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (isMine[j][i] != null) {
							continue;
						}

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

		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (numbers[j][i] != 9) {
						stringBuilder.append(numbers[j][i]);
					} else {
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
