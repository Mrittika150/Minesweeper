import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameUI extends JFrame {

    private Board board;
    private GameState gameState;
    private JButton[][] buttons;
    private JLabel timerLabel;
    private JLabel mineLabel;
    private JButton resetButton;

    private static final Color[] NUMBER_COLORS = {
            null,
            Color.BLUE,
            new Color(0, 128, 0),
            Color.RED,
            new Color(0, 0, 128),
            new Color(128, 0, 0),
            new Color(0, 128, 128),
            Color.BLACK,
            Color.GRAY
    };

    public GameUI(int rows, int cols, int mineCount) {
        this.board = new Board(rows, cols, mineCount);
        this.gameState = new GameState(mineCount);
        this.buttons = new JButton[rows][cols];

        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buildTopPanel();
        buildGrid(rows, cols);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        mineLabel = new JLabel("Mines: " + board.getMineCount());
        mineLabel.setFont(new Font("Courier New", Font.BOLD, 16));

        resetButton = new JButton("[ Restart ]");
        resetButton.setFont(new Font("Courier New", Font.BOLD, 14));
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetGame());

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 16));

        new javax.swing.Timer(1000, e -> {
            if (gameState.isPlaying()) {
                timerLabel.setText("Time: " + gameState.getElapsedSeconds() + "s");
            }
        }).start();

        top.add(mineLabel, BorderLayout.WEST);
        top.add(resetButton, BorderLayout.CENTER);
        top.add(timerLabel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
    }

    private void buildGrid(int rows, int cols) {
        JPanel grid = new JPanel(new GridLayout(rows, cols));
        grid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(45, 45));
                btn.setFont(new Font("Courier New", Font.BOLD, 14));
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setBorder(BorderFactory.createRaisedBevelBorder());

                final int row = r, col = c;
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1)
                            handleLeftClick(row, col);
                        else if (e.getButton() == MouseEvent.BUTTON3)
                            handleRightClick(row, col);
                    }
                });

                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        add(grid, BorderLayout.CENTER);
    }

    private void handleLeftClick(int row, int col) {
        if (!gameState.isPlaying() && !gameState.isWaiting()) return;

        Cell cell = board.getCell(row, col);
        if (cell.isRevealed() || cell.isFlagged()) return;

        if (gameState.isWaiting()) {
            board.placeMines(row, col);
            gameState.startGame();
        }

        board.revealCell(row, col);

        if (cell.isMine()) {
            gameState.lose();
            revealAllMines();
            resetButton.setText("[ LOST - Retry? ]");
            resetButton.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "You hit a mine! Game over.");
        } else if (board.checkWin()) {
            gameState.win();
            resetButton.setText("[ WON - Play again? ]");
            resetButton.setForeground(new Color(0, 128, 0));
            JOptionPane.showMessageDialog(this, "You cleared the board! You win!");
        }

        refreshBoard();
    }

    private void handleRightClick(int row, int col) {
        if (!gameState.isPlaying() && !gameState.isWaiting()) return;

        Cell cell = board.getCell(row, col);
        if (cell.isRevealed()) return;

        cell.toggleFlag();

        if (cell.isFlagged()) gameState.incrementFlags();
        else                  gameState.decrementFlags();

        mineLabel.setText("Mines: " + gameState.getRemainingMines());
        refreshBoard();
    }

    private void refreshBoard() {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                JButton btn = buttons[r][c];

                if (cell.isRevealed()) {
                    btn.setEnabled(false);
                    btn.setText(getRevealedText(cell));
                    btn.setForeground(getRevealedColor(cell));
                    btn.setBorder(BorderFactory.createLoweredBevelBorder());

                } else if (cell.isFlagged()) {
                    btn.setEnabled(true);
                    btn.setText("<html><center><b style='color:red'>P</b><br><b>---</b></center></html>");
                    btn.setForeground(Color.RED);
                    btn.setBorder(BorderFactory.createRaisedBevelBorder());

                } else {
                    btn.setEnabled(true);
                    btn.setText("");
                    btn.setForeground(Color.GRAY);
                    btn.setBorder(BorderFactory.createRaisedBevelBorder());
                }
            }
        }
    }

    private String getRevealedText(Cell cell) {
        if (cell.isMine())
            return "<html><center><b>***</b></center></html>";
        if (cell.getAdjacentMines() == 0)
            return "";
        return "<html><center><b>" + cell.getAdjacentMines() + "</b></center></html>";
    }

    private Color getRevealedColor(Cell cell) {
        if (cell.isMine()) return Color.RED;
        return NUMBER_COLORS[cell.getAdjacentMines()];
    }

    private void revealAllMines() {
        for (int r = 0; r < board.getRows(); r++)
            for (int c = 0; c < board.getCols(); c++)
                if (board.getCell(r, c).isMine())
                    board.getCell(r, c).reveal();
        refreshBoard();
    }

    private void resetGame() {
        board.reset();
        gameState.reset();
        resetButton.setText("[ Restart ]");
        resetButton.setForeground(Color.BLACK);
        timerLabel.setText("Time: 0s");
        mineLabel.setText("Mines: " + board.getMineCount());

        for (int r = 0; r < board.getRows(); r++)
            for (int c = 0; c < board.getCols(); c++) {
                buttons[r][c].setText("");
                buttons[r][c].setEnabled(true);
                buttons[r][c].setForeground(Color.GRAY);
                buttons[r][c].setBorder(BorderFactory.createRaisedBevelBorder());
            }
    }
}