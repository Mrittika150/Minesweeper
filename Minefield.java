
import java.util.Random;

public class Minefield {
    private int size;
    private int mineCount;
    private boolean[][] mines;
    private int[][] adjacentMines;

    public Minefield(int size, int mineCount) {
        this.size = size;
        this.mineCount = mineCount;
        mines = new boolean[size][size];
        adjacentMines = new int[size][size];
    }

    public void placeMines(int initialRow, int initialCol) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < mineCount) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            if ((row != initialRow || col != initialCol) && !mines[row][col]) {
                mines[row][col] = true;
                placedMines++;
            }
        }

        calculateAdjacentMines();
    }

    private void calculateAdjacentMines() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!mines[row][col]) {
                    int count = 0;
                    for (int r = Math.max(0, row - 1); r <= Math.min(size - 1, row + 1); r++) {
                        for (int c = Math.max(0, col - 1); c <= Math.min(size - 1, col + 1); c++) {
                            if (mines[r][c]) {
                                count++;
                            }
                        }
                    }
                    adjacentMines[row][col] = count;
                }
            }
        }
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public int getAdjacentMines(int row, int col) {
        return adjacentMines[row][col];
    }

    public void reset() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                mines[row][col] = false;
                adjacentMines[row][col] = 0;
            }
        }
    }
}
