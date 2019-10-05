package ru.noughtscrosses.objects;

public class Cell {
    public State state; //статус ячейки
    public int value; //ценность ячейки
    public int x, y; //координаты ячейки
    
    public Cell(State state, int x, int y) {
        this.state = state;
        this.y = y;
        this.x = x;
        this.value = 0;
    }
    
    public Cell(Cell cell) {
        this.state = cell.state;
        this.x = cell.x;
        this.y = cell.y;
        this.value = cell.value;
    }
}
