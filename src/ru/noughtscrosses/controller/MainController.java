package ru.noughtscrosses.controller;

import java.util.Random;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

public class MainController {

    @FXML
    private GridPane pnGridBox; //Панель с игровым полем

    @FXML
    private Button btStart; //кнопка "Начать игру"

    @FXML
    private Pane pnRoleLeft; //Левая панель с выбором роли

    @FXML
    private Pane pnRoleRight; //Правая панель с выбором роли

    @FXML
    private Pane pnCross; //Панель с "крестиком"

    @FXML
    private Pane pnNought; //Панель с "ноликом"

    int strokeCount;  //счетчик ходов
    Color drawPenLeft = Color.CORAL;
    Color drawPenRight = Color.AQUAMARINE;
    Shape[] arr; //массив фигур

    //Метод для инициализации объектов
    @FXML
    private void initialize() {
        Polyline polyline = new Polyline(
            new double[] {20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
        );
        Circle circle = new Circle(75, 75, 55);
        circle.setStrokeWidth(10);
        circle.setStroke(drawPenRight);
        circle.setFill(Color.WHITE);
        polyline.setStrokeWidth(10);
        polyline.setStroke(drawPenLeft);
        pnCross.getChildren().add(polyline);
        pnNought.getChildren().add(circle);
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/computer.jpg")));
        pnRoleRight.getChildren().add(imageView);
        ImageView imageView2 = new ImageView(new Image(getClass().getResourceAsStream("/ru/noughtscrosses/icons/person.jpg")));
        pnRoleLeft.getChildren().add(imageView2);
    }

    //Обработчик нажатия на кнопку "Начать игру"
    public void btStartButtonAction(ActionEvent actionEvent) {
        strokeCount = 0;
        int rows = 3; //количество строк
        int columns = 3; //количество столбцов
        arr = new Shape[9];
        pnGridBox.getChildren().clear();
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
                arr[i] = circ;
            } 
            else {
                Polyline p = new Polyline(
                    new double[] {20, 20, 130, 130, 75, 75, 20, 130, 130, 20}
                );
                p.setStrokeWidth(10);
                p.setStroke(drawPenLeft);
                arr[i] = p;
            }
        }
    }

    //Обработчик нажатия на панель с игровым полем
    public void pnGridBoxMouseClickedAction(MouseEvent mouseEvent) {
        try {
            Pane target = (Pane) mouseEvent.getTarget();
            if (target.getWidth() <= 150 && target.getChildren().isEmpty()) {
                if ((strokeCount & 1) == 1) {
                    target.getChildren().add(arr[strokeCount]);
                } 
                else {
                    target.getChildren().add(arr[strokeCount]);
                }
                //System.out.println(strokeCount);
                ++strokeCount;
            }
        } 
        catch (Exception ex) {
        }
    }
}
