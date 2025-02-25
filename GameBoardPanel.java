package sudoku;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class GameBoardPanel extends JPanel {
   private static final long serialVersionUID = 1L;  // to prevent serial warning

   // Define named constants for UI sizes
   public static final int CELL_SIZE = 90;   // Cell width/height in pixels
   public static final int BOARD_WIDTH  = CELL_SIZE * SudokuConstants.GRID_SIZE;
   public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
                                             // Board width/height in pixels

   // Define properties
   public static GameDifficulty difficulty;
   /** The game board composes of 9x9 Cells (customized JTextFields) */
   private static Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
   /** It also contains a Puzzle with array numbers and isGiven */

   private static Puzzle puzzle = new Puzzle();


   //music
   //private MusicPlayer musicPlayer = new MusicPlayer();
   //URL url = this.getClass().getResource("/bg_music_sudoku.wav");


   /** Constructor */
   public GameBoardPanel() {
      super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));  // JPanel

      // Allocate the 2D array of Cell, and added into JPanel.
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            cells[row][col] = new Cell(row, col);
            super.add(cells[row][col]);   // JPanel
         }
      }

      //[TODO 3] Allocate a common listener as the ActionEvent listener for all the
      //  Cells (JTextFields)
      CellInputListener listener = new CellInputListener();

      // [TODO 4] Adds this common listener to all editable cells
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            if (cells[row][col].isEditable()) {
               cells[row][col].addActionListener(listener);   // For all editable rows and cols
            }
         }
      }

      super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));


   }
   

   /**
    * Generate a new puzzle; and reset the game board of cells based on the puzzle.
    * You can call this method to start a new game.
    */
   public static void newGame() {
      // Generate a new puzzle
       if (difficulty == GameDifficulty.EASY)
          Puzzle.cellsToGuess = 32;
       else if (difficulty == GameDifficulty.MEDIUM)
          Puzzle.cellsToGuess = 45;
       else if (difficulty == GameDifficulty.HARD)
          Puzzle.cellsToGuess = 54;
       else
          Puzzle.cellsToGuess = 64;

      puzzle.newPuzzle(Puzzle.cellsToGuess);
      SudokuMain.timer.start();
      //musicPlayer.playMusic(url.getPath());
      SudokuMain.bgMusic.start();
      SudokuMain.bgMusic.loop(Clip.LOOP_CONTINUOUSLY);


      // Initialize all the 9x9 cells, based on the puzzle.
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
         }
      }
   }

   /**
    * Return true if the puzzle is solved
    * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
    */
   public boolean isSolved() {
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
               return false;
            }
         }
      }
      return true;
   }

   public Cell getCell(int row, int col) {
      return cells[row][col];
   }

   public boolean isGameOver() {
      return SudokuMain.mistakesCount == 5;
   }

   // [TODO 2] Define a Listener Inner Class for all the editable Cells
   private class CellInputListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         // Get a reference of the JTextField that triggers this action event
         Cell sourceCell = (Cell)e.getSource();
		 
         // Retrieve the int entered
         int numberIn = Integer.parseInt(sourceCell.getText());
         // For debugging
         System.out.println("You entered " + numberIn);

         /*
          * [TODO 5] (later - after TODO 3 and 4)
          * Check the numberIn against sourceCell.number.
          * Update the cell status sourceCell.status,
          * and re-paint the cell via sourceCell.paint().
          */
          if (numberIn == sourceCell.number) {
            sourceCell.status = CellStatus.CORRECT_GUESS;
            sourceCell.setEditable(false);

         } else {
            sourceCell.status = CellStatus.WRONG_GUESS;
            SudokuMain.mistakesCount++;
            SudokuMain.lblMistakes.setText("Mistakes: " + SudokuMain.mistakesCount);
         }
         sourceCell.paint();   // re-paint this cell based on its status

         /*
          * [TODO 6] (later)
          * Check if the player has solved the puzzle after this move,
          *   by calling isSolved(). Put up a congratulation JOptionPane, if so.
          */
         if (isSolved()) {
            SudokuMain.timer.stop();
            SudokuMain.bgMusic.stop();
            JOptionPane.showMessageDialog(null, "You Win!");
         }
         if (isGameOver()) {
            SudokuMain.timer.stop();
            SudokuMain.bgMusic.stop();
            JOptionPane.showMessageDialog(null, "You lose!");
         }
      }
   }
}