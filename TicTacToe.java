import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private char currentPlayer = 'X';
    private int xScore = 0;
    private int oScore = 0;
    private JLabel scoreLabel;
    private boolean isSinglePlayer;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Game Mode Selection
        String[] options = {"Single Player (vs Computer)", "Two Players"};
        int choice = JOptionPane.showOptionDialog(this, "Choose game mode:", "Game Mode Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        isSinglePlayer = (choice == 0);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                buttonPanel.add(buttons[row][col]);
            }
        }

        scoreLabel = new JLabel("Score - X: 0 | O: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());

        add(scoreLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resetButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Button click event handler
    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("")) {
                buttons[row][col].setText(String.valueOf(currentPlayer));
                if (checkWinner()) {
                    if (currentPlayer == 'X') {
                        xScore++;
                        JOptionPane.showMessageDialog(null, "Player X wins!");
                    } else {
                        oScore++;
                        JOptionPane.showMessageDialog(null, "Player O wins!");
                    }
                    updateScore();
                    resetBoard();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "It's a tie!");
                    resetBoard();
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X'; // Switch player

                if (isSinglePlayer && currentPlayer == 'O') {
                    computerMove(); // Computer plays
                }
            }
        }
    }

    // Computer makes a move using Minimax with Alpha-Beta Pruning
    private void computerMove() {
        int[] bestMove = findBestMove();
        if (bestMove != null) {
            buttons[bestMove[0]][bestMove[1]].setText(String.valueOf(currentPlayer));
            if (checkWinner()) {
                oScore++;
                JOptionPane.showMessageDialog(null, "Player O (Computer) wins!");
                updateScore();
                resetBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(null, "It's a tie!");
                resetBoard();
            }
            currentPlayer = 'X'; // Switch back to player X
        }
    }

    // Minimax algorithm with Alpha-Beta Pruning to find the best move for the computer
    private int[] findBestMove() {
        int bestValue = -1000;
        int[] bestMove = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O"); // Make the move
                    int moveValue = minimax(0, -1000, 1000, false);
                    buttons[i][j].setText(""); // Undo the move

                    if (moveValue > bestValue) {
                        bestMove = new int[]{i, j};
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    // Minimax algorithm implementation with Alpha-Beta Pruning
    private int minimax(int depth, int alpha, int beta, boolean isMax) {
        if (checkWinner()) {
            return (isMax) ? -1 : 1; // 1 for 'O' (computer), -1 for 'X' (player)
        }
        if (isBoardFull()) {
            return 0; // Tie
        }

        if (isMax) {
            int bestValue = -1000;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().equals("")) {
                        buttons[i][j].setText("O"); // Make the move
                        bestValue = Math.max(bestValue, minimax(depth + 1, alpha, beta, !isMax));
                        buttons[i][j].setText(""); // Undo the move
                        alpha = Math.max(alpha, bestValue);
                        if (beta <= alpha) {
                            break; // Beta cut-off
                        }
                    }
                }
            }
            return bestValue;
        } else {
            int bestValue = 1000;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().equals("")) {
                        buttons[i][j].setText("X"); // Make the move
                        bestValue = Math.min(bestValue, minimax(depth + 1, alpha, beta, !isMax));
                        buttons[i][j].setText(""); // Undo the move
                        beta = Math.min(beta, bestValue);
                        if (beta <= alpha) {
                            break; // Alpha cut-off
                        }
                    }
                }
            }
            return bestValue;
        }
    }

    // Check for a winner
    private boolean checkWinner() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if ((buttons[i][0].getText().equals(String.valueOf(currentPlayer)) &&
                    buttons[i][1].getText().equals(String.valueOf(currentPlayer)) &&
                    buttons[i][2].getText().equals(String.valueOf(currentPlayer))) ||
                    (buttons[0][i].getText().equals(String.valueOf(currentPlayer)) &&
                            buttons[1][i].getText().equals(String.valueOf(currentPlayer)) &&
                            buttons[2][i].getText().equals(String.valueOf(currentPlayer)))) {
                return true;
            }
        }
        // Check diagonals
        return (buttons[0][0].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[2][2].getText().equals(String.valueOf(currentPlayer))) ||
                (buttons[0][2].getText().equals(String.valueOf(currentPlayer)) &&
                        buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
                        buttons[2][0].getText().equals(String.valueOf(currentPlayer)));
    }

    // Check if the board is full
    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Reset the game board
    private void resetBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        currentPlayer = 'X'; // Reset to starting player
    }

    // Reset the game scores
    private void resetGame() {
        resetBoard();
        xScore = 0;
        oScore = 0;
        updateScore();
    }

    // Update the score label
    private void updateScore() {
        scoreLabel.setText("Score - X: " + xScore + " | O: " + oScore);
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}