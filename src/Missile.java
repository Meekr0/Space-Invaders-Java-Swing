import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Missile
{

    public BufferedImage img;
    public int speed;
    public int x, y;
    public boolean shotByPlayer;
    public boolean isVisible = true;

    public int missile_w, missile_h;
    public String imgPath;

    public Missile(int x, int y, int speed, boolean shotByPlayer) {

        this.x = x;
        this.y = y;
        this.speed = speed;
        this.shotByPlayer = shotByPlayer;

        if(shotByPlayer)
            imgPath = "resources/playerLaser.png";
        else
            imgPath = "resources/alienLaser.png";

        //IMAGE SETUP
        try
        {
            this.img = ImageIO.read(getClass().getResource(imgPath));
            this.missile_w = this.getImg().getWidth();
            this.missile_h = this.getImg().getHeight();
        }
        catch (IOException exception)
        {
            System.out.println("CANT LOAD MISSILE");
            exception.printStackTrace();
        }

    }

    public BufferedImage getImg() {
        return img;
    }

}
