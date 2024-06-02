
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JFrame {
    private static final int GRID_SIZE = 10;
    private static final int MINE_COUNT = 10;
    private static final int CELL_SIZE = 30;

    private JButton[][] buttons;
    private Minefield minefield;
    private boolean firstClick;
    private Timer timer;
    private int elapsedSeconds;
    private JLabel timerLabel;
    private JLabel smileyButton;
    private boolean gameEnded;

    public Minesweeper() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        minefield = new Minefield(GRID_SIZE, MINE_COUNT);
        firstClick = true;
        gameEnded = false;

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 14));
                buttons[row][col].setMargin(new Insets(0, 0, 0, 0));

                final int r = row;
                final int c = col;

                buttons[row][col].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            handleLeftClick(r, c);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(r, c);
                        }
                    }
                });

                gridPanel.add(buttons[row][col]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        timerLabel = new JLabel("Time: 0");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);

        smileyButton = new JLabel("\u263A", JLabel.CENTER); // Smiley face
        smileyButton.setFont(new Font("Arial", Font.BOLD, 24));
        smileyButton.setOpaque(true);
        smileyButton.setBackground(Color.LIGHT_GRAY);
        smileyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        smileyButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetGame();
            }
        });
        topPanel.add(smileyButton, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startTimer();
    }

    private void handleLeftClick(int row, int col) {
        if (gameEnded || buttons[row][col].getText().equals("F")) {
            return;
        }

        if (firstClick) {
            minefield.placeMines(row, col);
            firstClick = false;
        }

        if (minefield.isMine(row, col)) {
            revealMines();
            gameEnded = true;
            timer.cancel();
            smileyButton.setText("\u2639"); // Sad face
            JOptionPane.showMessageDialog(this, "Game Over!");
        } else {
            revealCell(row, col);
            checkWinCondition();
        }
    }

    private void handleRightClick(int row, int col) {
        if (gameEnded || !buttons[row][col].isEnabled()) {
            return;
        }

        if (buttons[row][col].getText().equals("F")) {
            buttons[row][col].setText("");
        } else {
            buttons[row][col].setText("F");
            buttons[row][col].setBackground(Color.YELLOW);
        }
    }

    private void revealMines() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (minefield.isMine(row, col)) {
                    buttons[row][col].setText("M");
                    buttons[row][col].setBackground(Color.RED);
                }
            }
        }
    }

    private void revealCell(int row, int col) {
        if (!buttons[row][col].isEnabled()) {
            return;
        }

        int adjacentMines = minefield.getAdjacentMines(row, col);
        buttons[row][col].setText(adjacentMines > 0 ? String.valueOf(adjacentMines) : "");
        buttons[row][col].setEnabled(false);

        if (adjacentMines == 0) {
            for (int r = Math.max(0, row - 1); r <= Math.min(GRID_SIZE - 1, row + 1); r++) {
                for (int c = Math.max(0, col - 1); c <= Math.min(GRID_SIZE - 1, col + 1); c++) {
                    if (r != row || c != col) {
                        revealCell(r, c);
                    }
                }
            }
        }
    }

    private void checkWinCondition() {
        boolean allRevealed = true;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (!minefield.isMine(row, col) && buttons[row][col].isEnabled()) {
                    allRevealed = false;
                    break;
                }
            }
        }

        if (allRevealed) {
            gameEnded = true;
            timer.cancel();
            smileyButton.setText("\u263A"); // Smiley face
            JOptionPane.showMessageDialog(this, "You Win!");
        }
    }

    private void startTimer() {
        timer = new Timer();
        elapsedSeconds = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedSeconds++;
                timerLabel.setText("Time: " + elapsedSeconds);
            }
        }, 1000, 1000);
    }

    private void resetGame() {
        minefield.reset();
        firstClick = true;
        gameEnded = false;
        smileyButton.setText("\u263A"); // Smiley face

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
                buttons[row][col].setBackground(null);
            }
        }

        if (timer != null) {
            timer.cancel();
        }
        startTimer();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Minesweeper());
    }
}
