import java.awt.image.BufferedImage;

public class Character {

    public int x, y, speed;
    public BufferedImage img;
    public boolean moveLeft, moveRight;
    public boolean isVisible;
    public String imgPath;
    public int missileCount = 0;

    public Character(int x, int y, int speed)
    {
        this.x = x;
        this.y = y;
        this.speed = speed;

        this.isVisible = true;

        this.moveLeft = false;
        this.moveRight = false;
    }

    public BufferedImage getImg() {
        return img;
    }

    void shoot()
    {
        System.out.println("PEWPEW");
    }

}
