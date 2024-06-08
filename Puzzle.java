package sudoku;

import java.util.Random;

/**
 * The Sudoku number puzzle to be solved
 */
public class Puzzle {
    // All variables have package access
    // The numbers on the puzzle
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    public static int cellsToGuess;
    // The clues - isGiven (no need to guess) or need to guess
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];

    private Random random = new Random();

    // Constructor
    public Puzzle() {
        super();
    }

    // Generate a new puzzle given the number of cells to be guessed, which can be used
    //  to control the difficulty level.
    // This method shall set (or update) the arrays numbers and isGiven
   /*
   public void newPuzzle(int cellsToGuess) {
      // I hardcode a puzzle here for illustration and testing.
      int[][] hardcodedNumbers =
         {{5, 3, 4, 6, 7, 8, 9, 1, 2},
          {6, 7, 2, 1, 9, 5, 3, 4, 8},
          {1, 9, 8, 3, 4, 2, 5, 6, 7},
          {8, 5, 9, 7, 6, 1, 4, 2, 3},
          {4, 2, 6, 8, 5, 3, 7, 9, 1},
          {7, 1, 3, 9, 2, 4, 8, 5, 6},
          {9, 6, 1, 5, 3, 7, 2, 8, 4},
          {2, 8, 7, 4, 1, 9, 6, 3, 5},
          {3, 4, 5, 2, 8, 6, 1, 7, 9}};

      // Copy from hardcodedNumbers into the array "numbers"
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            numbers[row][col] = hardcodedNumbers[row][col];
         }
      }

      // Need to use input parameter cellsToGuess!
      // Hardcoded for testing, only 2 cells of "8" is NOT GIVEN
      boolean[][] hardcodedIsGiven =
         {{true, true, true, true, true, false, true, true, true},
          {true, true, true, true, true, true, true, true, false},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true},
          {true, true, true, true, true, true, true, true, true}};

      // Copy from hardcodedIsGiven into array "isGiven"
      for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
         for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
            isGiven[row][col] = hardcodedIsGiven[row][col];
         }
      }

   }

    */

    //(For advanced students) use singleton design pattern for this class

    // Generate new puzzle
    public void newPuzzle(int cellsToGuess) {
        boolean[][] hardcodedIsGiven =
                {{true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, true, true, true, true, true, true, true}};

        // Copy from hardcodedIsGiven into array "isGiven"
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                isGiven[row][col] = hardcodedIsGiven[row][col];
            }
        }
        generateSolvedPuzzle();
        removeCells(cellsToGuess);
    }

    public void generateSolvedPuzzle() {
        fillDiagonalBoxes();
        solve(0, 0); //use recursive function
    }

    public void fillDiagonalBoxes() {
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i += SudokuConstants.SUBGRID_SIZE) {
            fillBox(i, i);
        }
    }

    // Fill a 3x3 box with numbers 1 to 9, and use isSafeBox() to ensure sudoku rules
    public void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                do {
                    num = random.nextInt(9) + 1;
                } while (!isSafeBox(row, col, num));
                numbers[row + i][col + j] = num;
            }
        }
    }

    // To ensure no repeated numbers within the 3x3 box
    public boolean isSafeBox(int row, int col, int num) {
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                if (numbers[row + i][col + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // Use recursive
    public boolean solve(int row, int col) {
        // if have passed the last col, reset to first col and move to next row
        if (col == SudokuConstants.GRID_SIZE) {
            col = 0;
            row++;
            if (row == SudokuConstants.GRID_SIZE) {
                return true;
            }
        }
        // if current cell is already filled, move to next cell
        if (numbers[row][col] != 0) {
            return solve(row, col + 1);
        }

        for (int num = 1; num <= 9; num++) {
            if (isSafe(row, col, num)) {
                numbers[row][col] = num;
                // solve the rest of the board
                if (solve(row, col + 1)) {
                    return true;
                }
                // if not solvable, backtrack: remove the number and try the next one
                numbers[row][col] = 0;
            }
        }
        return false;
    }

    public boolean isSafe(int row, int col, int num) {
        //check row and col
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            if (numbers[row][i] == num || numbers[i][col] == num) {
                return false;
            }
        }
        //check 3x3 box
        int boxRow = row - row % SudokuConstants.SUBGRID_SIZE; //gives row index of the top-left of the box (subgrid)
        int boxCol = col - col % SudokuConstants.SUBGRID_SIZE;
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                if (numbers[boxRow + i][boxCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeCells(int cellsToGuess) { // cells to guess = cells to remove

        while (cellsToGuess > 0) {
            int row = random.nextInt(SudokuConstants.GRID_SIZE);
            int col = random.nextInt(SudokuConstants.GRID_SIZE);

            if (isGiven[row][col]) { // Check if the cell is currently marked as given
                isGiven[row][col] = false; // Mark this cell as not given
                cellsToGuess--;
            }
        }
    }


}