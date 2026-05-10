import javax.swing.*;

public class Minesweeper {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameUI(12, 12, 10));
    }
}