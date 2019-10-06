package ru.noughtscrosses.objects;

import java.util.Random;

public class Board {

    private Cell[][] cellField; //двумерный массив ячеек
    private int width, height; //ширина и высота поля
    private Random rn = new Random();

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
    public Cell getBestCell(Turn turn, State state, int depth) {
        valuation(cellField, null, turn, state, depth);
        Cell bestCell = null;
        int maxValue = Integer.MIN_VALUE;
        for (Cell[] cells : cellField) {
            for (Cell cell : cells) {
                if (cell.value > maxValue && cell.state == State.EMPTY) {
                    maxValue = cell.value;
                    bestCell = cell;
                }
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
                    Cell choosenCell = cell;
                    newBoard[i][j].state = state;
                    if (depth == 5) {
                        choosenCell = cellField[i][j];
                    }
                    if (isGameOver(newBoard, depth)) {
                        int bestValue = calcBestValue(newBoard, turn, state);
                        if (turn == Turn.OURTURN) {
                            if (depth == 5) {
                                choosenCell.value += bestValue*1000;
                            }
                            choosenCell.value += bestValue*depth*depth*depth;
                        }
                        else {
                            if (depth == 4) {
                                choosenCell.value += bestValue*1000;
                            }
                            choosenCell.value += bestValue*depth*depth;
                        }
                        return 0;
                    } 
                    else {
                        valuation(newBoard, choosenCell, getNewTurn(turn), getNewState(state), depth - 1);
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
}