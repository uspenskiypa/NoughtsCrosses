package ru.noughtscrosses.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import ru.noughtscrosses.controller.MainController;

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

    //Геттер поля cellField
    public Cell[][] getCellField() {
        return cellField;
    }

    //Возвращает случайную ячейку игрового поля
    public Cell moveFirst() {
        return cellField[rn.nextInt(height)][rn.nextInt(width)];
    }

    //Проверяет игровое поле board на ниличие выиграшной комбинации для state
    //Если она есть вернет true, иначе false
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

    //Проверяет кончились ли ходы на игровом поле board
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

    //Проверяет произошло ли завершение матча на игровом поле board
    private boolean isGameOver(Cell[][] board) {
        if (isSomeoneWon(board, State.CROSS) || isSomeoneWon(board, State.NOUGHT) || isEndOfMoves(board)) {
            return true;
        } 
        else {
            return false;
        }
    }

    //В зависимости от итоговой ситуации на игровом поле board,
    //а также от текущего хода turn и состояния state
    //возвращает результирующее значение
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

    //Возвращает ячейку с наивысшим значением для состояния state 
    //и при глубине рекурсионного погружения depth.
    //Подсчитывается ценность пустых ячеек игрового поля,
    //далее ячейки с максимальной ценностью заносятся в список и
    //возвращается случайная ячейка из этого списка.
    public Cell getBestCell(State state, int depth) {
        valuation(cellField, null, Turn.OURTURN, state, depth);
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Cell> listBestCell = new ArrayList<>();
        for (Cell[] cells : cellField) {
            for (Cell cell : cells) {
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
        for (Cell[] cells : cellField) { 
            for (Cell cell : cells) {
                cell.value = 0; //Обнуляем ценность ячеек перед следующим ходом
            }
        }
        return bestCell;
    }

    /* Рекурсивный обход дерева ходов игрука до заданной глубины рекурсии
    и расчет значений (ценности) текущих свободных ячеек.
    board - двумерный массив ячеек
    cell - ячейка, для которой рассчитывается ценность 
    turn - чей ход (противника или наш)
    state - состояние хода (ходят крестики или нолики) 
    depth - глубина рекурсии */
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
                    if (isGameOver(newBoard)) {
                        int bestValue = calcBestValue(newBoard, turn, state);
                        cell.value += changeBestValue(bestValue, depth);
                        if (depth == 5) {
                            cell.value += 600;
                            return 0;
                        } 
                        else if (depth == 4) {
                            cell.value -= 200;
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
    
    //Корректирует ценнсость в зависимости от сложности, 
    //при максимальной сложности учитывается глубина рекурсии
    public int changeBestValue(int bestValue, int depth) {
        if (MainController.isMaxDifficulty()) {
            return bestValue * depth;
        } 
        else {
            return bestValue;
        }
    }
    
    //Возвращает очередное состояние State
    public State getNewState(State state) {
        return state == State.CROSS ? State.NOUGHT : State.CROSS;
    }

    //Возвращает очередной ход Turn
    public Turn getNewTurn(Turn turn) {
        return turn == Turn.OURTURN ? Turn.ENEMYTURN : Turn.OURTURN;
    }
    
    //Изменяет ценность ячейки cell при ситуации на игровом поле board, когда
    //центр принадлежит к вашему state, а противник занял противоположные углы.
    //Увеличивает ценность неугловых ячеек.
    public void smartChangeValue(Cell[][] board, Cell cell, State state) {
        State otherState = getNewState(state); //состояние противника
        if (board[1][1].state == state) {
            if ((board[0][0].state == otherState && board[2][2].state == otherState) 
                    || (board[0][2].state == otherState && board[2][0].state == otherState)) {
                int colEmpty = 0; //количество пустых ячеек
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
                        cell.value += 60;
                        if (MainController.isMaxDifficulty()) {
                            cell.value += 100;
                        }
                    }
                }
            }
        }
    }
}