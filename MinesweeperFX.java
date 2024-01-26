package org.minesweeper.minesweeper;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class MinesweeperFX extends Application {
    private static final int SIDE = 9;
    private Tile[][] gameField = new Tile[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private boolean isGameStopped;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        initializeGame(root);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    private void initializeGame(GridPane root) {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                Tile tile = new Tile(x, y);
                tile.setOnMouseClicked(event -> handleTileClick(tile, event));
                root.add(tile, x, y);
                gameField[y][x] = tile;
            }
        }
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = Math.random() < 0.1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x].setMine(isMine);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                Tile tile = gameField[y][x];
                if (!tile.isMine()) {
                    for (Tile neighbor : getNeighbors(tile)) {
                        if (neighbor.isMine()) {
                            tile.incrementMineNeighbors();
                        }
                    }
                }
            }
        }
    }

    private void handleTileClick(Tile tile, javafx.scene.input.MouseEvent event) {
        if (isGameStopped) {
            restart();
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            openTile(tile);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            markTile(tile);
        }
    }

    private void openTile(Tile tile) {
        if (tile.isOpen() || tile.isFlag() || isGameStopped) {
            return;
        }
        tile.open();
        countClosedTiles--;

        if (tile.isMine()) {
            tile.setText(MINE);
            gameOver();
            return;
        } else if (tile.getMineNeighbors() == 0) {
            tile.setText("");
            for (Tile neighbor : getNeighbors(tile)) {
                openTile(neighbor);
            }
        } else {
            tile.setText(Integer.toString(tile.getMineNeighbors()));
        }

        score += 5;
        if (countClosedTiles == countMinesOnField) {
            win();
        }
    }

    private void markTile(Tile tile) {
        if (tile.isOpen() || isGameStopped || (countFlags == 0 && !tile.isFlag())) {
            return;
        }

        if (tile.isFlag()) {
            countFlags++;
            tile.setFlag(false);
            tile.setText("");
        } else {
            countFlags--;
            tile.setFlag(true);
            tile.setText(FLAG);
        }
    }

    private void gameOver() {
        showMessage("GAME OVER");
        isGameStopped = true;
    }

    private void win() {
        showMessage("YOU WIN");
        isGameStopped = true;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int newX = tile.x + dx;
                int newY = tile.y + dy;

                // Skip the current tile itself
                if (dx == 0 && dy == 0) {
                    continue;
                }

                // Check if the new coordinates are within bounds
                if (newX >= 0 && newX < SIDE && newY >= 0 && newY < SIDE) {
                    neighbors.add(gameField[newY][newX]);
                }
            }
        }

        return neighbors;
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void restart() {
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        isGameStopped = false;

        GridPane root = new GridPane();
        initializeGame(root);

        Scene scene = new Scene(root);
        Stage stage = (Stage) gameField[0][0].getScene().getWindow();
        stage.setScene(scene);


    }
    private static class Tile extends javafx.scene.control.Button {
        private final int x;
        private final int y;
        private boolean isMine;
        private boolean isOpen;
        private boolean isFlag;
        private int mineNeighbors;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
            this.setMinSize(30, 30);
            this.setMaxSize(30, 30);
        }

        public boolean isMine() {
            return isMine;
        }

        public void setMine(boolean mine) {
            isMine = mine;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void open() {
            isOpen = true;
            setDisable(true);

            if (!isMine && mineNeighbors > 0) {
                // Установите цвет для текста плитки в зависимости от количества мин вокруг
                switch (mineNeighbors) {
                    case 1:
                        setTextFill(Color.BLUE);
                        break;
                    case 2:
                        setTextFill(Color.GREEN);
                        break;
                    case 3:
                        setTextFill(Color.RED);
                        break;
                    case 4:
                        setTextFill(Color.PURPLE);
                        break;
                    case 5:
                        setTextFill(Color.DARKRED);
                        break;
                    case 6:
                        setTextFill(Color.TEAL);
                        break;
                    case 7:
                        setTextFill(Color.BLACK);
                        break;
                    case 8:
                        setTextFill(Color.GRAY);
                        break;
                    default:
                        setTextFill(Color.BLACK);
                        break;
                }
            }
        }
        public boolean isFlag() {
            return isFlag;
        }

        public void setFlag(boolean flag) {
            isFlag = flag;
        }

        public int getMineNeighbors() {
            return mineNeighbors;
        }

        public void incrementMineNeighbors() {
            mineNeighbors++;
        }
    }
}
