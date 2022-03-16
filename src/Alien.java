import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Alien extends Character {

    int alien_w, alien_h;
    int alienDrop;
    boolean canShoot;
    double shootingChance;
    int hp;

    BufferedImage flippedImg;
    String flippedImgPath;

    public Alien(int x, int y, int speed, int alienDrop, double shootingChance)
    {

        super(x, y, speed);
        this.moveRight = true;
        this.alienDrop = alienDrop;
        this.canShoot = false;
        this.shootingChance = shootingChance;

        imgPath = "resources/droidShip.png";

        //IMAGE SETUP
        try
        {
            this.img = ImageIO.read(getClass().getResource(imgPath));
            alien_w = getImg().getWidth();
            alien_h = getImg().getHeight();
        }
        catch (IOException exception)
        {
            System.out.println("CANT LOAD ALIEN");
            exception.printStackTrace();
        }

    }

    @Override
    void shoot() {

        this.missileCount++;

    }

    public BufferedImage getFlippedImg()
    {
        return flippedImg;
    }

}
