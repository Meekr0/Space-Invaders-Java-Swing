import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;

public class GamePanel extends JPanel {

    int panelW, panelH;
    int uiPanelSize;

    JLabel scoreLabel;
    JLabel highscoreLabel;

    Player player;
    Alien[] aliens;
    java.util.List<Missile> playerMissiles = new ArrayList<>();
    java.util.List<Missile> alienMissiles = new ArrayList<>();

    Font retroFont;

    //SCORE, HP
    int score;
    int hScore;
    int scoreIncrement;
    int alienWaves;
    String scoreMessage;
    JLabel scoreTextLabel;
    BufferedImage hpImage;
    String hpImgPath;

    //CZAS
    long startTime;
    long endTime;
    long elapsedTime;
    long bestTime;

    boolean isGameRunning;
    boolean isInBossRound;
    int roundsBeforeBoss;

    int aliensHit;
    int aliensInRow;

    int a_speed;
    int a_drop;
    double a_shootingChance;

    boolean createdOneRow = false;

    //ACTIONS
    Action LeftAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.setMoveLeft(true);
        }
    };
    Action RightAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.setMoveRight(true);
        }
    };
    Action LeftReleaseAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.setMoveLeft(false);
        }
    };
    Action RightReleaseAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.setMoveRight(false);
        }
    };
    Action ShootAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.shoot();
        }
    };

    public GamePanel(int width, int height) {

        //SETUP
        panelW = width;
        panelH = height;
        uiPanelSize = 50;

        aliensHit = 0;
        aliensInRow = 0;

        a_speed = 1;
        a_drop = 5;
        a_shootingChance = 0.1d;

        this.setBackground(Color.BLACK);
        player = new Player(panelW / 2,
                4 * panelH / 5, 12);

        //SCORE
        score = 0;
        scoreIncrement = 10;
        isGameRunning = true;
        isInBossRound = false;
        roundsBeforeBoss = 1;
        scoreMessage = "";
        alienWaves = 0;

        startTime = System.currentTimeMillis();

        this.setSize(panelW, panelH);

        //HIGHSCORES
        try {

            BufferedReader bf = new BufferedReader(new FileReader("highscores.txt"));
            hScore = Integer.parseInt(bf.readLine());
            bestTime = Integer.parseInt(bf.readLine());

        }
        catch (IOException e) {
            e.printStackTrace();
            hScore = 0;
            bestTime = 0;
        }

        //HEARTS
        hpImgPath = "resources/heart.png";
        try
        {
            hpImage = ImageIO.read(getClass().getResource(hpImgPath));
        }
        catch (IOException exception)
        {
            System.out.println("CANT LOAD HP");
            exception.printStackTrace();
        }

        //FONT
        try {
            retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("RetroFont.ttf")).deriveFont(30f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(retroFont);
        }
        catch (IOException | FontFormatException e)
        {
            e.printStackTrace();
        }

        //SCORE TEXT SETUP ITD
        this.setLayout(new BorderLayout());
        this.add(scoreTextLabel = new JLabel(scoreMessage));
        scoreTextLabel.setSize(panelW * 2 / 3, panelH / 2);
        scoreTextLabel.setLocation(panelW / 6, panelH / 4);
        scoreTextLabel.setForeground(Color.GREEN);
        scoreTextLabel.setFont(retroFont);
        scoreTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreTextLabel.setVerticalAlignment(SwingConstants.CENTER);

        //UPPER BAR SET UP
        //SCORE
        this.setLayout(null);
        this.add(scoreLabel = new JLabel("SCORE 0"));
        scoreLabel.setSize(4*panelW/10, uiPanelSize);
        scoreLabel.setLocation(0,0);
        scoreLabel.setForeground(Color.GREEN);
        scoreLabel.setFont(retroFont);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setVerticalAlignment(SwingConstants.CENTER);

        //HIGHSCORES
        this.add(highscoreLabel = new JLabel("HIGHSCORE: " + hScore));
        highscoreLabel.setSize(4*panelW/10, uiPanelSize);
        highscoreLabel.setLocation(panelW-4*panelW/10,0);
        highscoreLabel.setForeground(Color.GREEN);
        highscoreLabel.setFont(retroFont);
        highscoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        highscoreLabel.setVerticalAlignment(SwingConstants.CENTER);

        //CREATE NEW ALIENS
        createNewAliens(a_speed, a_drop, a_shootingChance);

        //TIMER
        Timer timer = new Timer(25, (ActionEvent e) -> {
            this.repaint();
        });
        timer.start();

        //KEYBINDINGS
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "leftAction");
        getActionMap().put("leftAction", LeftAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "leftReleaseAction");
        getActionMap().put("leftReleaseAction", LeftReleaseAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "rightAction");
        getActionMap().put("rightAction", RightAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "rightReleaseAction");
        getActionMap().put("rightReleaseAction", RightReleaseAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "shootAction");
        getActionMap().put("shootAction", ShootAction);

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        //DRAW PLAYER
        if(player.hp>0) //IF PLAYER IS ALIVE
            g.drawImage(player.getImg(), player.x, player.y, this);
        else //IF PLAYER IS DESTROYED
            g.drawImage(player.getDestroyedImg(), player.x, player.y, this);

        //MOVE THE ALIENS
        moveAliens();

        //MOVE PLAYER
        if (player.moveLeft && player.x >= 0 && isGameRunning)
            player.x -= player.speed;
        else if (player.moveRight && player.x + player.player_w <= panelW && isGameRunning)
            player.x += player.speed;

        //DRAW ALIENS
        for (int i = 0; i < aliens.length; i++)
        {
            if(!isInBossRound) {
                if (aliens[i].isVisible)
                    g.drawImage(aliens[i].getImg(), aliens[i].x, aliens[i].y, this);
            }
            else
            {

                if(aliens[i].moveRight)
                    g.drawImage(aliens[i].getImg(), aliens[i].x, aliens[i].y, this);
                else
                    g.drawImage(aliens[i].getFlippedImg(), aliens[i].x, aliens[i].y, this);

            }
        }

        //CREATE A NEW MISSILE FOR THE PLAYER
        if (player.missileCount != 0 && isGameRunning) {
            //TODO - TRZEBA UWZGLĘDNIĆ SZEROKOŚĆ POCISKU PRZY WYSTRZALE, BY BYŁO W CENTRUM GRACZA
            playerMissiles.add(new Missile(player.x + player.player_w / 2 - 7/2, //7/2 -> POLOWA SZEROKOSCI LASERA
                    player.y - 21, 20, true));
            player.missileCount -= 1;
        }

        //SHOOTING PLAYER MISSILES, DESTROYING ALIENS
        for (Missile m : playerMissiles) {

            //DRAW AND MOVE MISSILES
            if (m.isVisible)
                g.drawImage(m.getImg(), m.x, m.y, this);
            if(isGameRunning)
                m.y -= m.speed;

            //CHECK FOR COLLISIONS
            //ALIENS
            for (int i = 0; i < aliens.length; i++) {
                //CZY PIERWSZY RAZ TRAFIONY

                if (aliens[i].isVisible) {

                    //CZY TRAFIONY
                    if (aliens[i].x - m.missile_w <= m.x && aliens[i].x + aliens[i].alien_w + m.missile_w >= m.x
                            &&
                            aliens[i].y + aliens[i].alien_h >= m.y && aliens[i].y <= m.y) {

                        //TRAFIENIE -> USTAWIANIE isVisible na false, zliczanie trafień
                        if (m.isVisible) {

                            score += scoreIncrement;
                            scoreLabel.setText("Score: " + score);
                            if(score > hScore)
                                highscoreLabel.setText("Highscore: " + score);

                            if(!isInBossRound) {

                                aliens[i].isVisible = false;
                                aliensHit++;

                                //POZWALANIE STRZELAĆ KOSMICIE WYŻEJ
                                if (i >= aliensInRow)
                                    aliens[i - aliensInRow].canShoot = true;

                            }
                            else
                            {

                                aliens[i].hp--;
                                if(aliens[i].hp == 0) {
                                    aliens[i].isVisible = false;
                                    aliensHit++;
                                }

                            }


                        }

                        m.isVisible = false;

                        //CZY WSZYSCY TRAFIENI, JEŻELI TAK TO RESTART I NOWY WAVE
                        if (aliensHit == aliens.length) {

                            //REMOVING EXISTING MISSILES ON RESET
                            for (Missile missile : playerMissiles)
                                if (missile.isVisible)
                                    missile.isVisible = false;
                            for (Missile missile : alienMissiles)
                                if (missile.isVisible)
                                    missile.isVisible = false;

                            alienWaves++;

                            //MAKE ALIENS HARDER
                            a_speed += 1;
                            a_drop += 1;
                            a_shootingChance += 0.01d;

                            aliensHit = 0;
                            if(alienWaves < roundsBeforeBoss) //NORMALNE RUNDY
                            {

                                createNewAliens(a_speed, a_drop, a_shootingChance);


                            } //BOSSFIGHT
                            else if(alienWaves == roundsBeforeBoss){
                                createBoss();
                                isInBossRound = true;
                            }
                            else //AFTER THE BOSS -> WIN
                            {
                                isInBossRound = false;
                                long o_bestTime = bestTime;
                                endTime = System.currentTimeMillis();
                                elapsedTime = endTime - startTime;

                                if(elapsedTime < bestTime || bestTime == 0L)
                                    bestTime = elapsedTime;

                                //ZAPISYWANIE DO PLIKU
                                try {

                                    FileWriter fw = new FileWriter("highscores.txt");

                                    if(score > hScore)
                                        fw.write(score + "\r\n");
                                    else fw.write(hScore + "\r\n");
                                    fw.write(bestTime + "");

                                    fw.flush();
                                    fw.close();

                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //DISPLAY SCORE
                                int elapsedTimeFullSec = (int)elapsedTime / 1000;
                                int elapsedTimeDecSec = (int)elapsedTime % 1000;

                                int bestTimeFullSec = (int)o_bestTime / 1000;
                                int bestTimeDecSec = (int)o_bestTime % 1000;

                                scoreMessage = "Game Over. Your final time was " + elapsedTimeFullSec + "." + elapsedTimeDecSec / 10 + " seconds," +
                                        " which was" + (o_bestTime == 0 || elapsedTime < o_bestTime ? " " : " not ") +
                                        "better than your best time" + (o_bestTime == 0 ? "." : (" of " + bestTimeFullSec + "." + bestTimeDecSec / 10));

                                scoreTextLabel.setText("<html>" + scoreMessage + "</html>");

                            }


                        }

                    }

                }

            }
            //OTHER MISSILES
            for (int i = 0; i < alienMissiles.size(); i++) {

                if (m.isVisible && alienMissiles.get(i).isVisible) {

                    if (alienMissiles.get(i).x - m.missile_w <= m.x &&
                            alienMissiles.get(i).x + alienMissiles.get(i).missile_w + m.missile_w >= m.x &&
                            alienMissiles.get(i).y + alienMissiles.get(i).missile_h >= m.y &&
                            alienMissiles.get(i).y <= m.y) {
                        m.isVisible = false;
                        alienMissiles.get(i).isVisible = false;

                        score += scoreIncrement / 2;
                        scoreLabel.setText("Score: " + score);
                        if(score > hScore)
                            highscoreLabel.setText("Highscore: " + score);

                    }

                }

            }

        }

        //CREATE A NEW MISSILE FOR THE ALIEN
        for (int i = 0; i < aliens.length; i++) {

            if(!isInBossRound)
            {

                if (aliens[i].missileCount != 0 && isGameRunning) {
                    alienMissiles.add(new Missile(aliens[i].x + aliens[i].alien_w / 2 - 7 / 2, // -7/2 -> POŁOWA WYMIARU LASERA
                            aliens[i].y + aliens[i].alien_h, 20, false));
                    aliens[i].missileCount -= 1;
                }

            }
            else //JEŻELI BOSS
            {

                if (aliens[i].missileCount != 0 && isGameRunning)
                {

                    for(int j = 0; j < aliens[i].missileCount; j++)
                    {

                        int shotFromX = aliens[i].x + (int) (Math.random() * (double) aliens[i].alien_w);
                        //System.out.println(shotFromX);
                        alienMissiles.add(new Missile(shotFromX - 7 / 2, // -7/2 -> POŁOWA WYMIARU LASERA
                                aliens[i].y + aliens[i].alien_h, 20, false));

                    }

                }

                aliens[i].missileCount = 0;


            }

        }

        //SHOOTING ALIEN MISSILES
        for (Missile m : alienMissiles) {

            //DRAW AND MOVE MISSILES
            if (m.isVisible)
                g.drawImage(m.getImg(), m.x, m.y, this);
            if(isGameRunning)
                m.y += m.speed;

            //CHECK IF PLAYER IS HIT
            if (m.isVisible) {
                if (player.x - m.missile_w <= m.x && player.x + player.player_w + m.missile_w >= m.x
                        && player.y + player.player_h >= m.y && player.y <= m.y) {
                    m.isVisible = false;
                    player.hp--;

                    //ON PLAYER DEATH
                    if (player.hp == 0) {

                        //STOP TIME
                        endTime = System.currentTimeMillis();
                        elapsedTime = endTime - startTime;

                        //REMOVE ALL REMAINING MISSILES
                        for(Missile m1 : alienMissiles)
                            m1.isVisible = false;
                        for(Missile m1 : playerMissiles)
                            m1.isVisible = false;

                        for(Alien a : aliens)
                            a.isVisible = false;


                        //DISPLAY SCORE
                        int elapsedTimeFullSec = (int)elapsedTime / 1000;
                        int elapsedTimeDecSec = (int)elapsedTime % 1000;

                        scoreMessage = "Game Over. After " + elapsedTimeFullSec + "." + elapsedTimeDecSec / 10 +
                                " seconds, you have been shot down. Better luck next time!";

                        scoreTextLabel.setText("<html>" + scoreMessage + "</html>");

                        isGameRunning = false;

                    }

                }

            }

        }

        //DRAW LINE AT THE BORDER OF PANELS
        g.setColor(Color.GREEN);
        g.drawLine(0, uiPanelSize, panelW, uiPanelSize);
        g.drawLine(4*panelW/10, 0, 4*panelW/10, uiPanelSize);
        g.drawLine(panelW - 4*panelW/10, 0, panelW - 4 * panelW / 10, uiPanelSize);

        //DRAW PLAYER'S HP HEARTS
        if(player.hp >= 1)
        {
            g.drawImage(hpImage, 4*panelW / 10, 0, this);
        }
        if(player.hp >= 2)
        {
            g.drawImage(hpImage, 5+4*panelW / 10 + (2 * panelW / 30), 0, this);
        }
        if(player.hp >= 3)
        {
            g.drawImage(hpImage, 10+4*panelW / 10 + (4 * panelW / 30), 0, this);
        }

    }

    public void moveAliens() {

        for (int i = 0; i < aliens.length; i++) {

            if (isGameRunning) {

                if (aliens[i].moveLeft)
                    aliens[i].x -= aliens[i].speed;
                else if (aliens[i].moveRight)
                    aliens[i].x += aliens[i].speed;

            }

        }

        //ROZDZIELENIE NA DWIE PĘTLE ZAPOBIEGA DESYNCHRONIZACJI ORAZ NABIERANIU DODATKOWEJ PRĘDKOŚCI

        for (int i = 0; i < aliens.length; i++) {
            if (aliens[i].isVisible) {

                //JEŻELI DOTRĄ DO PRAWEJ KRAWĘDZI
                if (aliens[i].x + aliens[i].alien_w + 15 >= panelW)
                {
                    //+15 bo inaczej wychodzili za prawą krawędź

                    for (int j = 0; j < aliens.length; j++) {
                        aliens[j].moveLeft = true;
                        aliens[j].moveRight = false;
                        aliens[j].y += aliens[i].alienDrop;
                    }

                }
                else if (aliens[i].x <= 0) //DO LEWEJ
                {

                    for (int j = 0; j < aliens.length; j++) {
                        aliens[j].moveRight = true;
                        aliens[j].moveLeft = false;
                        aliens[j].y += aliens[i].alienDrop;
                    }

                }

                //JEŻELI ZEJDĄ PONIŻEJ GRACZA
                if(aliens[i].y >= player.y)
                {

                    for(Alien a : aliens)
                        a.isVisible = false;

                    //STOP TIME
                    endTime = System.currentTimeMillis();
                    elapsedTime = endTime - startTime;

                    //REMOVE ALL REMAINING MISSILES
                    for(Missile m1 : alienMissiles)
                        m1.isVisible = false;
                    for(Missile m1 : playerMissiles)
                        m1.isVisible = false;

                    for(Alien a : aliens)
                        a.isVisible = false;


                    //DISPLAY SCORE
                    int elapsedTimeFullSec = (int)elapsedTime / 1000;
                    int elapsedTimeDecSec = (int)elapsedTime % 1000;

                    scoreMessage = "Game Over. After " + elapsedTimeFullSec + "." + elapsedTimeDecSec / 10 +
                            " seconds, you have been shot down. Better luck next time!";

                    scoreTextLabel.setText("<html>" + scoreMessage + "</html>");

                    player.hp = 0;
                    isGameRunning = false;

                }

            }

        }

        //RANDOMIZING IF ALIEN WILL SHOOT
        for (int i = 0; i < aliens.length; i++) {

            if (aliens[i].isVisible && aliens[i].canShoot) {
                double rand = (Math.random() * 10);

                if (rand <= aliens[i].shootingChance)
                    aliens[i].missileCount++;
            }

        }

    }

    public void createNewAliens(int speed, int drop, double shootingChance) {

        aliens = new Alien[30];

        int alienDistance = 25;
        int al_x = 2*alienDistance, al_y = uiPanelSize + alienDistance;

        for (int i = 0; i < aliens.length; i++) {

            aliens[i] = new Alien(al_x, al_y, speed, drop, shootingChance);
            if (!createdOneRow) {
                aliensInRow++;
            }

            if (al_x + 2 * aliens[i].alien_w + 2 * alienDistance >= panelW)
            {

                al_x = 2*alienDistance;
                al_y += aliens[i].alien_h + alienDistance;

                createdOneRow = true;

            }
            else
                al_x += alienDistance + aliens[i].alien_w;

        }

        for (int i = 1; i < aliensInRow; i++) {
            aliens[aliens.length - i].canShoot = true;
        }

    }

    public void createBoss() {

        int al_x = 100, al_y = uiPanelSize + 50;

        aliens = new Alien[1];
        aliens[0] = new Boss(al_x, al_y, 3, 0, 0.7d);


    }


}
