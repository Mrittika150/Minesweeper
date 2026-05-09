import java.util.Random;

public class Board {
    private int rows;
    private int cols;
    private int mineCount;
    private Cell[][] grid;
    private boolean minesPlaced;

    public  Board(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.minesPlaced = false;
        initGrid();
    }

    private void initGrid() {
        grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell();
            }
        }
    }

    public void placeMines(int safeRow, int safeCol) {
        Random random = new Random();
        int placed =0;

        while (placed < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (grid[r][c].isMine()|| r== safeRow && c== safeCol) {
                continue;
            }
            grid[r][c].setMine(true);
            placed++;
        }
        calculateAdjacentCounts();
        minesPlaced = true;
    }

    private void calculateAdjacentCounts() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!grid[r][c].isMine()) {
                    grid[r][c].setAdjacentMines(countNeighborMines(r,c));
                }
            }
        }
    }

    private int countNeighborMines(int row, int col) {
        int count = 0;
        for(int dr=-1; dr<=1; dr++) {
            for(int dc=-1; dc<=1; dc++) {
                if(dr==0 && dc==0) {
                    continue;
                }
                int nr = row+dr;
                int nc = col+dc;
                if(isInBounds(nr,nc)&&grid[nr][nc].isMine()){
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInBounds(int r, int c) {
        return r>=0 && r<rows && c>=0 && c<cols;
    }

    public void revealCell(int row, int col) {
        if(!isInBounds(row,col)) {
            return;
        }

        Cell cell = grid[row][col];
        if(cell.isRevealed()|| cell.isFlagged()){
            return;
        }
        cell.reveal();

        if(!cell.isMine() && cell.getAdjacentMines() ==0){
            for(int dr =-1; dr<=1; dr++) {
                for(int dc=-1; dc<=1; dc++) {
                    if(dr==0 && dc==0) {
                        continue;
                    }
                    revealCell(row+dr, col+dc);
                }
            }
        }
    }

    public boolean checkWin(){
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                if(!grid[r][c].isMine() && !grid[r][c].isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Cell getCell(int r, int c) {
        return grid[r][c];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMineCount() {
        return mineCount;
    }

    public boolean isMinesPlaced() {
        return minesPlaced;
    }
}
