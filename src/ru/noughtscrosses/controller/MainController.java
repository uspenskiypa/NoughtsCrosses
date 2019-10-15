package ru.noughtscrosses.controller;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import ru.noughtscrosses.objects.Board;
import ru.noughtscrosses.objects.Cell;
import ru.noughtscrosses.objects.State;

public class MainController {

    @FXML
    public GridPane pnGridBox; //панель с игровым полем

    @FXML
    private Button btStart; //кнопка "Начать игру"

    @FXML
    private Pane pnRoleLeft; //левая панель с выбором роли

    @FXML
    private Pane pnRoleRight; //правая панель с выбором роли

    @FXML
    private Pane pnCross; //панель с "крестиком"

    @FXML
    private Pane pnNought; //панель с "ноликом"
    
    @FXML
    private Label lbGameResult; //значок с результатом игры
    
    @FXML
    private Slider sliderPause; //слайдер задержки хода ИИ
    
    @FXML
    private RadioButton rbMaxDifficulty; //переключатель сложности на максимальную
    
    @FXML
    private RadioButton rbLightDifficulty; //переключатель сложности на облегченную

    private int strokeCount;  //счетчик ходов
    private Random rn = new Random(); //объект класса Random
    private Color drawPenLeft = Color.CORAL; //цвет "крестика"
    private Color drawPenRight = Color.AQUAMARINE; //цвет "нолика"
    private Shape[] shapes; //ссылка на массив фигур (крестиков и ноликов)
    private boolean isRoleLeftComputer = false; //левый игрок - компьютер
    private boolean isRoleRightComputer = true; //правый игрок - компьютер
    private Image imageComputer; //изображение иконки компьютера
    private ImageView imageViewLeft; //левый контейнер для изображения
    private Image imagePerson; //изображение иконки человека
    private ImageView imageViewRight; //правый контейнер для изображения
    private Polyline polyline; //ссылка на объект класса Polyline для "крестика"
    private Circle circle; //ссылка на объект класса Circle для "нолика"
    private Board board; //ссылка на объект класса Board - игровое поле
    private Timeline timeline; //для задержек в процессе анимации 
    private static boolean maxDifficulty; //флаг максимальной сложности

