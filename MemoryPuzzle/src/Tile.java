import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Images are taken from the Darkest Dungeon wiki:
// https://darkestdungeon.gamepedia.com/Darkest_Dungeon_Wiki

public class Tile {

    public static final int SIZE = 70;

    private static int IMAGE_SIZE = SIZE - 10;
    private static int INIT_FLIP_ANIM_COUNT = 30;
    private static Color COL = Color.WHITE;
    private static Color DEFAULT_BORDER_COL = Color.BLACK;
    private static Color HOVER_BORDER_COL = Color.ORANGE;
    private static Color CORRECT_BORDER_COL = Color.rgb(0, 255, 0);

    private int x, y, revealAnimCount, hideAnimCount;
    private boolean hover, showImage, correct, revealing, hiding;
    private String imageIDStr;
    private Image image;

    public Tile(int xIndex, int yIndex, int offset, int spaceBetween) {
        x = offset + xIndex * (SIZE + spaceBetween);
        y = offset + yIndex * (SIZE + spaceBetween);
        revealAnimCount = INIT_FLIP_ANIM_COUNT;
        hideAnimCount = INIT_FLIP_ANIM_COUNT;
        hover = false;
        showImage = false;
        correct = false;
        revealing = false;
        hiding = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean getHover() { return hover; }
    public boolean getShowImage() { return showImage; }
    public boolean getCorrect() { return correct; }
    public String getImageStr() { return imageIDStr; }

    public void show(GraphicsContext gc) {
        gc.setFill(DEFAULT_BORDER_COL);
        if (hover) gc.setFill(HOVER_BORDER_COL);
        if (correct) gc.setFill(CORRECT_BORDER_COL);
        gc.fillRect(x, y, SIZE, SIZE);
        gc.setFill(COL);
        gc.fillRect(x+3, y+3, SIZE-6, SIZE-6);
        if (showImage) gc.drawImage(image, x+5, y+5);
        if (revealing) {
            int yPos = y+5 + (-2*revealAnimCount + IMAGE_SIZE);
            int height = revealAnimCount * 2;
            gc.setFill(COL);
            gc.fillRect(x+5, yPos, IMAGE_SIZE, height);
            revealAnimCount--;
            if (revealAnimCount == 0) {
                revealing = false;
                revealAnimCount = INIT_FLIP_ANIM_COUNT;
            }
        }
        if (hiding) {
            int yPos = y+5 + (hideAnimCount * 2);
            int height = -2*hideAnimCount + IMAGE_SIZE;
            gc.setFill(COL);
            gc.fillRect(x+5, yPos, IMAGE_SIZE, height);
            hideAnimCount--;
            if (hideAnimCount == 0) {
                hiding = false;
                showImage = false;
                hideAnimCount = INIT_FLIP_ANIM_COUNT;
            }
        }
    }

    public void reveal() {
        showImage = true;
        revealing = true;
    }

    public void hide() {
        hiding = true;
    }

    public void matched() {
        correct = true;
    }

    public void updateHover(int mouseX, int mouseY) {
        boolean xBool = x < mouseX && (x + SIZE) > mouseX;
        boolean yBool = y < mouseY && (y + SIZE) > mouseY;
        hover = xBool && yBool;
    }

    public void setupImage(String imageStr) {
        imageIDStr = imageStr;
        image = new Image("file:images/" + imageStr + ".png");
    }

}
