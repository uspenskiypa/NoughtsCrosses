package ru.noughtscrosses.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Board {

    private Cell[][] cellField; //двумерный массив ячеек
    private final int width, height; //ширина и высота поля
    private final Random rn = new Random();

    //Конструктор класса Board
    //Принимает размер игрового поля
    //columns - количество столбцов
    //rows - количество строк
    public Board(int columns, int rows) {
        cellField = new Cell[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                cellField[i][j] = new Cell(State.EMPTY, i, j);
            }
        }
        width = columns;
        height = rows;
    }

    //Геттер для поля cellField
    public Cell[][] getCellField() {
        return cellField;
    }

//!Временный метод! Выводит характеристики ячеек поля
    public void printCells() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(cellField[i][j].state + " | " + cellField[i][j].x + " | " + cellField[i][j].y + " | ");
            }
            System.out.println();
        }
    }

    //Выбирает ячейку для первого хода и возвращает ее индексы 
    public Cell moveFirst() {
        return cellField[rn.nextInt(3)][rn.nextInt(3)];
    }

    //Проверяет поле на ниличие выиграшной комбинации. Если такая есть вернет true, иначе false.
    public boolean isSomeoneWon(Cell[][] board, State state) {
        if ((board[0][0].state == state && board[0][1].state == state && board[0][2].state == state)
                || (board[1][0].state == state && board[1][1].state == state && board[1][2].state == state)
                || (board[2][0].state == state && board[2][1].state == state && board[2][2].state == state)
                || (board[0][0].state == state && board[1][0].state == state && board[2][0].state == state)
                || (board[0][1].state == state && board[1][1].state == state && board[2][1].state == state)
                || (board[0][2].state == state && board[1][2].state == state && board[2][2].state == state)
                || (board[0][0].state == state && board[1][1].state == state && board[2][2].state == state)
                || (board[0][2].state == state && board[1][1].state == state && board[2][0].state == state)) {
            return true;
        } 
        else {
            return false;
        }
    }

    //Проверяет кончились ли ходы
    public boolean isEndOfMoves(Cell[][] board) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].state == State.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    //Проверяет матч на завершенность
    private boolean isGameOver(Cell[][] board, int depth) {
        if (depth == 0 || isSomeoneWon(board, State.CROSS) || isSomeoneWon(board, State.NOUGHT) || isEndOfMoves(board)) {
            return true;
        } 
        else {
            return false;
        }
    }

    //Подсчитывает итоговое значение для ячейки
    private int calcBestValue(Cell[][] board, Turn turn, State state) {
        if (turn == Turn.OURTURN) {
            if (state == State.CROSS) {
                if (isSomeoneWon(board, State.CROSS)) {
                    return 10;
                } 
                else if (isSomeoneWon(board, State.NOUGHT)) {
                    return -10;
                }
            } 
            else if (isSomeoneWon(board, State.CROSS)) {
                return -10;
            } 
            else if (isSomeoneWon(board, State.NOUGHT)) {
                return 10;
            }
        } 
        else if (state == State.CROSS) {
            if (isSomeoneWon(board, State.CROSS)) {
                return -10;
            } 
            else if (isSomeoneWon(board, State.NOUGHT)) {
                return 10;
            }
        } 
        else if (isSomeoneWon(board, State.CROSS)) {
            return 10;
        } 
        else if (isSomeoneWon(board, State.NOUGHT)) {
            return -10;
        }
        return 0;
    }

    //Возвращает ячейку с наивысшим значение
    public Cell getBestCell(State state, int depth) {
        valuation(cellField, null, Turn.OURTURN, state, depth);
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Cell> listBestCell = new ArrayList<>();
        for (Cell[] cells : cellField) {
            for (Cell cell : cells) {
                System.out.println("[" + cell.x + "][" + cell.y + "] = " + cell.value);
                if (cell.state == State.EMPTY) {
                    if (cell.value > maxValue) {
                        maxValue = cell.value;
                        listBestCell.clear();
                        listBestCell.add(cell);
                    } 
                    else if (cell.value == maxValue) {
                        listBestCell.add(cell);
                    }
                }
            }
        }
        Collections.shuffle(listBestCell);
        Cell bestCell = listBestCell.get(0);
        System.out.println("==========================");
        //Обнуляем ценность ячеек перед следующим ходом
        for (Cell[] cells : cellField) {
            for (Cell cell : cells) {
                cell.value = 0;
            }
        }
        return bestCell;
    }

    //Рекурсивный обход возможных вариантов ходов игруков до заданной глубины depth
    //и оценка значений свободных ячеек
    public int valuation(Cell[][] board, Cell cell, Turn turn, State state, int depth) {
        if (depth == 0) {
            return 0;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].state == State.EMPTY) {
                    Cell[][] newBoard = new Cell[width][height];
                    for (int k = 0; k < width; k++) {
                        for (int l = 0; l < height; l++) {
                            newBoard[k][l] = new Cell(board[k][l]);
                        }
                    }
                    newBoard[i][j].state = state;
                    if (depth == 5) {
                        cell = cellField[i][j];
                        smartChangeValue(newBoard, cell, state);
                    }
                    if (isGameOver(newBoard, depth)) {
                        int bestValue = calcBestValue(newBoard, turn, state);
                        if (turn == Turn.OURTURN) {
                            cell.value += bestValue*depth*depth*depth;
                            if (depth == 5) {
                                cell.value += bestValue*50;
                                return 0;
                            }
                        }
                        else {
                            cell.value += bestValue*depth*depth*depth;
                            if (depth == 4) {
                                cell.value += bestValue*50;
                            }
                        }
                    } 
                    else {
                        valuation(newBoard, cell, getNewTurn(turn), getNewState(state), depth - 1);
                    }
                }
            }
        }
        return 0;
    }
    
    //Возвращает новое состояние State
    public State getNewState(State state) {
        return state == State.CROSS ? State.NOUGHT : State.CROSS;
    }

    //Возвращает новую очередность хода Turn
    public Turn getNewTurn(Turn turn) {
        return turn == Turn.OURTURN ? Turn.ENEMYTURN : Turn.OURTURN;
    }
    
    //Изменяет ценность ячеек при ситуации, когда противник занял противоположные углы
    public void smartChangeValue(Cell[][] board, Cell cell, State state) {
        State otherState = getNewState(state);
        if (board[1][1].state == state) {
            if ((board[0][0].state == otherState && board[2][2].state == otherState) 
                    || (board[0][2].state == otherState && board[2][0].state == otherState)) {
                int colEmpty = 0;
                for (Cell[] cells : board) {
                    for (Cell c : cells) {
                        if (c.state == State.EMPTY) {
                            ++colEmpty;
                        }
                    }
                }
                if (colEmpty == 5) {
                    int sum = cell.x + cell.y;
                    if (sum == 1 || sum == 3) {
                        cell.value += 1000;
                    }
                }
            }
        }
    }
}