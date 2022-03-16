import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Character {

    int player_w, player_h;
    int hp = 3;

    BufferedImage destroyedImg;
    String destroyedImgPath;

    public Player(int x, int y, int speed)
    {

        super(x, y, speed);

        imgPath = "resources/player.png";
        destroyedImgPath = "resources/shipDestroyed.png";
        //IMAGE SETUP
        try
        {

            //NORMAL
            this.img = ImageIO.read(getClass().getResource(imgPath));
            this.player_w = this.getImg().getWidth();
            this.player_h = this.getImg().getHeight();
            //DESTROYED
            this.destroyedImg = ImageIO.read(getClass().getResource(destroyedImgPath));

        }
        catch (IOException exception)
        {
            System.out.println("CANT LOAD PLAYER");
            exception.printStackTrace();
        }

    }

    @Override
    void shoot() {

        this.missileCount++;

    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public BufferedImage getDestroyedImg()
    {
        return destroyedImg;
    }

}
