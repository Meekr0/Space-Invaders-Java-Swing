import javax.swing.*;

public class MainWindow extends JFrame {


    GamePanel gamePanel;

    public MainWindow()
    {

        InitUI();

    }

    private void InitUI()
    {

        this.setSize(1200, 1000);

        this.setResizable(false);
        this.setTitle("Space Invaders");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gamePanel = new GamePanel(this.getWidth(), this.getHeight());
        this.add(gamePanel);

        this.setVisible(true);

    }

}
