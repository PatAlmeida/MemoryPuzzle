import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class GameWindow {

    private static int WIDTH = 512;
    private static int HEIGHT = 512;
    private static int NUM_TILES = 6;
    private static int IBGX = 11; // INNER_BACKGROUND_X
    private static int IBGY = 11; // INNER_BACKGROUND_Y
    /* private static int SB = (WIDTH - 2*IBGX - NUM_TILES*Tile.SIZE) / (NUM_TILES + 1); */
    private static int SB = 10; // SPACE_BETWEEN
    private static int INIT_WAIT_TO_HIDE_ANIM_COUNT = 100;
    private static Color BG_COLOR = Color.rgb(186, 186, 186);
    private static Color INNER_COLOR = Color.rgb(150, 150, 150);

    private int tilesRevealed, waitToHideAnimCount, numCorrect, turns;
    private boolean waitingToHide;
    private Tile chosenTile;
    private Tile[][] tiles;

    public GameWindow(Stage myStage) {

        StackPane root = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        scene.setOnMouseMoved(e -> {
            updateTileHovers((int) e.getX(), (int) e.getY());
        });
        scene.setOnMouseClicked(e -> {
            if (tilesRevealed < 2) {
                for (int i = 0; i < NUM_TILES; i++) {
                    for (int j = 0; j < NUM_TILES; j++) {
                        Tile tile = tiles[i][j];
                        if (tile.getHover() && !tile.getShowImage()) {
                            tile.reveal();
                            tilesRevealed++;
                            if (tilesRevealed == 1) chosenTile = tile;
                            if (tilesRevealed == 2) {
                                turns++;
                                if (chosenTile.getImageStr().equals(tile.getImageStr())) {
                                    chosenTile.matched();
                                    tile.matched();
                                    tilesRevealed = 0;
                                    numCorrect += 2;
                                    if (numCorrect == NUM_TILES * NUM_TILES) {
                                        showWinMessage();
                                    }
                                } else {
                                    waitingToHide = true;
                                }
                            }
                        }
                    }
                }
            }
        });

        tilesRevealed = 0;
        waitToHideAnimCount = INIT_WAIT_TO_HIDE_ANIM_COUNT;
        numCorrect = 0;
        turns = 0;
        waitingToHide = false;
        chosenTile = null;
        tiles = new Tile[NUM_TILES][NUM_TILES];
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                tiles[i][j] = new Tile(i, j, IBGX + SB, SB);
            }
        }
        assignTileImages();

        // Game loop - runs at (about) 60fps
        new AnimationTimer() {
            public void handle(long nano) {
                showBackground(gc);
                showTiles(gc);
                doAnimations();
            }
        }.start();

        myStage.setTitle("Memory Puzzle");
        myStage.setScene(scene);
        myStage.show();

    }

    private void showBackground(GraphicsContext gc) {
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(INNER_COLOR);
        gc.fillRect(IBGX, IBGY, WIDTH - 2*IBGX, HEIGHT - 2*IBGY);
    }

    private void showTiles(GraphicsContext gc) {
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                tiles[i][j].show(gc);
            }
        }
    }

    private void updateTileHovers(int mouseX, int mouseY) {
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                tiles[i][j].updateHover(mouseX, mouseY);
            }
        }
    }

    private void doAnimations() {
        if (waitingToHide) {
            waitToHideAnimCount--;
            if (waitToHideAnimCount == 0) {
                for (int i = 0; i < NUM_TILES; i++) {
                    for (int j = 0; j < NUM_TILES; j++) {
                        Tile tile = tiles[i][j];
                        if (tile.getShowImage() && !tile.getCorrect()) {
                            tile.hide();
                        }
                    }
                }
                tilesRevealed = 0;
                waitingToHide = false;
                waitToHideAnimCount = INIT_WAIT_TO_HIDE_ANIM_COUNT;
            }
        }
    }

    private void showWinMessage() {
        JOptionPane.showMessageDialog(null, "You Win! - " + turns + " Moves",
                "You Win!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignTileImages() {
        int[] nums = new int[18];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = 2;
        }
        int tileCount = 0;
        while (tileCount < nums.length * 2) {
            int rand = (int) (Math.random() * nums.length);
            if (nums[rand] != 0) {
                nums[rand]--;
                rand++;
                String str = "";
                if (rand < 10) str += "0";
                str += rand;
                tiles[tileCount % NUM_TILES][tileCount / NUM_TILES].setupImage(str);
                tileCount++;
            }
        }
    }

}
