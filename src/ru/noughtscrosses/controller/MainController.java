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

    private int strokeCount;  //счетчик ходов
    private Random rn = new Random();
    private Color drawPenLeft = Color.CORAL;
    private Color drawPenRight = Color.AQUAMARINE;
    private Shape[] arr; //массив фигур
    private boolean isRoleLeftComputer = false;
    private boolean isRoleRightComputer = true;
    private Image imageComputer;
    private ImageView imageViewLeft;
    private Image imagePerson;
    private ImageView imageViewRight;
    private Polyline polyline;
    private Circle circle;
    private Board board;
    private Timeline timeline;

    //Метод для инициализации объектов
    @FXML
    private void initialize() {
        polyline = new Polyline(
                new double[]{20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
        );
        circle = new Circle(75, 75, 55);
        circle.setStrokeWidth(10);
        circle.setStroke(drawPenRight);
        circle.setFill(Color.WHITE);
        polyline.setStrokeWidth(10);
        polyline.setStroke(drawPenLeft);
        pnCross.getChildren().add(polyline);
        pnNought.getChildren().add(circle);
        imageComputer = new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/computer.jpg"));
        imagePerson = new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/person.jpg"));
        imageViewLeft = new ImageView(imagePerson);
        imageViewRight = new ImageView(imageComputer);
        pnRoleRight.getChildren().add(imageViewRight);
        pnRoleLeft.getChildren().add(imageViewLeft);
    }

    //Обработчик нажатия на левую панель с ролью
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
    public void pnCrossMouseClickedAction(MouseEvent mouseEvent) {
        drawPenLeft = Color.rgb(rn.nextInt(256), rn.nextInt(256), rn.nextInt(256));
        polyline.setStroke(drawPenLeft);
    }

    //Обработчик нажатия на правую панель с ноликом
    public void pnNoughtMouseClickedAction(MouseEvent mouseEvent) {
        drawPenRight = Color.rgb(rn.nextInt(256), rn.nextInt(256), rn.nextInt(256));
        circle.setStroke(drawPenRight);
    }

    //Обработчик нажатия на кнопку "Начать игру"
    public void btStartButtonAction(ActionEvent actionEvent) throws Exception {
        strokeCount = 0;
        int rows = 3; //количество строк
        int columns = 3; //количество столбцов
        arr = new Shape[9];
        board = new Board(columns, rows);
        pnGridBox.setDisable(false);
        pnRoleLeft.setDisable(true);
        pnRoleRight.setDisable(true);
        pnGridBox.getChildren().clear();
        lbGameResult.setText("");
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                pane.getStyleClass().add("centre-grid");
                pnGridBox.add(pane, i, j);
            }
        }
        for (int i = 0; i < arr.length; i++) {
            if ((i & 1) == 1) {
                Circle circ = new Circle(75, 75, 55);
                circ.setStrokeWidth(10);
                circ.setStroke(drawPenRight);
                circ.setFill(Color.WHITE);
                circ.setOpacity(0.1);
                arr[i] = circ;
            } 
            else {
                Polyline p = new Polyline(
                        new double[]{20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
                );
                p.setStrokeWidth(10);
                p.setStroke(drawPenLeft);
                p.setOpacity(0.1);
                arr[i] = p;
            }
        }
        if (isRoleLeftComputer) {
            Cell firstCell = board.moveFirst();
            firstCell.state = State.CROSS;
            ((Pane)getNodeFromGridPane(pnGridBox, firstCell.x, firstCell.y)).getChildren().add(arr[strokeCount]);
            FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), arr[strokeCount]);
            ft.setToValue(1);
            ft.play();
            ++strokeCount;
            if (isRoleRightComputer) {
                timeline = new Timeline();
                timeline.setCycleCount(8);
                timeline.getKeyFrames().add(getNewKeyFrame());
                btStart.setDisable(true);
                pnGridBox.setDisable(true);
                timeline.play();
            }
        }
    }
    
    private KeyFrame getNewKeyFrame() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(sliderPause.getValue()), (ActionEvent evt) -> {
            State state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
            Cell bestCell = board.getBestCell(state, 5);
            bestCell.state = state;
            ((Pane) getNodeFromGridPane(pnGridBox, bestCell.x, bestCell.y)).getChildren().add(arr[strokeCount]);
            FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), arr[strokeCount]);
            ft.setToValue(1);
            ft.play();
            if (isEndGame()) {
                pnGridBox.setDisable(true);
                btStart.setDisable(false);
                pnRoleLeft.setDisable(false);
                pnRoleRight.setDisable(false);
                timeline.stop();
            }
            ++strokeCount;
        });
        return keyFrame;
    }

    //Обработчик нажатия на панель с игровым полем
    public void pnGridBoxMouseClickedAction(MouseEvent mouseEvent) {
        try {
            Pane target = (Pane) mouseEvent.getTarget();
            if (target.getWidth() <= 150 && target.getChildren().isEmpty()) {
                target.getChildren().add(arr[strokeCount]);
                FadeTransition ftn = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), arr[strokeCount]);
                ftn.setToValue(1);
                ftn.play();
                getCellFromGridPane(target).state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
                ++strokeCount;
                if (isEndGame()) {
                    pnGridBox.setDisable(true);
                    pnRoleLeft.setDisable(false);
                    pnRoleRight.setDisable(false);
                }
                else if (!(!isRoleLeftComputer && !isRoleRightComputer)){
                    PauseTransition pause = new PauseTransition(Duration.seconds(sliderPause.getValue()));
                    pause.setOnFinished((ActionEvent evt) -> {
                        State state = ((strokeCount & 1) == 1) ? State.NOUGHT : State.CROSS;
                        Cell bestCell = board.getBestCell(state, 5);
                        bestCell.state = state;
                        ((Pane) getNodeFromGridPane(pnGridBox, bestCell.x, bestCell.y)).getChildren().add(arr[strokeCount]);
                        FadeTransition ft = new FadeTransition(Duration.seconds(sliderPause.getValue()/2), arr[strokeCount]);
                        ft.setToValue(1);
                        ft.play();
                        btStart.setDisable(false);
                        pnGridBox.setDisable(false);
                        if (isEndGame()) {
                            pnGridBox.setDisable(true);
                            pnRoleLeft.setDisable(false);
                            pnRoleRight.setDisable(false);
                        }
                        ++strokeCount;
                    });
                    btStart.setDisable(true);
                    pnGridBox.setDisable(true);
                    pause.play();
                }
            }
        } 
        catch (Exception ex) {
        }
    }

    //Возвращает объект панели по индексу
    private Node getNodeFromGridPane(GridPane gridPane, int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }

    //Возвращает объект панели по индексу
    private Cell getCellFromGridPane(Pane target) {
        return board.getCellField()[GridPane.getRowIndex(target)][GridPane.getColumnIndex(target)];
    }

    //Возвращает объект панели по индексу
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
}