    //Инициализирует поля класса-контроллера
    //Запускается в процессе подгрузки fxml файла при старте программы
    @FXML
    private void initialize() {
        polyline = new Polyline( //создание креста по точкам
                new double[]{20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
        );
        circle = new Circle(75, 75, 55); //создание окружности по центру и радиусу
        circle.setStrokeWidth(10); //установка ширины линий
        circle.setStroke(drawPenRight); //установка цвета линий
        circle.setFill(Color.WHITE); //установка внутренней заливки
        polyline.setStrokeWidth(10);
        polyline.setStroke(drawPenLeft);
        pnCross.getChildren().add(polyline); //добавление фигуры на панель
        pnNought.getChildren().add(circle);
        imageComputer = new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/computer.jpg"));
        imagePerson = new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/person.jpg"));
        imageViewLeft = new ImageView(imagePerson);
        imageViewRight = new ImageView(imageComputer);
        pnRoleRight.getChildren().add(imageViewRight); //добавление рисунка на панель
        pnRoleLeft.getChildren().add(imageViewLeft);
        shapes = createShapes(9);
        pnGridBox.setDisable(true);
    }

    //Обработчик нажатия на левую панель с ролью
    //Если текущая роль на панели - компьютер, то она заменяется на человека
    //Если текущая роль на панели - человек, то она заменяется на компьютера
    public void pnRoleLeftMouseClickedAction(MouseEvent mouseEvent) { 
        if (isRoleLeftComputer) {
            isRoleLeftComputer = false;
            imageViewLeft = new ImageView(imagePerson);
        } 
        else {
            isRoleLeftComputer = true;
            imageViewLeft = new ImageView(imageComputer);
        }
        pnRoleLeft.getChildren().clear();
        pnRoleLeft.getChildren().add(imageViewLeft);
    }

    //Обработчик нажатия на правую панель с ролью
    public void pnRoleRightMouseClickedAction(MouseEvent mouseEvent) {
        if (isRoleRightComputer) {
            isRoleRightComputer = false;
            imageViewRight = new ImageView(imagePerson);
        } 
        else {
            isRoleRightComputer = true;
            imageViewRight = new ImageView(imageComputer);
        }
        pnRoleRight.getChildren().clear();
        pnRoleRight.getChildren().add(imageViewRight);
    }

    //Обработчик нажатия на левую панель с крестиком
    //цвет "крестика" заменяется на случайный
    public void pnCrossMouseClickedAction(MouseEvent mouseEvent) {
        drawPenLeft = Color.rgb(rn.nextInt(256), rn.nextInt(256), rn.nextInt(256));
        polyline.setStroke(drawPenLeft);
    }

    //Обработчик нажатия на правую панель с ноликом
    //цвет "нолика" заменяется на случайный
    public void pnNoughtMouseClickedAction(MouseEvent mouseEvent) {
        drawPenRight = Color.rgb(rn.nextInt(256), rn.nextInt(256), rn.nextInt(256));
        circle.setStroke(drawPenRight);
    }

    //Обработчик нажатия на кнопку "Начать игру"
    //Создается игровое поле заданных размеров
    //Если первый игрок - компьютер, то делается первый ход
    //Если второй игрок тоже компьютер, то делаются остальные ходы по очереди
    public void btStartButtonAction(ActionEvent actionEvent) {
        int rows = 3; //количество строк игрового поля
        int columns = 3; //количество столбцов игрового поля
        strokeCount = 0; //первый ход
        lbGameResult.setText("");
        maxDifficulty = rbMaxDifficulty.isSelected();
        board = new Board(columns, rows);
        setNodeDisable(true);
        pnGridBox.setDisable(false);
        pnGridBox.getChildren().clear();
        fillGridPane(pnGridBox, columns, rows);
        changeShapesOpacity(shapes, 0.1);
        if (isRoleLeftComputer) {
            moveFirst();
            if (isRoleRightComputer) {
                pnGridBox.setDisable(true);
                timeline = new Timeline();
                timeline.setCycleCount(8);
                timeline.getKeyFrames().add(getNewKeyFrame());
                timeline.play();
            }
        }
    }
    
    //Создает и заполняет массив shapes фигурами согласно порядку хода игроков
    private Shape[] createShapes(int numShapes) {
        Shape[] shapes = new Shape[numShapes];
        for (int i = 0; i < numShapes; i++) {
            if ((i & 1) == 1) { //если индекс i - нечетное число
                Circle circ = new Circle(75, 75, 55);
                circ.setStrokeWidth(10);
                circ.setStroke(drawPenRight);
                circ.setFill(Color.WHITE);
                circ.setOpacity(0.1);
                shapes[i] = circ;
            } 
            else {
                Polyline p = new Polyline(
                        new double[]{20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
                );
                p.setStrokeWidth(10);
                p.setStroke(drawPenLeft);
                p.setOpacity(0.1);
                shapes[i] = p;
            }
        }
        return shapes; 
    }
    
    //Изменяет непрозрачность фигур массива shapes на opacity
    private void changeShapesOpacity(Shape[] shapes, double opacity) {
        for (Shape shape : shapes) {
            shape.setOpacity(opacity);
            if (shape instanceof Polyline) {
                shape.setStroke(drawPenLeft);
            }
            else {
                shape.setStroke(drawPenRight);
            }
        }
    }
    
    //Создает панели типа Pane и заполняет ими gridPane размерами columns, rows
    private void fillGridPane(GridPane gridPane, int columns, int rows) {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                pane.getStyleClass().add("centre-grid"); //добавление css стиля
                gridPane.add(pane, i, j);
            }
        }
    }
   
