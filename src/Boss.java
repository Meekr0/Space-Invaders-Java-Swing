import javax.imageio.ImageIO;
import java.io.IOException;

public class Boss extends Alien {

    public Boss(int x, int y, int speed, int alienDrop, double shootingChance) {

        super(x, y, speed, alienDrop, shootingChance);

        this.canShoot = true;
        this.hp = 50;

        imgPath = "resources/boss.png";
        flippedImgPath = "resources/bossFlipped.png";

        //IMAGE SETUP
        try
        {
            this.img = ImageIO.read(getClass().getResource(imgPath));
            this.flippedImg = ImageIO.read(getClass().getResource(flippedImgPath));
            alien_w = getImg().getWidth();
            alien_h = getImg().getHeight();
        }
        catch (IOException exception)
        {
            System.err.println("CANT LOAD BOSS");
            exception.printStackTrace();
        }

    }

    @Override
    void shoot() {

        this.missileCount += 5;

    }

}
