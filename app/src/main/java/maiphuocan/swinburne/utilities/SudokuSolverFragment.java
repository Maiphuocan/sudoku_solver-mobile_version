package maiphuocan.swinburne.utilities;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SudokuSolverFragment extends android.support.v4.app.Fragment {

    ArrayList<boolean[]> possibleValueList = new ArrayList<>();
    int[][] matrix = new int[9][9];
    int finishedCell;

    public SudokuSolverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sudoku_solver, container, false);
    }

    public static SudokuSolverFragment newInstance()
    {
        SudokuSolverFragment fragment = new SudokuSolverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onResume() {
        super.onResume();
        Button btnSolve = (Button)getView().findViewById(R.id.buttonSolve);
        btnSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSolveAction();
            }
        });
    }

    /**
     * Get editText when know ít position
     */
    private EditText getEditText(int i, int j) {
        String idName = "editText" + (i + 1) + (j + 1);
        int id = getResources().getIdentifier(idName, "id", "maiphuocan.swinburne.utilities");
        return (EditText)getView().findViewById(id);
    }

    /**
     * Get inputted value then fill to matrix
     */
    private void fillMatrix() {
        finishedCell = 0;
        this.possibleValueList = new ArrayList<>();
        this.matrix = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                /*Add possibleValue*/
                boolean[] possibleValue = new boolean[9];
                for (int x = 0; x < 9; x++) {
                    possibleValue[x] = false;
                }
                this.possibleValueList.add(possibleValue);
                /*Get inputted value*/
                String text = getEditText(i, j).getText().toString();
                try {
                    int value = text.equals("") ? 0 : Integer.parseInt(text);
                    /*Check range of number. Range is form 1 to 9*/
                    if (9 >= value && value >= 1) {
                        matrix[i][j] = value;
                        finishedCell += 1;
                    } else if (value == 0) {
                        matrix[i][j] = 0;
                    } else {
//                        JOptionPane.showMessageDialog(this, "Please input number from 1 to 9 only");
                        return;
                    }
                } catch (NumberFormatException e) {
                    /*Check if user input number or text*/
//                    JOptionPane.showMessageDialog(this, "Please input number");
                    return;
                }
            }
        }
        /*Show the matrix*/
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Log.i("Value at " + (i + 1) + "," + (j+1) + ": ", matrix[i][j] + "");
            }
        }
    }

    /**
     * try to solve the puzzle
     */
    private boolean btnSolveAction() {
        /*Fill the matrix*/
        fillMatrix();
        /*Go through matrix*/
        int stopFlag = this.finishedCell;
        while (finishedCell < 81) {
            System.out.println("Found " + finishedCell + " cells.");
            /*Solve the matrix*/
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (matrix[i][j] == 0) {
                        testCell(i, j);
                    }
                }
            }
            /*After go throught all cells, no more cell is solved, stop solving*/
            if (this.finishedCell == stopFlag) {
                break;
            }
            stopFlag = finishedCell;
        }
        return finishedCell == 81;
    }

    /**
     * Test each cell in matrix, if the cell is solved, assign value to that
     * cell
     *
     * @param row the row of cell
     * @param column the column of cell
     */
    private boolean testCell(int row, int column) {
        boolean[] possibleValue = this.possibleValueList.get(row * 9 + column);
        for (int i = 0; i < 9; i++) {
            possibleValue[i] = false;
        }
        testRow(row, possibleValue);
        int result = checkFound(possibleValue);
        if (result > 0) {
            matrix[row][column] = result;
            getEditText(row, column).setText(result + "");
            finishedCell += 1;
            System.out.println("testRow found " + result + " at position " + (row + 1) + "," + (column + 1));
            return true;
        }
        testColumn(column, possibleValue);
        result = checkFound(possibleValue);
        if (result > 0) {
            matrix[row][column] = result;
            getEditText(row, column).setText(result + "");
            finishedCell += 1;
            System.out.println("testColumn found " + result + " at position " + (row + 1) + "," + (column + 1));
            return true;
        }
        testSmallMatrix(row, column, possibleValue);
        result = checkFound(possibleValue);
        if (result > 0) {
            matrix[row][column] = result;
            getEditText(row, column).setText(result + "");
            finishedCell += 1;
            System.out.println("testSmallMatrix found " + result + " at position " + (row + 1) + "," + (column + 1));
            return true;
        }
        result = testUniqueValue(row, column);
        if (result > 0) {
            matrix[row][column] = result;
            getEditText(row, column).setText(result + "");
            finishedCell += 1;
            return true;
        }
        testDuplicate(row, column);
        return false;
    }

    /**
     * Check if value of the cell is found or not, it is found if 8 over 9 value
     * in possibleValue is true.
     *
     * @param possibleValue
     * @return -1 if there is error during finding process, 0 if value of the
     * cell is not found, 1 to 9 if the value is found
     */
    private int checkFound(boolean[] possibleValue) {
        int result = -1;    //initial is -1 mean did not check
        for (int i = 0; i < 9; i++) {
            /*False value at position i mean the cell could be this value*/
            if (!possibleValue[i]) {
                /*result equal -1 means this is the first value that the could be receive*/
                if (result == -1) {
                    result = i + 1;
                } else {    //This mean there are more than 1 possible value for the cell, return 0
                    return 0;
                }
            }
        }
        return result;
    }

    /**
     * To eliminate some values in possible values list
     *
     * @param row the row to be checked
     * @param possibleValue an array of 9 boolean value to mark if the cell
     * could eliminate which value
     */
    private void testRow(int row, boolean[] possibleValue) {
        /*Go throught row*/
        for (int i = 0; i < 9; i++) {
            if (matrix[row][i] != 0) {
                possibleValue[matrix[row][i] - 1] = true;
            }
        }
    }

    /**
     * To eliminate some values in possible values list
     *
     * @param column the column to be checked
     * @param possibleValue an array of 9 boolean value to mark if the cell
     * could eliminate which value
     */
    private void testColumn(int column, boolean[] possibleValue) {
        /*Go throught column*/
        for (int i = 0; i < 9; i++) {
            if (matrix[i][column] != 0) {
                possibleValue[matrix[i][column] - 1] = true;
            }
        }
    }

    /**
     * To eliminate some values in possible values list
     *
     * @param row the row to be checked
     * @param column the column to be checked
     * @param possibleValue an array of 9 boolean value to mark if the cell
     * could eliminate which value
     */
    private void testSmallMatrix(int row, int column, boolean[] possibleValue) {
        int startRow = row - row % 3;
        int startColumn = column - column % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startColumn; j < startColumn + 3; j++) {
                if (matrix[i][j] != 0) {
                    possibleValue[matrix[i][j] - 1] = true;
                }
            }
        }
    }

    /**
     * Get a string that contain all possible value for specific cell
     *
     * @param row
     * @param column
     * @return
     */
    private String getPossibleValue(int row, int column) {
        boolean[] possibleValue = this.possibleValueList.get(row * 9 + column);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (!possibleValue[i]) {
                result.append(i + 1);
            }
        }
        return result.toString();
    }

    /**
     * Check unsolved cells in same row or column or small matrix, if no more
     * cell hold same value, this cell should is the value
     *
     * @param row
     * @param column
     * @return
     */
    private int testUniqueValue(int row, int column) {
        boolean[] possibleValue = this.possibleValueList.get(row * 9 + column);
        for (int i = 0; i < 9; i++) {
            /*For each possible value*/
            if (!possibleValue[i]) {
                /*Check other unsolved cells in row, if no more cell hold this value, return this value*/
                boolean found = false;  //False if cannot found any other unsolved cell hold this value
                for (int j = 0; j < 9; j++) {
                    if (j == column || matrix[row][j] != 0) {
                        continue; //Only check other unsolved cells in same row
                    }
                    if (!this.possibleValueList.get(row * 9 + j)[i]) {
                        found = true;
                        break;  //If an unsolved cell in same row hold this value, stop search in row
                    }
                }
                if (!found) {
                    System.out.println("testUniqueValue.Row found " + (i + 1) + " at position " + (row + 1) + "," + (column + 1));
                    return i + 1;
                }
                /*Check other unsolved cells in column, if no more cell hold this value, return this value*/
                found = false;
                for (int j = 0; j < 9; j++) {
                    if (j == row || matrix[j][column] != 0) {
                        continue; //Only check other unsolved cells in same column
                    }
                    if (!this.possibleValueList.get(j * 9 + column)[i]) {
                        found = true;
                        break;  //If an unsolved cell in same column hold this value, stop search in column
                    }
                }
                if (!found) {
                    System.out.println("testUniqueValue.Column found " + (i + 1) + " at position " + (row + 1) + "," + (column + 1));
                    return i + 1;
                }
                /*Check other unsolved cells in same small matrix, if no more cell hold this value, return this value*/
                found = false;
                int startRow = row - row % 3;
                int startColumn = column - column % 3;
                for (int j = startRow; j < startRow + 3; j++) {
                    if (found) {
                        break;
                    }
                    for (int k = startColumn; k < startColumn + 3; k++) {
                        if ((j == row && k == column) || matrix[j][k] != 0) {
                            continue; //Only check other unsolved cells in same column
                        }
                        if (getPossibleValue(j, k).contains((i + 1) + "")) {
                            found = true;
                            break;  //If an unsolved cell in same matrix hold this value, stop search in matrix
                        }
                    }
                }
                if (!found) {
                    System.out.println("testUniqueValue.SmallMatrix found " + (i + 1) + " at position " + (row + 1) + "," + (column + 1));
                    return i + 1;
                }
            }
        }
        return 0;
    }

    /**
     * If 2 unsolved cells in same row, or column, or small matrix hold only 2
     * values and same as each other, other unsolved cells in same row, column,
     * small matrix could not hold those 2 values
     *
     * @param row
     * @param column
     */
    private void testDuplicate(int row, int column) {
        /*Check other unsolved cells in row, if they are same, modify other unsolved cells in the row*/
        String possibleString = getPossibleValue(row, column);
        /*Only check unsolved cells that hold 2 possible values*/
        if (possibleString.length() != 2 || matrix[row][column] != 0) {
            return;
        }
        int[] duplicateValues = new int[2];
        duplicateValues[0] = Integer.valueOf(possibleString) / 10;
        duplicateValues[1] = Integer.valueOf(possibleString) % 10;
        /*Check duplicate in row*/
        testDuplicateRow(row, column, possibleString, duplicateValues);
        /*Check duplicate in column*/
        testDuplicateColumn(row, column, possibleString, duplicateValues);
        /*Check duplicate in small matrix*/
        testDuplicateSmallMatrix(row, column, possibleString, duplicateValues);
    }

    /**
     * A section of testDuplicate function
     *
     * @param row
     * @param column
     * @param possibleString
     * @param duplicateValues
     */
    private void testDuplicateRow(int row, int column, String possibleString, int[] duplicateValues) {
        for (int i = 0; i < 9; i++) {
            if (i == column || matrix[row][i] != 0) {
                continue; //Only check other unsolved cells in same row
            }
            if (getPossibleValue(row, i).equals(possibleString)) {
                /*Modify other unsolved cells in the row*/
                for (int j = 0; j < 9; j++) {
                    /*do not check solved cells and 2 duplicate cells*/
                    if (matrix[row][j] != 0 || j == column || j == i) {
                        continue;
                    }
                    boolean[] possibleValue = this.possibleValueList.get(row * 9 + j);
                    possibleValue[duplicateValues[0] - 1] = true;
                    possibleValue[duplicateValues[1] - 1] = true;
                    int result = checkFound(possibleValue);
                    if (result > 0) {
                        matrix[row][j] = result;
                        getEditText(row, j).setText(result + "");
                        finishedCell += 1;
                        System.out.println("testDupicate.Row found " + result + " at position " + (row + 1) + "," + (j + 1));
                    }
                }
                break;
            }
        }
    }

    /**
     * A section of testDuplicate function
     *
     * @param row
     * @param column
     * @param possibleString
     * @param duplicateValues
     */
    private void testDuplicateColumn(int row, int column, String possibleString, int[] duplicateValues) {
        for (int i = 0; i < 9; i++) {
            if (i == row || matrix[i][column] != 0) {
                continue; //Only check other unsolved cells in same row
            }
            if (getPossibleValue(i, column).equals(possibleString)) {
                /*Modify other unsolved cells in the column*/
                for (int j = 0; j < 9; j++) {
                    /*do not check solved cells and 2 duplicate cells*/
                    if (matrix[j][column] != 0 || j == row || j == i) {
                        continue;
                    }
                    boolean[] possibleValue = this.possibleValueList.get(j * 9 + column);
                    possibleValue[duplicateValues[0] - 1] = true;
                    possibleValue[duplicateValues[1] - 1] = true;
                    int result = checkFound(possibleValue);
                    if (result > 0) {
                        matrix[j][column] = result;
                        getEditText(j, column).setText(result + "");
                        finishedCell += 1;
                        System.out.println("testDupicate.Column found " + result + " at position " + (j + 1) + "," + (column + 1));
                    }
                }
                break;
            }
        }
    }

    /**
     * A section of testDuplicate function
     *
     * @param row
     * @param column
     * @param possibleString
     * @param duplicateValues
     */
    private void testDuplicateSmallMatrix(int row, int column, String possibleString, int[] duplicateValues) {
        int startRow = row - row % 3;
        int startColumn = column - column % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startColumn; j < startColumn + 3; j++) {
                if ((i == row && j == column) || matrix[i][j] != 0) {
                    continue; //Only check other unsolved cells in same column
                }
                if (getPossibleValue(i, j).equals(possibleString)) {
                    /*Modify other unsolved cells in the row*/
                    for (int k = startRow; k < startRow + 3; k++) {
                        for (int l = startColumn; l < startColumn + 3; l++) {
                            /*do not check solved cells and 2 duplicate cells*/
                            if (matrix[k][l] != 0 || (k == row && l == column) || (k == i && l == j)) {
                                continue;
                            }
                            boolean[] possibleValue = this.possibleValueList.get(k * 9 + l);
                            possibleValue[duplicateValues[0] - 1] = true;
                            possibleValue[duplicateValues[1] - 1] = true;
                            int result = checkFound(possibleValue);
                            if (result > 0) {
                                matrix[k][l] = result;
                                getEditText(k, l).setText(result + "");
                                finishedCell += 1;
                                System.out.println("testDupicate.SmallMatrix found " + result + " at position " + (k + 1) + "," + (l + 1));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