    //ИИ делает первый ход и отображает его на игровом поле с использованием анимации
    private void moveFirst() {
        Cell firstCell = board.moveFirst();
        firstCell.state = State.CROSS;
        ((Pane) getNodeFromGridPane(pnGridBox, firstCell.x, firstCell.y)).getChildren().add(shapes[strokeCount]);
        FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue() / 2), shapes[strokeCount]);
        ft.setToValue(1);
        ft.play();
        ++strokeCount;
    }
    
    //Создает и возвращает ссылку на объект типа KeyFrame
    //Во время создания объекта указывается длительность события и
    //само событие - ход очередного игрока в зависимость от номера текущего хода
    private KeyFrame getNewKeyFrame() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(sliderPause.getValue()), (ActionEvent evt) -> {
            State state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
            Cell bestCell = board.getBestCell(state, 5);
            bestCell.state = state;
            ((Pane) getNodeFromGridPane(pnGridBox, bestCell.x, bestCell.y)).getChildren().add(shapes[strokeCount]);
            FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), shapes[strokeCount]);
            ft.setToValue(1);
            ft.play();
            if (isEndGame()) {
                setNodeDisable(false);
                pnGridBox.setDisable(true);
                timeline.stop();
            }
            ++strokeCount;
        });
        return keyFrame;
    }

    //Обработчик нажатия на панель с игровым полем
    //Если была выбрана пустая ячейка, то ход отображается на игровом поле
    //Если ход не привел к завершению матча, то игра продалжается и
    //следующий ход делает ИИ (при условии, что матч проходит против компьютера)
    public void pnGridBoxMouseClickedAction(MouseEvent mouseEvent) {
        try {
            Pane target = (Pane) mouseEvent.getTarget();
            if (target.getWidth() <= 150 && target.getChildren().isEmpty()) {
                target.getChildren().add(shapes[strokeCount]);
                FadeTransition ftn = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), shapes[strokeCount]);
                ftn.setToValue(1);
                ftn.play();
                getCellFromGridPane(target).state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
                ++strokeCount;
                if (isEndGame()) {
                    setNodeDisable(false);
                    pnGridBox.setDisable(true);
                }
                else if (!(!isRoleLeftComputer && !isRoleRightComputer)){
                    pnGridBox.setDisable(true);
                    PauseTransition pause = new PauseTransition(Duration.seconds(sliderPause.getValue()));
                    pause.setOnFinished((ActionEvent evt) -> {
                        State state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
                        Cell bestCell = board.getBestCell(state, 5);
                        bestCell.state = state;
                        ((Pane) getNodeFromGridPane(pnGridBox, bestCell.x, bestCell.y)).getChildren().add(shapes[strokeCount]);
                        FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), shapes[strokeCount]);
                        ft.setToValue(1);
                        ft.play();
                        pnGridBox.setDisable(false);
                        if (isEndGame()) {
                            setNodeDisable(false);
                            pnGridBox.setDisable(true);
                        }
                        ++strokeCount;
                    });
                    pause.play();
                }
            }
        } 
        catch (Exception ex) {
        }
    }

    //Возвращает объект панели по номеру строки и столбца
    private Node getNodeFromGridPane(GridPane gridPane, int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }

    //Рассчитывает и возвразает на основе target нужную ячейку игрового поля
    private Cell getCellFromGridPane(Pane target) {
        int row = GridPane.getRowIndex(target);
        int col = GridPane.getColumnIndex(target);
        return board.getCellField()[row][col];
    }

    //Проверяет матч на завершенность
    //Если матч завершился, то отображает результат на интерфейсе
    private boolean isEndGame() {
        if (board.isSomeoneWon(board.getCellField(), State.CROSS)) {
            lbGameResult.setTextFill(drawPenLeft);
            lbGameResult.setText("Победили крестики!");
            return true;
        } 
        else if (board.isSomeoneWon(board.getCellField(), State.NOUGHT)) {
            lbGameResult.setTextFill(drawPenRight);
            lbGameResult.setText("Победили нолики!");
            return true;
        } 
        else if (board.isEndOfMoves(board.getCellField())) {
            lbGameResult.setTextFill(Color.BLACK);
            lbGameResult.setText("Ничья!");
            return true;
        }
        return false;
    }
    
    //Включение или отключение кнопок
    private void setNodeDisable(boolean disable) {
        pnRoleLeft.setDisable(disable);
        pnRoleRight.setDisable(disable);
        pnCross.setDisable(disable);
        pnNought.setDisable(disable);
        rbMaxDifficulty.setDisable(disable);
        rbLightDifficulty.setDisable(disable);
        sliderPause.setDisable(disable);
        btStart.setDisable(disable);
    }
    
    //Геттер поля maxDifficulty
    public static boolean isMaxDifficulty() {
        return maxDifficulty;
    }
}
