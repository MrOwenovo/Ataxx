package com.Ataxx.test;

// Optional Task: The GUI for the Ataxx Game

import javazoom.jl.decoder.JavaLayerException;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Observable;
import java.util.Random;
import java.util.TimerTask;
import java.util.function.Consumer;

import static com.Ataxx.test.GUI.Home.*;
import static com.Ataxx.test.GUI.Menu.*;

class GUI implements View, CommandSource, Reporter {

    // Add some codes here


    ViewServer viewServer = new ViewServerImpl();
    public static Board board;
    public static boolean HomeIsInit = false;
    public static boolean HomeIsDeliver = false;
    public static Home home;
    public static Chess[][] chess=new Chess[7][7];
    public static int[][] blocks = new int[7][7];
    private static boolean isTheFirstTime = true;

    /**
     * Constants used by programs
     */
    public class Constant {


        //Related judgment of Controller

        public static boolean getMyIcon = false;  //Get my avatar
        public static boolean whetherFriendsToTableIndex = false;  //Whether the user name corresponds to the chat table subscript

    }

    /**
     * Jitter interface
     */
    public interface Shakeable {
        void shake();
    }
    /**
     * There is a maximization minimization animation interface
     */
    public interface Minimize {
        public void minimize();

        public void maximize();
    }
    public interface ViewServer {
        public static int PATTERN_MANUAL_VS_MANUAL = 0;
        public static int PATTERN_MANUAL_VS_AI = 1;
        /**
         * The client login page is displayed
         */
        void DisplayConnectedLogin();

        /**
         * The main window of the connected client is displayed
         */
        void DisplayConnectedClient(int pattern,boolean isAddBlocks);
        /**
         * The function of making the left menu of the main interface shrink is used for active shrinkage after background detection
         */
        void MenuShrink();
    }

    public interface MusicService {
        /**
         * Play failure sound
         */
        public void playErrorMP3();

        /**
         * Play success sound
         */
        public void playSuccessMP3();

        /**
         * Play send message sound effects
         */
        public void playSendMessageMP3();

        /**
         * Play the receiving message sound effect
         */
        public void playAcceptMessageMP3();

        /**
         * Play message alert sound effects
         */
        public void playNoticeMP3();
        public void playChessMP3();
        public void playWinMP3();
    }

    class MusicServiceImpl implements MusicService {
        @Override
        public void playErrorMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/error.mp3");
                }
            }.start();
        }

        @Override
        public void playSuccessMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/loginsuccess.mp3");
                }
            }.start();
        }

        @Override
        public void playSendMessageMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/sendmsg.mp3");
                }
            }.start();
        }

        @Override
        public void playAcceptMessageMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/msgsound.mp3");
                }
            }.start();
        }

        @Override
        public void playNoticeMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/notice.mp3");
                }
            }.start();
        }

        @Override
        public void playChessMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/chess1.mp3");
                }
            }.start();

        }

        @Override
        public void playWinMP3() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    PlaySound.play("sound/win.mp3");
                }
            }.start();

        }
    }

    /**
     * A rounded rectangle done by the button calling the printComponent method
     */
    public class RadioButton extends JButton implements MouseListener , MouseMotionListener {
        private Shape shape = null;  //shape

        private Color click = new Color(0, 0, 113);// Default color when pressed

        private Color quit = new Color(237, 234, 228);// Default color when leaving

        private Color put;  //Placement color

        private int arcWidth = 20;  //Horizontal roundness of rounded corners, default 20

        private int arcHeight = 20; //Vertical roundness of rounded corners, default 20
        public RadioButton(String s) {
            super(s);
            addMouseListener(this);
            setContentAreaFilled(false);// Whether to display the surrounding rectangular area Select No
        }

        public void setColor(Color c, Color q,Color p) {
            click = c;
            quit = q;
            put = p;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
        /**
         * Click the button when called,fillRoundRect is printed graphic size and rounded corners,arcWidth control rounded corners,height and other control width
         * @param g The image to print
         */
        public void paintComponent(Graphics g) {
            //CLICK color if pressed or QUIT color, PUT on
            if (getModel().isRollover()&&!getModel().isPressed()) {
                g.setColor(put);
            }else if (getModel().isPressed()){
                g.setColor(click);
            }else{
                g.setColor(quit);
            }
            //Fill the rounded rectangle area for other shapes as well
            g.fillRoundRect(0, 0, getSize().height - 1, getSize().height - 1,
                    arcWidth, arcHeight);
            //It has to be last or it won't work
            super.paintComponent(g);
        }

        /**
         * Draw the rounded button border
         */
        public void paintBorder(Graphics g) {
            //Draw boundary area
            g.setColor(click);
            g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                    arcWidth, arcHeight);
        }

        /**
         * If you change the arcw and the arch up here, you change the arch up here
         * @param x Default 0
         * @param y Default 0
         * @return Returns shape to call contains
         */
        public boolean contains(int x, int y) {
            //Determine if the point (x,y) is inside the button
            if (shape == null || !(shape.getBounds().equals(getBounds()))) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        arcWidth, arcHeight);
            }
            return shape.contains(x, y);
        }

        /**
         * Set the horizontal and vertical corners of the button
         * @param arcWidth
         * @param arcHeight
         */
        public void setArc(int arcWidth, int arcHeight) {
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
        }

    }

    /**
     * Set the horizontal and vertical corners of the button
     * Add Border to achieve anti-aliasing
     * Set round button dots in front and after color
     */
    class RoundButton extends RadioButton {

        @Override
        public boolean contains(int x, int y) {
            return super.contains(x, y);
        }

        @Override
        public void paintBorder(Graphics g) {
            super.paintBorder(g);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public RoundButton(String message, Color click, Color normal, Color put) {
            super(message);
            this.setBorder(new ThreeDimensionalBorder(Color.gray,200,3));
            this.setColor(click,normal,put);

        }
    }

    /**
     * Three-contentareafill (false) : Border; setContentAreaFill(false)
     */
    public class ThreeDimensionalBorder extends AbstractBorder {

        Color color;

        int thickness = 8;

        int radius = 8;

        Insets insets = null;

        BasicStroke stroke = null;

        int strokePad;

        RenderingHints hints;

        int shadowPad = 3;

        public ThreeDimensionalBorder(Color color) {

            this(color, 128, 8);

        }

        /**
         * Create a 3D button with a background
         * @param color Default BLACK
         * @param transparency Default 200
         * @param shadowWidth Default 5
         */
        ThreeDimensionalBorder(Color color, int transparency, int shadowWidth) {

            this.color = color;

            shadowPad = shadowWidth;

            stroke = new BasicStroke(thickness);

            strokePad = thickness / 2;

            hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,

                    RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = radius + strokePad;

            int bottomPad = pad + strokePad + shadowPad;

            int rightPad = pad + strokePad + shadowPad;

            insets = new Insets(pad, pad, bottomPad + shadowPad, rightPad);

        }

        @Override

        public Insets getBorderInsets(Component c) {

            return insets;

        }

        @Override

        public Insets getBorderInsets(Component c, Insets insets) {

            return getBorderInsets(c);

        }

        @Override

        public void paintBorder(Component c, Graphics g, int x, int y, int width,

                                int height) {

            Graphics2D g2 = (Graphics2D) g;

            int bottomLineY = height - thickness - shadowPad;

            RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(0 + strokePad,

                    0 + strokePad, width - thickness - shadowPad, bottomLineY, radius, radius);

            Area area = new Area(bubble);

            g2.setRenderingHints(hints);

            g2.setColor(color);

            g2.setStroke(stroke);

            g2.draw(area);

            Area shadowArea = new Area(new Rectangle(0, 0, width, height));

            shadowArea.subtract(area);

            g.setClip(shadowArea);

            Color shadow = new Color(color.getRed(), color.getGreen(), color.getBlue(),

                    128);

            g2.setColor(shadow);

            g2.translate(shadowPad, shadowPad);

            g2.draw(area);

        }

    }


    public class ViewServerImpl implements ViewServer {

        @Override
        public void DisplayConnectedLogin() {
            Menu.isBacking=false;
            new LoginHome();  //The client login window is displayed
        }

        @Override
        public void DisplayConnectedClient(int pattern,boolean isAddBlocks) {
            //The client login window is displayed
            Home home = new Home();
            GUI.home = home;
            GUI.HomeIsDeliver = true;
            home.setPattern(pattern);
            home.setIsAddBlocks(isAddBlocks);
            home.ServerCloseLoad.setColor(getColorFromHex("#7bc6e5"));

            if (isAddBlocks) {  //如果要随即加入方块
                Random random = new Random();
                int blocksNum = random.nextInt(4) + 1;
                for (int i = 0; i < blocksNum; i++) {
                    int blockX = random.nextInt(7);
                    int blockY = random.nextInt(7);
                    if (blockX == 0 && blockY == 0 || blockX == 0 && blockY == 6 || blockX == 6 && blockY == 6 || blockX == 6 && blockY == 0) {
                        blockX = random.nextInt(7);
                        blockY = random.nextInt(7);
                    }
                    blocks[blockX][blockY] = 1;
                    blocks[6-blockX][blockY] = 1;
                    blocks[6-blockX][6-blockY] = 1;
                    blocks[blockX][6-blockY] = 1;
                    GUI.board.setBlock(XNumberLetters[blockY+1],Character.forDigit(blockX+1,10) );
                }

            }

        }





        @Override
        public void MenuShrink() {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    int WIDTH = WIDTHNOW[0];
                    int MENUWIDTH = 140;
                    keepFlag[0] = false;
                    Home.menuBack.setBounds(110, 20, newMenuIcon.getIconWidth(), newMenuIcon.getIconHeight());

                    label2:
                    {
                        Menu.isShrink = true;  //Be shrinking
                        while (WIDTH > -20 - 180 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth())) {
                            Thread.sleep(1);
                            menuTop.setBounds(WIDTH, 0, newMenuIcon.getIconWidth(), menuIcon.getIconHeight());
                            if (menuFlag[0] && MENUWIDTH > 12)
                                menuHomeUser.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                            if (menuFlag1[0] && MENUWIDTH > 12)
                                menuHomeUser1.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                            if (menuFlag2[0] && MENUWIDTH > 12)
                                menuHomeUser2.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                            if (menuFlag3[0] && MENUWIDTH > 12)
                                menuHomeUser3.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 300, menuIcon.getIconHeight());
                            if (menuFlag4[0] && MENUWIDTH > 12)
                                menuHomeUser4.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                            if (menuFlag5[0] && MENUWIDTH > 12)
                                menuHomeUser5.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                            if (menuFlag6[0] && MENUWIDTH > 12)
                                menuHomeUser6.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());

                            WIDTH -= 5;
                            if (menuFlag[0] || menuFlag1[0] || menuFlag2[0] || menuFlag3[0] || menuFlag4[0] || menuFlag5[0] || menuFlag6[0])
                                MENUWIDTH -= 5;

                            if (keepFlag[0]) {
                                WIDTHNOW[0] = WIDTH;
                                break label2;
                            }
                        }
                        Menu.isShrink = false;  //Contraction completion
                        menuBack.setBounds(110, 20, 0, 0);
                        Menu.isOpen[0] = false;
                        WIDTHNOW[0] = -(newMenuIcon.getIconWidth() - menuIcon.getIconWidth());  //clear
                    }
                }
            }.start();
        }
    }



    class ChooseBackButton extends RadioJLabel implements MouseMotionListener, MouseListener {
        ImageIcon icon;  //Displayed picture
        ImageIcon iconCache; //Picture cache
        ImageIcon putIcon;  //Displayed picture
        ImageIcon backIcon;  //Displayed picture
        String resource;  //Picture name
        String put;  //Place picture
        String backResource;  //Place picture
        public static boolean click = false;  //No click by default
        RadioJLabel back;  //Background JLabel
        DynamicJLabel text; //The text near the button

        /**
         * Create an image radio button, pass in the name of the image, place the name of the image, the name of the background image, as needed under resources
         * Set the default location to the top left corner and the default size to the image size
         * You need to add a background label, JLabel, which you can create without any action, and then add this to the container in the container, otherwise the background will not display properly
         *
         * @param resource
         */
        @SneakyThrows
        public ChooseBackButton(String resource, String put, String back, RadioJLabel init) {
            super();

            try (InputStream in = Resources.getResourceAsStream(resource)) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                icon = new ImageIcon(bytes);
                iconCache = new ImageIcon(bytes);  //Make a copy of the image

            } //Get pictures
            try (InputStream in = Resources.getResourceAsStream(put)) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                putIcon = new ImageIcon(bytes);
            } //Get pictures
            try (InputStream in = Resources.getResourceAsStream(back)) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                backIcon = new ImageIcon(bytes);
            } //Get pictures
            this.resource = resource;  //Set name
            this.put = put;  //Set name
            this.backResource = back;
            this.setIcon(icon);  //Set picture
            this.setBounds(1, 1, icon.getIconWidth(), icon.getIconHeight()); //Set default location
            this.addMouseListener(this);  //Join a listener
            this.addMouseMotionListener(this);   //Join a listener
            this.back = init;  //Initialization background
        }




        /**
         * Create an image radio button, pass in the name of the image, place the name of the image, the name of the background image, as needed under resources
         * Set the default location to the top left corner and the default size to the image size
         * You need to add a background label, JLabel, which you can create without any action, and then add this to the container in the container, otherwise the background will not display properly
         *
         * @param resource
         */
        @SneakyThrows
        public ChooseBackButton(String resource, String put, String back, RadioJLabel init, DynamicJLabel text, Location location) {
            this(resource, put, back, init);
            this.text = text; //Initialization text
            if (location == Location.EAST) {  //Position of text
                text.setBoundsDynamic(this.getX() + 30, this.getY() - 3);  //Position of text
            } else {
                text.setBoundsDynamic(this.getX() - 30 - text.getWidth(), this.getY() - 3);  //Position of text
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            click = !click;  //Click and add background
            if (click) {
                icon = backIcon;  //The default is now backIcon
                back.repaint();
            } else {
                icon = iconCache;   //Restore default icon
                back.repaint();

            }

        }

        /**
         * Set the orientation, provided that the constructor passed DynamicJLabel is called. Note that the font for the dynamic text is created ahead of time
         */
        public void setLocation(Location location) {
            if (location == Location.EAST) {  //If I add on the right
                text.setBoundsDynamic(this.getX() + 30, this.getY() - 3);  //Position of text
            } else {
                text.setBoundsDynamic(this.getX() - 30 - text.getWidth() + 20, this.getY() - 3);  //Position of text
            }
        }

        /**
         * The functional method, which stores whether the button is in the click state, passes in the external consumer, gives the consumer the current click state, and the consumer processes the event through the click state
         *
         * @param comsumer Consumers to deal with listening events
         */
        public void isClick(Consumer<Boolean> comsumer) {
            comsumer.accept(click); //消费者接收click
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {
            this.setIcon(icon); //Restore picture
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @SneakyThrows
        @Override
        public void mouseMoved(MouseEvent e) {
            this.setIcon(putIcon);  //Set the place image
        }

        /**
         * Inner enumeration class
         */
        public enum Location {
            EAST,   //The text is on the right
            WEST    //Text on the left
        }
    }

    /***
     - Music player class
     */
    class PlaySound{
        static javazoom.jl.player.Player player;

        //Play method
        public static void play(String filePath) throws FileNotFoundException, JavaLayerException {

            FileInputStream buffer = new FileInputStream("src/main/resources/"+filePath);
            player = new javazoom.jl.player.Player(buffer);
            player.play();
        }

    }



    public class Menu {

        public static ImageIcon newMenuIcon;
        public static int homeColor1 = 57;
        public static int homeColor2 = 72;
        public static int homeColor3 = 103;
        public static int homeColorBack1 = 33;
        public static int homeColorBack2 = 42;
        public static int homeColorBack3 = 62;
        public static int menuHomeColor1 = 33;
        public static int menuHomeColor2 = 42;
        public static int menuHomeColor3 = 62;
        static Color LeftColor = getColorFromHex("#B8FFF9");
        private static boolean[] canDo;
        private static boolean[] isIn;
        public static boolean[] isClick1_1;
        private static boolean[] canDo2;
        private static boolean[] isIn2;
        private static boolean[] isClick2_1;
        private static boolean[] canDo3;
        private static boolean[] isIn3;
        private static boolean[] isClick3_1;
        private static boolean[] canDo4;
        private static boolean[] isIn4;
        private static boolean[] isClick4_1;
        private static boolean[] canDo5;
        private static boolean[] isIn5;
        private static boolean[] isClick5_1;
        private static boolean[] canDo6;
        private static boolean[] isIn6;
        private static boolean[] isClick6_1;
        private static boolean isBacking;
        private static RadioJLabel addFriendBackLabel;
        private static RadioJLabel addFriendBackRightLabel;
        private static RadioJLabel chatFriendBackLabel;
        private static RadioJLabel chatFriendBackRightLabel;
        private static RadioJLabel colorBackLabel;
        private static RadioJLabel colorBackRightLabel;
        private static RadioJLabel officialBackLabel;
        private static RadioJLabel officialBackRightLabel;
        private static RadioJLabel marketBackLabel;
        private static RadioJLabel backBackLabel;
        private static RadioJLabel marketBackRightLabel;
        private static RadioJLabel backBackRightLabel;
        private static RadioJLabel settingBackLabel;
        private static RadioJLabel settingBackRightLabel;
        private static RadioJLabel addFriendBackLeftLabel;
        private static RadioJLabel chatFriendBackLeftLabel;
        private static RadioJLabel colorBackLeftLabel;
        private static RadioJLabel officialBackLeftLabel;
        private static RadioJLabel marketBackLeftLabel;
        private static RadioJLabel backBackLeftLabel;
        private static RadioJLabel settingBackLeftLabel;
        private static ImageIcon addFriendIcon;
        static final boolean[] isInLeft1 = {false};
        static final boolean[] isInLeft2 = {false};
        static final boolean[] isInLeft3 = {false};
        static final boolean[] isInLeft4 = {false};
        static final boolean[] isInLeft5 = {false};
        static final boolean[] isInLeft6 = {false};
        static final boolean[] leftIsFinish = {true};
        private static ImageIcon addFriendIconOn;
        private static ImageIcon chatIconOn;
        private static ImageIcon colorIconOn;
        private static ImageIcon officialIconOn;
        private static ImageIcon marketIconOn;
        private static ImageIcon backIconOn;
        private static ImageIcon settingIconOn;
        private static ImageIcon chatIcon;
        private static ImageIcon colorIcon;
        private static ImageIcon officialIcon;
        private static ImageIcon marketIcon;
        private static ImageIcon backIcon;
        private static ImageIcon settingIcon;
        private static RadioJLabel addFriendLabel;
        private static RadioJLabel backLabel;
        private static RadioJLabel chatLabel;
        private static RadioJLabel colorLabel;
        private static RadioJLabel officialLabel;
        private static RadioJLabel marketLabel;
        private static RadioJLabel settingLabel;
        public static boolean[] isInAll;
        public static int[] WIDTHNOW;
        public static boolean[] isOpen;
        public  static boolean[] keepFlag;
        private static RadioJLabel menuHomeUser3Back;
        private static int menuHomeUserBackIndex=0;
        private static int menuHomeUser1BackIndex=0;
        private static int menuHomeUser2BackIndex=0;
        private static int menuHomeUser3BackIndex=0;
        private static int menuHomeUser4BackIndex=0;
        private static int menuHomeUser5BackIndex=0;
        private static int menuHomeUser6BackIndex=0;
        public static boolean isOut = false;
        public static boolean isShrink = false;
        public static Object board;
        public static PieceState COLOR_NOW ;
        public static String promptNow = "";
        public static boolean gameOn = false;
        public static boolean gamePause = false;
        public static boolean gameIsEnd = false;
        public static boolean isAddingBlocks = false;
        MusicService musicService = new MusicServiceImpl();
        public static PieceState[][] myBoard = new PieceState[7][7];



        @SneakyThrows
        public static void init() {

            newMenuIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/menubar3.png")));
            keepFlag = new boolean[]{true};
            isOpen = new boolean[]{false};
            WIDTHNOW = new int[]{0};
            isInAll = new boolean[]{false};

            MouseAdapter mouseAd = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    isInAll[0] = true;
                    if (!isMenuChild&&!isOut&&!isShrink) {
                        isMenuChild = false;
                        //
                        menuHomeBack.setBackground(new Color(0, 0, 0, 0));
                        //
                        newMenuIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/menubar3.png")));
                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int WIDTH;
                                int MENUWIDTH = 180 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                                if (WIDTHNOW[0] == 0) {
                                    WIDTH = -5 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                                } else {
                                    WIDTH = WIDTHNOW[0];
                                }
                                keepFlag[0] = true;
                                menuTop.setIcon(newMenuIcon);
                                menuBack.setBounds(110, 20, newMenuIcon.getIconWidth(), newMenuIcon.getIconHeight());
                                label:
                                {
                                    isOut = true;
                                    while (WIDTH < -40) {
                                        Thread.sleep(1);
                                        menuTop.setBounds(WIDTH, 0, newMenuIcon.getIconWidth(), menuIcon.getIconHeight());
                                        WIDTH += 3;
//                                    if (!keepFlag[0]) {
//                                        WIDTHNOW[0] = WIDTH;
//                                        keepFlag[0] = false;
//                                        break label;
//                                    }
                                    }
                                    isOut = false;
                                    isOpen[0] = true;
                                    WIDTHNOW[0] = 0;
                                }
//
                            }
                        }.start();
                    }
                }


            };
            menu.addMouseListener(mouseAd);




            menuBack.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    isInAll[0] = false;
                    isMenuChild = false;
                    if (!isOut&&!isShrink) {
                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int WIDTH = WIDTHNOW[0];
                                int MENUWIDTH = 140;
                                keepFlag[0] = false;
                                menuBack.setBounds(110, 20, newMenuIcon.getIconWidth(), newMenuIcon.getIconHeight());

                                label2:
                                {
                                    isShrink = true;
                                    while (WIDTH > -20 - 180 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth())) {
                                        Thread.sleep(1);
                                        menuTop.setBounds(WIDTH, 0, newMenuIcon.getIconWidth(), menuIcon.getIconHeight());
                                        if (menuFlag[0] && MENUWIDTH > 12)
                                            menuHomeUser.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                                        if (menuFlag1[0] && MENUWIDTH > 12)
                                            menuHomeUser1.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                                        if (menuFlag2[0] && MENUWIDTH > 12)
                                            menuHomeUser2.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                                        if (menuFlag3[0] && MENUWIDTH > 12)
                                            menuHomeUser3.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 300, menuIcon.getIconHeight());
                                        if (menuFlag4[0] && MENUWIDTH > 12)
                                            menuHomeUser4.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                                        if (menuFlag5[0] && MENUWIDTH > 12)
                                            menuHomeUser5.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());
                                        if (menuFlag6[0] && MENUWIDTH > 12)
                                            menuHomeUser6.setBounds(MENUWIDTH, 10, menuIcon.getIconWidth() + 350, menuIcon.getIconHeight());

                                        WIDTH -= 7;
                                        if (menuFlag[0] || menuFlag1[0] || menuFlag2[0] || menuFlag3[0] || menuFlag4[0] || menuFlag5[0] || menuFlag6[0])
                                            MENUWIDTH -= 7;

                                        if (keepFlag[0]) {
                                            WIDTHNOW[0] = WIDTH;
                                            break label2;
                                        }
                                    }
                                    isShrink = false;
                                    menuBack.setBounds(110, 20, 0, 0);
                                    isOpen[0] = false;
                                    WIDTHNOW[0] = -(newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                                }
                            }
                        }.start();
                    }
                }
            });

            addFriendIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/back.png")));
            addFriendIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/addFriendOn.png")));
            addFriendLabel = new RadioJLabel(addFriendIcon);
            addFriendLabel.setColor(new Color(0, 0, 0, 0));
            menu.add(addFriendLabel);
            addFriendLabel.setBounds(11, 160, addFriendIcon.getIconWidth(), addFriendIcon.getIconHeight());

            addFriendBackLabel = new RadioJLabel("");
            addFriendBackLabel.setColor(new Color(45, 101, 154, 0));
            addFriendBackLabel.setArc(0, 0);
            menu.add(addFriendBackLabel);  //选中背景
            addFriendBackLabel.setBounds(0, 150, addFriendIcon.getIconWidth() + 32, addFriendIcon.getIconHeight() + 20);

            addFriendBackLeftLabel = new RadioJLabel("");
            addFriendBackLeftLabel.setColor(LeftColor);  //把颜色改为rgb值
            addFriendBackLabel.add(addFriendBackLeftLabel);  //选中背景


            addFriendBackRightLabel = new RadioJLabel("");
            addFriendBackRightLabel.setColor(new Color(45, 101, 154, 0));
            menuTop.add(addFriendBackRightLabel);  //选中背景
            addFriendBackRightLabel.setBounds(0, 150, addFriendIcon.getIconWidth() + 140, addFriendIcon.getIconHeight() + 20);
            wrongMessage = new DynamicJLabel("Print Score", new Font("Serif", Font.BOLD, 18), 173);
            wrongMessage.setForeground(new Color(255, 255, 255, 231));
            wrongMessage.setCenter(160);
            menuBack.add(wrongMessage);

            canDo = new boolean[]{true};
            isIn = new boolean[]{true};
            isClick1_1 = new boolean[]{false};
            menuTop.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isMenuChild = true;
                }
            });
            addFriendBackRightLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterBackAction(isInLeft1, isIn, canDo, isClick1_1, addFriendBackLabel, addFriendBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitBackAction(isIn, canDo, isClick1_1, addFriendBackLabel, addFriendBackRightLabel);

                }
            });
            addFriendLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterAction(menuHomeUser1, isOpen, WIDTHNOW, keepFlag, isIn, canDo, isClick1_1, addFriendBackLabel, addFriendBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitAction(isIn, canDo, isClick1_1, addFriendBackLabel, addFriendBackRightLabel);
                }
            });

            final boolean[] canDo1_1 = {true, true};
            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            PlaySound.play("sound/error.mp3");
                        }
                    }.start();
                    back.dispose();
                    isBacking = true;
                    LoginHome.background.setVisible(true);
                }

            };
            addFriendLabel.addMouseListener(menuOpen);
            addFriendBackRightLabel.addMouseListener(menuOpen);

            chatIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/peoples.png")));
            chatIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/peoplesOn.png")));
            chatLabel = new RadioJLabel(chatIcon);
            chatLabel.setColor(new Color(0, 0, 0, 0));
            menu.add(chatLabel);
            int oriY = addFriendLabel.getY() + addFriendIcon.getIconHeight() + 20;
            chatLabel.setBounds(11, oriY, chatIcon.getIconWidth(), chatIcon.getIconHeight());

            chatFriendBackLabel = new RadioJLabel("");
            chatFriendBackLabel.setColor(new Color(45, 101, 154, 0));
            menu.add(chatFriendBackLabel);  //选中背景
            chatFriendBackLabel.setBounds(0, oriY - 10, addFriendIcon.getIconWidth() + 32, addFriendIcon.getIconHeight() + 20);

            chatFriendBackLeftLabel = new RadioJLabel("");
            chatFriendBackLeftLabel.setColor(LeftColor);  //把颜色改为rgb值
            chatFriendBackLabel.add(chatFriendBackLeftLabel);  //选中背景
//        chatFriendBackLeftLabel.setBounds(0, 0, 5, addFriendIcon.getIconHeight()+20);


            chatFriendBackRightLabel = new RadioJLabel("");
            chatFriendBackRightLabel.setColor(new Color(45, 101, 154, 0));
            menuTop.add(chatFriendBackRightLabel);  //选中背景
            chatFriendBackRightLabel.setBounds(0, oriY - 10, addFriendIcon.getIconWidth() + 140, addFriendIcon.getIconHeight() + 20);
            wrongMessage = new DynamicJLabel("Reset The Match", new Font("Serif", Font.BOLD, 15), 246);
            wrongMessage.setForeground(new Color(255, 255, 255, 231));
            wrongMessage.setCenter(160);
            menuBack.add(wrongMessage);


            canDo2 = new boolean[]{true};
            isIn2 = new boolean[]{true};
            isClick2_1 = new boolean[]{false};
            chatFriendBackRightLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterBackAction(isInLeft2, isIn2, canDo2, isClick2_1, chatFriendBackLabel, chatFriendBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitBackAction(isIn2, canDo2, isClick2_1, chatFriendBackLabel, chatFriendBackRightLabel);

                }
            });
            chatFriendBackLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterAction(menuHomeUser2, isOpen, WIDTHNOW, keepFlag, isIn2, canDo2, isClick2_1, chatFriendBackLabel, chatFriendBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitAction(isIn2, canDo2, isClick2_1, chatFriendBackLabel, chatFriendBackRightLabel);
                }
            });
            final boolean[] canDo2_1 = {true, true};

            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            PlaySound.play("sound/error.mp3");
                        }
                    }.start();

                    if (gameIsEnd) {
                        tip2.setFont(new Font("Serif", Font.BOLD, 12));
                        tip2.setForeground(new Color(245, 87, 87));
                        tip2.setCenter(230);
//                        back.dispose();
                        GUI.board.clear();

                        gameIsEnd = false;

                        return;
                    }
                    Menu.promptNow = "new";
                    tip1.setTextDynamic("Red Round");
                    tip1.setForeground(new Color(224, 78, 78));
                    tip2.setTextDynamic("Reset The Match");
                    tip2.setFont(new Font("Serif", Font.BOLD, 15));
                    tip2.setForeground(new Color(245, 87, 87));
                    tip2.setCenter(200);
                    GUI.blocks = new int[7][7];
                    gameIsEnd = false;
                    gameOn = false;


                }

            };
            chatLabel.addMouseListener(menuOpen);
            chatFriendBackRightLabel.addMouseListener(menuOpen);

            colorIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/platte.png")));
            colorIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/platteOn.png")));
            colorLabel = new RadioJLabel(colorIcon);
            colorLabel.setColor(new Color(0, 0, 0, 0));
            menu.add(colorLabel);
            int oriY2 = oriY + addFriendIcon.getIconHeight() + 20;
            colorLabel.setBounds(11, oriY2, chatIcon.getIconWidth(), chatIcon.getIconHeight());

            colorBackLabel = new RadioJLabel("");
            colorBackLabel.setColor(new Color(45, 101, 154, 0));
            menu.add(colorBackLabel);  //选中背景
            colorBackLabel.setBounds(0, oriY2 - 10, addFriendIcon.getIconWidth() + 32, addFriendIcon.getIconHeight() + 20);

            colorBackLeftLabel = new RadioJLabel("");
            colorBackLeftLabel.setColor(LeftColor);
            colorBackLabel.add(colorBackLeftLabel);


            colorBackRightLabel = new RadioJLabel("");
            colorBackRightLabel.setColor(new Color(45, 101, 154, 0));
            menuTop.add(colorBackRightLabel);
            colorBackRightLabel.setBounds(0, oriY2 - 10, addFriendIcon.getIconWidth() + 140, addFriendIcon.getIconHeight() + 20);
            wrongMessage = new DynamicJLabel("Set Blocks", new Font("Serif", Font.BOLD, 18), 320);
            wrongMessage.setForeground(new Color(255, 255, 255, 231));
            wrongMessage.setCenter(160);
            menuBack.add(wrongMessage);

            canDo3 = new boolean[]{true};
            isIn3 = new boolean[]{true};
            isClick3_1 = new boolean[]{false};
            colorBackRightLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterBackAction(isInLeft3, isIn3, canDo3, isClick3_1, colorBackLabel, colorBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitBackAction(isIn3, canDo3, isClick3_1, colorBackLabel, colorBackRightLabel);
                }
            });
            colorBackLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterAction(menuHomeUser3, isOpen, WIDTHNOW, keepFlag, isIn3, canDo3, isClick3_1, colorBackLabel, colorBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitAction(isIn3, canDo3, isClick3_1, colorBackLabel, colorBackRightLabel);
                }
            });
            final boolean[] canDo3_1 = {true, true};

            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            PlaySound.play("sound/error.mp3");
                        }
                    }.start();

                    //增加方块
                    if (gameOn) {
                        tip2.setTextDynamic("Failed to add block!");
                        tip2.setFont(new Font("Serif", Font.BOLD, 14));
                        tip2.setForeground(new Color(245, 87, 87));
                        tip2.setCenter(230);

                    } else {
                        isAddingBlocks = !isAddingBlocks;
                        if (isAddingBlocks) {
                            tip2.setTextDynamic("Start to add block!");
                            tip2.setFont(new Font("Serif", Font.BOLD, 14));
                            tip2.setForeground(new Color(245, 87, 87));
                            showMargin(colorBackLeftLabel, 3);
                            tip2.setCenter(230);

                        } else {
                            tip2.setTextDynamic("Stop to add block!");
                            tip2.setFont(new Font("Serif", Font.BOLD, 14));
                            tip2.setForeground(new Color(245, 87, 87));
                            backMargin(colorBackLeftLabel, 3);
                            tip2.setCenter(230);


                        }

                    }
                }

            };
            colorLabel.addMouseListener(menuOpen);
            colorBackRightLabel.addMouseListener(menuOpen);


            officialIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/browser.png")));
            officialIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/browserOn.png")));
            officialLabel = new RadioJLabel(officialIcon);
            officialLabel.setColor(new Color(0, 0, 0, 0));
            menu.add(officialLabel);
            int oriY3 = oriY2 + addFriendIcon.getIconHeight() + 20;
            officialLabel.setBounds(11, oriY3, chatIcon.getIconWidth(), chatIcon.getIconHeight());

            officialBackLabel = new RadioJLabel("");
            officialBackLabel.setColor(new Color(45, 101, 154, 0));
            menu.add(officialBackLabel);  //选中背景
            officialBackLabel.setBounds(0, oriY3 - 10, addFriendIcon.getIconWidth() + 32, addFriendIcon.getIconHeight() + 20);

            officialBackLeftLabel = new RadioJLabel("");
            officialBackLeftLabel.setColor(LeftColor);  //把颜色改为rgb值
            officialBackLabel.add(officialBackLeftLabel);  //选中背景


            officialBackRightLabel = new RadioJLabel("");
            officialBackRightLabel.setColor(new Color(45, 101, 154, 0));
            menuTop.add(officialBackRightLabel);  //选中背景
            officialBackRightLabel.setBounds(0, oriY3 - 10, addFriendIcon.getIconWidth() + 140, addFriendIcon.getIconHeight() + 20);
            wrongMessage = new DynamicJLabel("Start The Game", new Font("Serif", Font.BOLD, 15), 395);
            wrongMessage.setForeground(new Color(255, 255, 255, 231));
            wrongMessage.setCenter(160);
            menuBack.add(wrongMessage);

            canDo4 = new boolean[]{true};
            isIn4 = new boolean[]{true};
            isClick4_1 = new boolean[]{false};

            officialBackRightLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterBackAction(isInLeft4, isIn4, canDo4, isClick4_1, officialBackLabel, officialBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitBackAction(isIn4, canDo4, isClick4_1, officialBackLabel, officialBackRightLabel);
                }
            });
            officialBackLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterAction(menuHomeUser4, isOpen, WIDTHNOW, keepFlag, isIn4, canDo4, isClick4_1, officialBackLabel, officialBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitAction(isIn4, canDo4, isClick4_1, officialBackLabel, officialBackRightLabel);
                }
            });

            final boolean[] canDo4_1 = {true, true};

            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            PlaySound.play("sound/error.mp3");
                        }
                    }.start();

                    gameOn = true;
                    gamePause = false;
                    tip2.setTextDynamic("");
                    tip2.setFont(new Font("Serif", Font.BOLD, 21));
                    tip2.setCenter(200);
                }

            };
            officialLabel.addMouseListener(menuOpen);
            officialBackRightLabel.addMouseListener(menuOpen);


            marketIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/market.png")));
            marketIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/marketOn.png")));
            marketLabel = new RadioJLabel(marketIcon);
            marketLabel.setColor(new Color(0, 0, 0, 0));
            menu.add(marketLabel);
            int oriY4 = oriY3 + addFriendIcon.getIconHeight() + 20;
            marketLabel.setBounds(11, oriY4, chatIcon.getIconWidth(), chatIcon.getIconHeight());

            marketBackLabel = new RadioJLabel("");
            marketBackLabel.setColor(new Color(45, 101, 154, 0));
            menu.add(marketBackLabel);  //选中背景
            marketBackLabel.setBounds(0, oriY4 - 10, addFriendIcon.getIconWidth() + 32, addFriendIcon.getIconHeight() + 20);

            marketBackLeftLabel = new RadioJLabel("");
            marketBackLeftLabel.setColor(LeftColor);  //把颜色改为rgb值
            marketBackLabel.add(marketBackLeftLabel);  //选中背景
//        marketBackLeftLabel.setBounds(0, 0, 5, addFriendIcon.getIconHeight()+20);


            marketBackRightLabel = new RadioJLabel("");
            marketBackRightLabel.setColor(new Color(45, 101, 154, 0));
            menuTop.add(marketBackRightLabel);  //选中背景
            marketBackRightLabel.setBounds(0, oriY4 - 10, addFriendIcon.getIconWidth() + 140, addFriendIcon.getIconHeight() + 20);
            wrongMessage = new DynamicJLabel("Stop The Game", new Font("Serif", Font.BOLD, 15), 469);
            wrongMessage.setForeground(new Color(255, 255, 255, 231));
            wrongMessage.setCenter(160);
            menuBack.add(wrongMessage);

            canDo5 = new boolean[]{true};
            isIn5 = new boolean[]{true};
            isClick5_1 = new boolean[]{false};

            marketBackRightLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterBackAction(isInLeft5, isIn5, canDo5, isClick5_1, marketBackLabel, marketBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitBackAction(isIn5, canDo5, isClick5_1, marketBackLabel, marketBackRightLabel);
                }
            });
            marketBackLabel.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseEntered(MouseEvent e) {
                    enterAction(menuHomeUser5, isOpen, WIDTHNOW, keepFlag, isIn5, canDo5, isClick5_1, marketBackLabel, marketBackRightLabel);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitAction(isIn5, canDo5, isClick5_1, marketBackLabel, marketBackRightLabel);

                }
            });

            final boolean[] canDo5_1 = {true, true};

            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            PlaySound.play("sound/error.mp3");
                        }
                    }.start();

                    gamePause = true;
                    tip2.setTextDynamic("Game pause");
                    tip2.setFont(new Font("Serif", Font.BOLD, 21));
                    tip2.setCenter(200);
                }

            };
            marketLabel.addMouseListener(menuOpen);
            marketBackRightLabel.addMouseListener(menuOpen);






        }


        @SneakyThrows
        private static void enterAction(RadioJLabelNew menuHomeUser, boolean[] isOpen, int[] WIDTHNOW, boolean[] keepFlag, boolean[] isIn, boolean[] canDo, boolean[] isClick, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isIn[0] = true;
            if (isMenuChild && !isOpen[0]) {
                newMenuIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/menubar3.png")));
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int WIDTH;
                        int MENUWIDTH = 180 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                        if (WIDTHNOW[0] == 0) {
                            WIDTH = -5 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                        } else {
                            WIDTH = WIDTHNOW[0];
                        }
                        keepFlag[0] = true;
                        menuTop.setIcon(newMenuIcon);
                        menuBack.setBounds(110, 20, newMenuIcon.getIconWidth(), newMenuIcon.getIconHeight());
                        label:
                        {
                            int demo = 54;
                            isOpen[0] = true;
                            while (WIDTH < -40) {
                                Thread.sleep(1);
                                menuTop.setBounds(WIDTH, 0, newMenuIcon.getIconWidth(), menuIcon.getIconHeight());
                                WIDTH += 3;
                                demo += 3;
                            }
                            WIDTHNOW[0] = 0;
                        }
//
                    }
                }.start();
            }

            isMenuChild = true;
            isIn[0] = true;




        }


        private static void exitAction(boolean[] isIn, boolean[] canDo, boolean[] isClick, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isIn[0] = false;

        }



        private static void enterBackAction(boolean[] isInLeft, boolean[] isIn, boolean[] canDo, boolean[] isClick, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isMenuChild = true;
            isIn[0] = true;
            if (canDo[0] && (!isClick[0])) {
                canDo[0] = false;
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int MAXTRANS = 1;
                        while (MAXTRANS <= 255) {
                            Thread.sleep(6);
                            BackLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackRightLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackLabel.repaint();
                            BackRightLabel.repaint();
                            MAXTRANS += 4;
                        }
                        canDo[0] = true;
                        Thread.sleep(200);
                        if (!isIn[0] && !(isInLeft[0]))
                            otherInCheck(isIn, canDo, BackLabel, BackRightLabel);
                    }
                }.start();
            }
        }


        private static void exitBackAction(boolean[] isIn, boolean[] canDo, boolean[] isClick, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isIn[0] = false;
            if (canDo[0] && !isClick[0]) {
                canDo[0] = false;
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int MAXTRANS = 255;
                        while (MAXTRANS >= 0) {
                            Thread.sleep(6);
                            BackLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackRightLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackLabel.repaint();
                            BackRightLabel.repaint();
                            MAXTRANS -= 4;
                        }
                        canDo[0] = true;
                        Thread.sleep(200);
                        if (isIn[0])
                            otherOutCheck(isIn, canDo, BackLabel, BackRightLabel);
                    }
                }.start();
            }
        }


        private static void otherOutCheck(boolean[] isIn, boolean[] canDo, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isMenuChild = true;
            isIn[0] = true;
            if (canDo[0]) {
                canDo[0] = false;
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int MAXTRANS = 1;
                        while (MAXTRANS <= 255) {
                            Thread.sleep(6);
                            BackLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackRightLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackLabel.repaint();
                            BackRightLabel.repaint();
                            MAXTRANS += 4;
                        }
                        canDo[0] = true;
                        Thread.sleep(200);
                    }
                }.start();
            }
        }


        private static void otherInCheck(boolean[] isIn, boolean[] canDo, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            isIn[0] = false;
            if (canDo[0]) {
                canDo[0] = false;
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int MAXTRANS = 255;
                        while (MAXTRANS >= 0) {
                            Thread.sleep(6);
                            BackLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackRightLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                            BackLabel.repaint();
                            BackRightLabel.repaint();
                            MAXTRANS -= 4;
                        }
                        canDo[0] = true;
                        Thread.sleep(200);
                    }
                }.start();
            }
        }


        private static void openContent(boolean[] canDo, int index) {

            switch (index) {
                case 6:
                    if (!menuFlag6[0] && canDo[0] && canDo[1] && leftIsFinish[0]) {
                        isClick6_1[0] = !isClick6_1[0];
                        menuHomeUser6BackIndex++;


                        canDo[0] = false;
                        canDo[1] = false;
                        menuFlag6[0] = true;
                        menuHomeBack.setBounds(115, 10, menuIcon.getIconWidth() + 600, menuIcon.getIconHeight() + 20);
                        menuHomeBack.setBackground(new Color(0, 0, 0, 0));
                        menuHomeUser6.setBounds(124, 8, menuIcon.getIconWidth() + 200, menuIcon.getIconHeight());


                        if (openMenuIndex != -1) {
                            Menu.DealWithOldMenuCont(openMenuIndex, canDo);
                        }

                        showMargin(settingBackLeftLabel, 6);


                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int MAXTRANS = 1;
                                if (menuHomeUser6BackIndex>1)
                                    while (MAXTRANS <= 255) {
                                        Thread.sleep(6);
                                        menuHomeUser6.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));

                                        menuHomeUser6.repaint();
                                        MAXTRANS += 5;
                                    }
                                canDo[0] = true;
                            }
                        }.start();
                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int MAXTRANS = 255;
                                while (MAXTRANS >= 100) {
                                    Thread.sleep(6);
                                    Home.home.setColor(new Color(homeColor1, homeColor2, homeColor3, MAXTRANS));
                                    homeBack.setColor(new Color(homeColorBack1, homeColorBack2, homeColorBack3, MAXTRANS));
                                    Home.home.repaint();
                                    MAXTRANS -= 7;
                                }
                                canDo[1] = true;
                            }
                        }.start();
                        openMenuIndex = 6;


                    } else if (menuFlag6[0] && canDo[0] && canDo[1] && leftIsFinish[0]) {
                        isClick6_1[0] = !isClick6_1[0];

                        canDo[0] = false;
                        canDo[1] = false;
                        menuFlag6[0] = false;
                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int MAXTRANS = 255;

                                while (MAXTRANS >= 0) {
                                    Thread.sleep(6);
                                    menuHomeUser6.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                    menuHomeUser6.repaint();
                                    MAXTRANS -= 5;
                                }
                                canDo[0] = true;
                            }
                        }.start();
                        openMenuIndex = -1;

                        backMargin(settingBackLeftLabel, 6);


                        new Thread() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                int MAXTRANS = 1;
                                while (MAXTRANS <= 255) {
                                    Thread.sleep(6);
                                    Home.home.setColor(new Color(homeColor1, homeColor2, homeColor3, MAXTRANS));
                                    homeBack.setColor(new Color(homeColorBack1, homeColorBack2, homeColorBack3, MAXTRANS));
                                    Home.home.repaint();
                                    MAXTRANS += 12;
                                }
                                canDo[1] = true;
                            }
                        }.start();
                    }
                    break;
            }

        }


        public static void DealWithOldMenuCont(int index, boolean[] canDo) {
            switch (index) {
                case 0:
                    canDo[0] = false;
                    canDo[1] = false;


                    menuFlag[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;
                case 1:
                    deleteBack(isClick1_1, addFriendBackLabel, addFriendBackRightLabel);
                    backMargin(addFriendBackLeftLabel, 1);


                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag1[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser1.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser1.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;

                case 2:
                    deleteBack(isClick2_1, chatFriendBackLabel, chatFriendBackRightLabel);
                    backMargin(chatFriendBackLeftLabel, 2);


                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag2[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser2.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser2.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;
                case 3:
                    deleteBack(isClick3_1, colorBackLabel, colorBackRightLabel);
                    backMargin(colorBackLeftLabel, 3);


                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag3[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser3.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser3.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;
                case 4:
                    deleteBack(isClick4_1, officialBackLabel, officialBackRightLabel);
                    backMargin(officialBackLeftLabel, 4);


                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag4[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser4.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser4.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;
                case 5:
                    deleteBack(isClick5_1, marketBackLabel, marketBackRightLabel);
                    backMargin(marketBackLeftLabel, 5);



                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag5[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser5.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser5.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;
                case 6:
                    deleteBack(isClick6_1, settingBackLabel, settingBackRightLabel);
                    backMargin(settingBackLeftLabel, 6);



                    canDo[0] = false;
                    canDo[1] = false;
                    menuFlag6[0] = false;
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            int MAXTRANS = 255;
                            while (MAXTRANS >= 0) {
                                Thread.sleep(6);
                                menuHomeUser6.setColor(new Color(menuHomeColor1, menuHomeColor2, menuHomeColor3, MAXTRANS));
                                menuHomeUser6.repaint();
                                MAXTRANS -= 5;
                            }
                            canDo[0] = true;
                        }
                    }.start();
                    canDo[1] = true;
                    break;

            }
        }


        private static void deleteBack(boolean[] isClick1_1, RadioJLabel BackLabel, RadioJLabel BackRightLabel) {
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    isClick1_1[0] = !isClick1_1[0];
                    int MAXTRANS = 255;
                    while (MAXTRANS >= 0) {
                        Thread.sleep(6);
                        BackLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                        BackRightLabel.setColor(new Color(45, 101, 154, MAXTRANS));
                        BackLabel.repaint();
                        BackRightLabel.repaint();
                        MAXTRANS -= 4;
                    }
                    Thread.sleep(200);
                }
            }.start();
        }


        @SneakyThrows
        private static void showMargin(RadioJLabel LeftLabel, int index) {
            leftIsFinish[0] = false;
            final int[] LOCATION = {0};
            final int[] TIME = {6};
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    while (LOCATION[0] <= addFriendIcon.getIconHeight() + 20) {
                        Thread.sleep(TIME[0]);
                        LeftLabel.setBounds(0, 0, 5, LOCATION[0]);
                        LeftLabel.repaint();
                        LOCATION[0] += 3;
                        TIME[0] += 2;
                    }
                    leftIsFinish[0] = true;

                }
            }.start();
            switch (index) {
                case 1:
                    isInLeft1[0] = true;

                    addFriendLabel.setIcon(addFriendIconOn);

                    break;
                case 2:
                    isInLeft2[0] = true;

                    chatLabel.setIcon(chatIconOn);

                    break;
                case 3:
                    isInLeft3[0] = true;

                    colorLabel.setIcon(colorIconOn);

                    break;
                case 4:
                    isInLeft4[0] = true;

                    officialLabel.setIcon(officialIconOn);

                    break;
                case 5:
                    isInLeft5[0] = true;

                    marketLabel.setIcon(marketIconOn);

                    break;
                case 6:
                    isInLeft6[0] = true;

                    settingLabel.setIcon(settingIconOn);
                    break;

            }


        }


        @SneakyThrows
        private static void backMargin(RadioJLabel LeftLabel, int index) {
            leftIsFinish[0] = false;
            final int[] LOCATION = {addFriendIcon.getIconHeight() + 20};
            final int[] TIME = {6};
            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    while (LOCATION[0] >= 0) {
                        Thread.sleep(TIME[0]);
                        LeftLabel.setBounds(0, 0, 5, LOCATION[0]);
                        LeftLabel.repaint();
                        LOCATION[0] -= 3;
                        TIME[0] += 2;

                    }
                    leftIsFinish[0] = true;

                }
            }.start();
            switch (index) {
                case 1:
                    isInLeft1[0] = false;
                    addFriendLabel.setIcon(addFriendIcon);

                    break;
                case 2:
                    isInLeft2[0] = false;
                    chatLabel.setIcon(chatIcon);

                    break;
                case 3:
                    isInLeft3[0] = false;
                    colorLabel.setIcon(colorIcon);

                    break;
                case 4:
                    isInLeft4[0] = false;
                    officialLabel.setIcon(officialIcon);

                    break;
                case 5:
                    isInLeft5[0] = false;
                    marketLabel.setIcon(marketIcon);

                    break;
                case 6:
                    isInLeft6[0] = false;
                    settingLabel.setIcon(settingIcon);

                    break;
            }
        }

        @SneakyThrows
        private static void openMenu() {
            isInAll[0] = true;
            if (!isMenuChild) {
                newMenuIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/menubar3.png")));
                new Thread() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        int WIDTH;
                        int MENUWIDTH = 180 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                        if (WIDTHNOW[0] == 0) {
                            WIDTH = -5 - (newMenuIcon.getIconWidth() - menuIcon.getIconWidth());
                        } else {
                            WIDTH = WIDTHNOW[0];
                        }
                        keepFlag[0] = true;
                        menuTop.setIcon(newMenuIcon);
                        menuBack.setBounds(110, 20, newMenuIcon.getIconWidth(), newMenuIcon.getIconHeight());
                        label:
                        {
                            while (WIDTH < -40) {
                                Thread.sleep(1);
                                menuTop.setBounds(WIDTH, 0, newMenuIcon.getIconWidth(), menuIcon.getIconHeight());
                                WIDTH += 3;
//                                    if (!keepFlag[0]) {
//                                        WIDTHNOW[0] = WIDTH;
//                                        keepFlag[0] = false;
//                                        break label;
//                                    }
                            }
                            isOpen[0] = true;
                            WIDTHNOW[0] = 0;
                        }
//
                    }
                }.start();
            }
        }
    }



    /**
     * BufferedImage images are converted to strings and stored in the database
     */
    class ToPicture {
        // Converts the image to string data based on the image address
        public static String imageToString(BufferedImage image, String format) {
            StringBuffer sb2 = new StringBuffer();
            BufferedImage image1 = image;
            byte[] img = getBytes(image1,format);

            for (int i = 0; i < img.length; i++) {
                if (sb2.length() == 0) {
                    sb2.append(img[i]);
                } else {
                    sb2.append("," + img[i]);
                }
            }
            return sb2.toString();

        }
        // Converts a BufferImage to an array of bytes
        private static byte[] getBytes(BufferedImage image, String format) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                if (format.equalsIgnoreCase("png"))
                    ImageIO.write(image, "PNG", baos);
                if (format.equalsIgnoreCase("jpg"))
                    ImageIO.write(image, "JPG", baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return baos.toByteArray();
        }

    }

    /**
     * The jitter button inherits the jitter action interface
     */
    static class ShakeLabel extends JLabel implements Shakeable {
        public ShakeLabel(ImageIcon icon) {
            super(icon);
        }
        public ShakeLabel(String message) {
            super(message);
        }
        @Override
        public void shake() {  // Start jitter and jitter for 0.3 seconds
            // Set the window status
            Point p = this.getLocation();  //Current coordinate
            new Thread() {
                //Start time
                long begin = System.currentTimeMillis();
                long end = System.currentTimeMillis();

                @SneakyThrows
                @Override
                public void run() {
                    while ((end - begin)  < 400) {  //Shake 0.3 s
                        ShakeLabel.this.setLocation(new Point((int) p.getX() - 5, (int) p.getY() + 5));  //Reset position
                        ShakeLabel.this.setLocation(p);  //Restore position
                        end = System.currentTimeMillis(); //Set the current end time
                    }
                }
            }.start();
        }
    }


    /**
     * A rounded rectangle done by the button calling the printComponent method
     */
    class RadioJLabelNew extends JLabel {
        private Shape shape = null;  //shape..

        private Color put;  //Placement color

        private int arcWidth = 20;  //Horizontal roundness of rounded corners, default 20

        private int arcHeight = 20; //Vertical roundness of rounded corners, default 20

        public RadioJLabelNew(String s) {
            super(s);
        }

        public void setColor(Color p) {
            put = p;
        }

        /**
         * Click the button when called,fillRoundRect is printed graphic size and rounded corners,arcWidth control rounded corners,height and other control width
         *
         * @param g The image to print
         */
        public void paintComponent(Graphics g) {
            g.setColor(put);
            //Fill the rounded rectangle area for other shapes as well
            g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                    arcWidth, arcHeight);
            //It has to be last or it won't work
            super.paintComponent(g);
        }

        /**
         * Draw the rounded button border
         */
        public void paintBorder(Graphics g) {
            //Draw boundary area
            g.setColor(put);
            g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                    arcWidth, arcHeight);
        }

        /**
         * If you change arcw and arch above, change it here
         *
         * @param x Default 0
         * @param y Default 0
         * @return Returns shape to call contains
         */
        public boolean contains(int x, int y) {
            // Check whether point (x,y) is inside the button
            if (shape == null || !(shape.getBounds().equals(getBounds()))) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        arcWidth, arcHeight);
            }
            return shape.contains(x, y);
        }

        /**
         * Set the horizontal and vertical corners of the button
         *
         * @param arcWidth  Horizontal fillet Angle
         * @param arcHeight Vertical fillet Angle
         */
        public void setArc(int arcWidth, int arcHeight) {
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
        }


    }
        /**
     * A rounded rectangle done by the button calling the printComponent method
     */
        static class RadioJLabel extends JLabel implements MouseListener , MouseMotionListener {
        private Shape shape = null;  //shape

        private Color put;  //Placement color

        private int arcWidth = 20;  //Horizontal roundness of rounded corners, default 20

        private int arcHeight = 20; //Vertical roundness of rounded corners, default 20

        public RadioJLabel(String s) {
            super(s);
            addMouseListener(this);
        }
        public RadioJLabel(ImageIcon icon) {
            super(icon);
            addMouseListener(this);
        }

        public RadioJLabel() {

        }

        public void setColor(Color p) {
            put = p;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
        /**
         * Click the button when called,fillRoundRect is printed graphic size and rounded corners,arcWidth control rounded corners,height and other control width
         * @param g The image to print
         */
        public void paintComponent(Graphics g) {
            g.setColor(put);
            //Fill the rounded rectangle area for other shapes as well
            g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                    arcWidth, arcHeight);
            //It has to be last or it won't work
            super.paintComponent(g);
        }

        /**
         * Draw the rounded button border
         */
        public void paintBorder(Graphics g) {
            //Draw boundary area
            g.setColor(put);
            g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                    arcWidth, arcHeight);
        }

        /**
         * If you change the arcw and the arch up here, you change the arch up here
         * @param x Default 0
         * @param y Default 0
         * @return Returns shape to call contains
         */
        public boolean contains(int x, int y) {
            //Determine if the point (x,y) is inside the button
            if (shape == null || !(shape.getBounds().equals(getBounds()))) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        arcWidth, arcHeight);
            }
            return shape.contains(x, y);
        }

        /**
         * Set the horizontal and vertical corners of the button
         * @param arcWidth Horizontal fillet Angle
         * @param arcHeight Vertical fillet Angle
         */
        public void setArc(int arcWidth, int arcHeight) {
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
        }

        /**
         * Get current color
         */
        public Color getColor() {
            return put;
        }

    }


   /**
   * Use a Timer to change the value of variable X and override the paintComponent method to animate the rotation effect
   * Set the Timer delay and define an ActionListener
   * Paint a shape (circle) in the paintComponent method and then change the color of the brush to fill. When filling, set the start position (Angle) according to the variable X.
   * Constantly modifying the value of the variable X and calling the overridden paintComponent method in the ActionListener event
   */
    public class ServerLoading extends JPanel {

        private static final long serialVersionUID = 1551571546L;

        private Color color=Color.RED;
        private Timer timer;
        private int delay;
        private int startAngle;
        private int arcAngle = 0;
        private int orientation;

        public static final int CLOCKWISE = 0;
        public static final int ANTICLOCKWISE = 1;

        public ServerLoading() {
            this.delay = 1;
            this.orientation = CLOCKWISE;
            init();
        }

        public ServerLoading(int delay) {
            this.delay = delay;
            this.orientation = CLOCKWISE;
            init();
        }

        public ServerLoading(int delay, int orientation) {
            this.delay = delay;
            this.orientation = orientation;
            init();
        }

        @Override
        public void show() {
            this.timer.start();
        }

        /**
         * @param orientation	set the direction of rotation
         *
         * @beaninfo
         *        enum: CLOCKWISE LodingPanel.CLOCKWISE
         *        		ANTICLOCKWISE LodingPanel.ANTICLOCKWISE
         */
        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        private void init() {
            this.timer = new Timer(delay, new ReboundListener());
        }

        public void setColor(Color color) {
            this.color = color;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawArc(g);
        }

        private void drawArc(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g2d.setColor(getColorFromHex("#448ACA"));
            g2d.drawArc(width / 2 - 36, height / 2 - 28, 20 + 54, 20 + 54, 0, 360);
            g2d.setColor(color);
            g2d.fillArc(width / 2 - 36, height / 2 - 28, 20 + 54, 20 + 54, startAngle, arcAngle);
            g2d.setColor(getColorFromHex("#448ACA"));
            g2d.fillArc(width / 2 - 31, height / 2 - 23, 20 + 44, 20 + 44, 0, 360);
            g2d.dispose();
        }

        private class ReboundListener implements ActionListener {

            private int o = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startAngle < 360) {
                    switch (orientation) {
                        case CLOCKWISE:
                            startAngle = startAngle + 1;
                            break;
                        case ANTICLOCKWISE:
                            startAngle = startAngle - 1;
                            break;
                        default:
                            startAngle = startAngle + 1;
                            break;
                    }
                } else {
                    startAngle = 0;
                }

                if (o == 0) {
                    if (arcAngle >= 359) {
                        o = 1;
                        orientation = ANTICLOCKWISE;
                    }else {
                        if (orientation == CLOCKWISE) {
                            arcAngle += 1;
                        }
                    }
                }else {
                    if (arcAngle <= 1) {
                        o = 0;
                        orientation = CLOCKWISE;
                    }else {
                        if (orientation == ANTICLOCKWISE) {
                            arcAngle -= 1;
                        }
                    }
                }

                repaint();
            }
        }
    }


    /**
     * Gets the font width length of the string
     */
    public class GetStringWidth {
        private static AffineTransform atf = new AffineTransform();  //You get an affine transformation

        private static FontRenderContext frc = new FontRenderContext(atf, true,
                true);  //Get the text content container

        public static int getStringHeight(String str, Font font) {  //Gets the width of the string under this font
            if (str == null || str.isEmpty() || font == null) {
                return 0;
            }
            return (int) font.getStringBounds(str, frc).getHeight();

        }

        public static int getStringWidth(String str, Font font) { //Gets the length of the string under this font
            if (str == null || str.isEmpty() || font == null) {
                return 0;
            }
            return (int) font.getStringBounds(str, frc).getWidth();
        }

    }


    /**
     * Dynamic JLabel, changing size based on text length
     */
    static class DynamicJLabel extends ShakeLabel {
        Font font;
        boolean CENTER=false;  //Determine whether you are centered
        int FRAMELENGTH;  //The length of the storage container
        public DynamicJLabel(String message, Font font, int y) {
            super(message); //Setting information
            this.setFont(font);
            this.setBounds(0,y, GetStringWidth.getStringWidth(message,font)+10, GetStringWidth.getStringHeight(message,font)+10);  //设置大小
            this.font=font;
        }

        /**
         * Modify the content. The default font remains unchanged
         * @param message  content
         */
        public void setTextDynamic(String message){
            this.setText(message);
            this.setBounds(this.getX(),this.getY(),GetStringWidth.getStringWidth(message,font)+10, GetStringWidth.getStringHeight(message,font)+10);  //设置大小
            if(CENTER) this.setCenter(FRAMELENGTH);
        }


        /**
         * Modify the content and modify the font
         * @param message Content to be modified
         * @param font The font to change
         */
        public void setTextDynamic(String message, Font font) {
            int x=this.getX();
            int y=this.getY();
            this.font=font;
            this.setText(message);
            this.setFont(font);
            this.setBounds(x,y,GetStringWidth.getStringWidth(message,font)+10, GetStringWidth.getStringHeight(message,font)+10);  //设置大小
            if(CENTER) this.setCenter(FRAMELENGTH);
        }

        /**
         * Center the component
         * @param framLength  Length of container
         */
        public void setCenter(int framLength) {
            int marge = (framLength - this.getWidth()) / 2; //Computational border
            this.setBounds(marge, this.getY(), this.getWidth(), this.getHeight());
            FRAMELENGTH = framLength;
            CENTER = true;
        }

        /**
         * uncenter
         */
        public void setNotCenter(){
            CENTER = false;
        }

        /**
         * Set starting coordinates
         * @param x Initial x-coordinate
         * @param y Initial y coordinate
         */
        public void setBoundsDynamic(int x, int y) {
            this.setBounds(x,y,this.getWidth(),this.getHeight());
        }

    }

    class Loading extends JPanel {

        private static final long serialVersionUID = 1551571546L;

        private Color color=Color.RED;
        private Timer timer;
        private int delay;
        private int startAngle;
        private int arcAngle = 0;
        private int orientation;

        public static final int CLOCKWISE = 0;
        public static final int ANTICLOCKWISE = 1;

        public Loading() {
            this.delay = 1;
            this.orientation = CLOCKWISE;
            init();
        }

        public Loading(int delay) {
            this.delay = delay;
            this.orientation = CLOCKWISE;
            init();
        }

        public Loading(int delay, int orientation) {
            this.delay = delay;
            this.orientation = orientation;
            init();
        }

        @Override
        public void show() {
            this.timer.start();
        }

        public void stopShow() {
            this.timer.stop();
        }

        /**
         * @param orientation	set the direction of rotation
         *
         * @beaninfo
         *        enum: CLOCKWISE LodingPanel.CLOCKWISE
         *        		ANTICLOCKWISE LodingPanel.ANTICLOCKWISE
         */
        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        private void init() {
            this.timer = new Timer(delay, new ReboundListener());
        }

        public void setColor(Color color) {
            this.color = color;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawArc(g);
        }

        private void drawArc(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g2d.setColor(Color.WHITE);
            g2d.drawArc(width / 2 - 75, height / 2 - 55, 20 + 110, 20 + 110, 0, 360);
            g2d.setColor(color);
            g2d.fillArc(width / 2 - 75, height / 2 - 55, 20 + 110, 20 + 110, startAngle, arcAngle);
            g2d.setColor(Color.WHITE);
            g2d.fillArc(width / 2 - 70, height / 2 - 50, 20 + 100, 20 + 100, 0, 360);
            g2d.dispose();
        }

        private class ReboundListener implements ActionListener {

            private int o = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startAngle < 360) {
                    switch (orientation) {
                        case CLOCKWISE:
                            startAngle = startAngle + 1;
                            break;
                        case ANTICLOCKWISE:
                            startAngle = startAngle - 1;
                            break;
                        default:
                            startAngle = startAngle + 1;
                            break;
                    }
                } else {
                    startAngle = 0;
                }

                if (o == 0) {
                    if (arcAngle >= 359) {
                        o = 1;
                        orientation = ANTICLOCKWISE;
                    }else {
                        if (orientation == CLOCKWISE) {
                            arcAngle += 1;
                        }
                    }
                }else {
                    if (arcAngle <= 1) {
                        o = 0;
                        orientation = CLOCKWISE;
                    }else {
                        if (orientation == ANTICLOCKWISE) {
                            arcAngle -= 1;
                        }
                    }
                }

                repaint();
            }
        }
    }


    class LoginHome implements ActionListener {

        public static boolean ANIMATION_KEEP_ON = false;
        static boolean iconified;
        private static ChooseBackButton switchMode = null;
        private static JTextField emailText;
        public static  RoundButton Rbut1;
        private static RoundButton Rbut2;
        public static DynamicJLabel plate;
        private ImageIcon leftOpenIcon;
        private RadioJLabel sendEmailLabel;
        private RadioJLabel leftContent;
        private RadioJLabel leftBack;
        private JLabel leftOpen;
        private Boolean Switch;
        private static String verrify;
        private static boolean leftOpening = false;
        private static TimerTask timer;
        private TimerTask timerOn;
        private Timer timer2;
        private static java.util.Timer timerFather=new java.util.Timer();;


        public static Frameless background;
        private static JLabel left = null;
        public static JPanel right;
        private final ImageIcon login;
        private final JLabel rightLabel;
        public static JTextField passwordMessage;
        public static JLabel sign;
        private static ImageIcon icon = null;
        public static ShakeLabel loginLabel;
        public static ShakeLabel AILabel;
        private static ImageIcon loginButtonIcon = null;
        private final ImageIcon AIButtonIcon;
        private final JButton loginButton;
        private final JButton AIButton;
        public static JTextField username;
        public static JPasswordField password;
        private static JLabel registerLabel = null;
        private static ImageIcon registerIcon = null;
        private static JTextField registerText2;
        private static ShakeLabel registerLabel2;
        public static ShakeLabel registerLabel5;
        private static JPasswordField registerPassword3;
        private static ShakeLabel registerLabel3;
        private static ShakeLabel registerLabel4;
        private static JPasswordField registerPassword4;
        private static JLabel registerMessage;
        private static JLabel registerLabelMessage1;
        public static DynamicJLabel wrongMessage;
        public static Loading load;
        public static Loading loadIn;
        private ImageIcon texton;
        private static String usernameMessage;
        private static String passwordMessage1;
        private JLabel registerbgLabel;
        public static String iconString;  //头像的String类型储存图片,默认是官方头像
        public static boolean isAlive = true;
        public static boolean switchFlag = false;  //判断是什么动画格式
        public static boolean isAddBlockRandom = false;
        private ViewServer viewServer = new ViewServerImpl();
        public MusicService musicService = new MusicServiceImpl();




        @SneakyThrows
        public LoginHome() {
            Menu.isBacking = false;

            System.setProperty("sun.java2d.noddraw", "true");
            iconString = ToPicture.imageToString(ImageIO.read(Resources.getResourceAsStream("login/sign.png")), "png");


            background = new Frameless(1380, 743, false);

            left = new JLabel();
            plate = new DynamicJLabel("Ataxx", new Font("Serif", Font.BOLD, 23), 56);
            plate.setForeground(new Color(30, 29, 29));

            ImageIcon toolBarIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/hamburger.png")));
            JLabel toolBar = new JLabel(toolBarIcon);
            right = new JPanel();

            RadioJLabel toolMenu = new RadioJLabel("");
            toolMenu.setColor(new Color(162, 159, 159, 0));

            RadioJLabel toolMenuItem2 = new RadioJLabel("");
            toolMenuItem2.setColor(new Color(162, 159, 159, 0));
            toolMenuItem2.setArc(15, 15);
            DynamicJLabel toolMenuMessage2 = new DynamicJLabel("Pause animation", new Font("Serif", Font.BOLD, 16), 141);
            toolMenuMessage2.setForeground(new Color(79, 78, 78, 0));


            switchMode = new ChooseBackButton("main/switch.png", "main/switchOn.png", "main/switchOn.png", toolMenuItem2, new DynamicJLabel("", null, 0), ChooseBackButton.Location.EAST) {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    switchMode.isClick(c -> {
                        musicService.playErrorMP3();
                        switchFlag = !switchFlag;
                        new Thread() {
                            @Override
                            public void run() {
                                setPicture(left);
                            }
                        }.start();

                    });
                }
            };

            switchMode.setColor(new Color(162, 159, 159, 0));

            final boolean[] toolMenuFlag = {false};
            toolBar.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (toolMenuFlag[0]) {
                        toolMenuFlag[0] = false;
                        toolMenuItem2.setColor(new Color(162, 159, 159, 0));
                        toolMenuMessage2.setForeground(new Color(79, 78, 78, 0));
                        switchMode.setBounds(left.getWidth() - 260, 138, 0, 0);
                        toolMenu.setColor(new Color(162, 159, 159, 0));


                        toolMenuItem2.repaint();
                        toolMenuMessage2.repaint();
                        switchMode.repaint();
                        toolMenu.repaint();
                        toolMenuFlag[0] = false;

                    } else {
                        toolMenuFlag[0] = true;
                        toolMenuItem2.setColor(new Color(162, 159, 159, 255));
                        toolMenuMessage2.setForeground(new Color(79, 78, 78, 255));
                        switchMode.setBounds(left.getWidth() - 260, 138, 35, 35);
                        toolMenu.setColor(new Color(162, 159, 159, 255));

                        toolMenuItem2.repaint();
                        toolMenuMessage2.repaint();
                        switchMode.repaint();
                        toolMenu.repaint();
                        toolMenuFlag[0] = true;

                    }
                }
            });



            background.addWindowListener(new WindowAdapter() {
                @SneakyThrows
                @Override
                public void windowIconified(WindowEvent e) {
                    iconified = true;
                }

                @SneakyThrows
                @Override
                public void windowDeiconified(WindowEvent e) {
                    iconified = false;
                }


            });
            Rbut1 = new RoundButton("", new Color(58, 124, 243, 190), new Color(92, 143, 236, 221), new Color(132, 171, 243, 181)) {
                @Override
                public void mouseClicked(MouseEvent e) {
                    musicService.playErrorMP3();
                    background.setExtendedState(JFrame.ICONIFIED);
                }

            };
            Rbut2 = new RoundButton("", new Color(243, 58, 101, 192), new Color(238, 70, 109, 189), new Color(252, 108, 141, 189)) {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    musicService.playErrorMP3();
                    float MAXTRANS = 1;
                    while (MAXTRANS >= 0) {
                        Thread.sleep(2);
//                    AWTUtilities.setWindowOpacity(background, MAXTRANS);  //半透明  //todo
                        MAXTRANS -= 0.05;
                    }
                    System.exit(1);
                }

            };

            wrongMessage = new DynamicJLabel("", new Font("Serif", Font.BOLD, 18), 249);
            wrongMessage.setForeground(new Color(215, 27, 71, 205));


            left.setBackground(new Color(0, 0, 0, 0));
            right.setBackground(new Color(0, 0, 0, 0));


            background.setLocation(250, 80);

            {
                login = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/login.png")));
                rightLabel = new JLabel(login);
                right.add(rightLabel, 0);
                rightLabel.setBounds(0, 0, login.getIconWidth(), login.getIconHeight());
            }
            {
                icon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/sign.png")));
                sign = new JLabel(icon);

            }
            {
                loginButtonIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/button.png")));
                ImageIcon loginButtonIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/buttonon.png")));
                loginButton = new JButton();
                loginButton.addActionListener(this);
                JLabel loginMessage = new JLabel("manual VS manual");
                loginLabel = new ShakeLabel(loginButtonIcon);
                loginMessage.setFont(new Font("Serif", Font.BOLD, 30));
                loginMessage.setForeground(new Color(255, 255, 255));
                loginMessage.setHorizontalAlignment(SwingConstants.CENTER);
                loginLabel.add(loginButton);
                loginLabel.add(loginMessage);
                loginMessage.setBounds(29, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                loginButton.setBounds(20, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                loginButton.setContentAreaFilled(false);
                loginButton.setBorder(null);
                loginButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        loginLabel.setIcon(loginButtonIconOn);
                        loginMessage.setBounds(55, 48, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                        loginButton.setBounds(20, 21, loginButtonIconOn.getIconWidth() - 70, loginButtonIconOn.getIconHeight() - 67);
                        loginLabel.setBounds(right.getWidth() - 40, 472-210, loginButtonIconOn.getIconWidth() + 30, loginButtonIconOn.getIconHeight());
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        musicService.playSuccessMP3();
                        loginLabel.setIcon(loginButtonIcon);
                        loginButton.setBounds(20, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                        loginLabel.setBounds(right.getWidth() - 25, 490-210, loginButtonIcon.getIconWidth() + 30, loginButtonIcon.getIconHeight());
                        loginMessage.setBounds(29, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                    }
                });

            }
            {
                AIButtonIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/button.png")));
                ImageIcon loginButtonIconOn = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/buttonon.png")));
                AIButton = new JButton();
                AIButton.addActionListener(this);
                JLabel loginMessage = new JLabel("manual VS AI");
                AILabel = new ShakeLabel(AIButtonIcon);
                loginMessage.setFont(new Font("Serif", Font.BOLD, 30));
                loginMessage.setForeground(new Color(255, 255, 255));
                loginMessage.setHorizontalAlignment(SwingConstants.CENTER);
                AILabel.add(AIButton);
                AILabel.add(loginMessage);
                loginMessage.setBounds(29, 21, AIButtonIcon.getIconWidth() - 70, AIButtonIcon.getIconHeight() - 67);
                AIButton.setBounds(20, 21, AIButtonIcon.getIconWidth() - 70, AIButtonIcon.getIconHeight() - 67);
                AIButton.setContentAreaFilled(false);
                AIButton.setBorder(null);
                AIButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        AILabel.setIcon(loginButtonIconOn);
                        loginMessage.setBounds(55, 48, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                        AIButton.setBounds(20, 21, loginButtonIconOn.getIconWidth() - 70, loginButtonIconOn.getIconHeight() - 67);
                        AILabel.setBounds(right.getWidth() - 40, 472-80, loginButtonIconOn.getIconWidth() + 30, loginButtonIconOn.getIconHeight());

                        if (e.getSource() == loginButton) {
                            viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_MANUAL, isAddBlockRandom);
                            background.setVisible(false);

                        } else if (e.getSource() == AIButton) {
                            viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_AI, isAddBlockRandom);
                            background.setVisible(false);

                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        musicService.playSuccessMP3();
                        AILabel.setIcon(loginButtonIcon);
                        AIButton.setBounds(20, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);
                        AILabel.setBounds(right.getWidth() - 25, 490-80, loginButtonIcon.getIconWidth() + 30, loginButtonIcon.getIconHeight());
                        loginMessage.setBounds(29, 21, loginButtonIcon.getIconWidth() - 70, loginButtonIcon.getIconHeight() - 67);

                        if (e.getSource() == loginButton) {
                            viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_MANUAL, isAddBlockRandom);
                            background.setVisible(false);


                        } else if (e.getSource() == AIButton) {
                            viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_AI, isAddBlockRandom);
                            background.setVisible(false);


                        }
                    }
                });

            }
            {
                registerIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("login/registericon.png")));
                registerLabel = new JLabel(registerIcon);
                JLabel registerMessage = new JLabel("Add Blocks");
                registerMessage.setFont(new Font("Courier", Font.BOLD, 17));
                registerMessage.setForeground(new Color(255, 255, 255));
                registerMessage.setHorizontalAlignment(SwingConstants.CENTER);
                JButton registerButton = new JButton();
                registerLabel.add(registerButton);
                registerLabel.add(registerMessage);
                registerMessage.setBounds(0, 0, registerIcon.getIconWidth(), registerIcon.getIconHeight());
                registerButton.setBounds(0, 0, registerIcon.getIconWidth(), registerIcon.getIconHeight());
                registerButton.setContentAreaFilled(false);
                registerButton.setBorder(null);
                final boolean[] flag = {true};
                final boolean[] start = {true};
                registerButton.addMouseListener(new MouseAdapter() {
                    @SneakyThrows
                    @Override
                    public void mousePressed(MouseEvent e) {
                        musicService.playErrorMP3();
                        isAddBlockRandom = !isAddBlockRandom;
                        if (isAddBlockRandom) {
                            wrongMessage.setTextDynamic("Add Random Blocks √");
                            wrongMessage.setForeground(new Color(123, 198, 229));
                        } else {
                            wrongMessage.setTextDynamic("Remove Random Blocks √");
                            wrongMessage.setForeground(new Color(123, 198, 229));

                        }
                    }
                });

            }






        }

        public static void pack() {

            background.add(Rbut1);
            background.add(Rbut2);
            if (isAlive)
                background.add(sign);
            if (!isAlive)
                background.add(load);
            background.add(loginLabel);
            background.add(AILabel);
            background.add(registerLabel);
            background.add(wrongMessage);
            background.add(plate);
            plate.setCenter(left.getWidth() + 1600);
            background.add(switchMode);
            background.add(right);
            background.add(left);


            new Thread(() -> {
                setPicture(left);
            }).start();
            try {
                background.setIconImage(ImageIO.read(Resources.getResourceAsStream("login/sign.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }


            left.setBounds(0, 85, 1380, 563);
            right.setBounds(630, 17, 800, 843);
            Rbut1.setBounds(right.getWidth() + 415, 60, 25, 25);
            Rbut2.setBounds(right.getWidth() + 450, 60, 25, 25);
            wrongMessage.setCenter(2039);
            if (isAlive)
                sign.setBounds(right.getX() + right.getWidth() / 2 - 73, 122, icon.getIconWidth(), icon.getIconHeight());
            if (!isAlive)
                load.setBounds(right.getX() + right.getWidth() / 2 - 79, 95, icon.getIconWidth() + 35, icon.getIconHeight() + 35);
            loginLabel.setBounds(right.getWidth() - 25, 490-210, loginButtonIcon.getIconWidth() + 30, loginButtonIcon.getIconHeight());
            AILabel.setBounds(right.getWidth() - 25, 490-80, loginButtonIcon.getIconWidth() + 30, loginButtonIcon.getIconHeight());
            registerLabel.setBounds(right.getWidth() + 172, 620-40, registerIcon.getIconWidth(), registerIcon.getIconHeight());
            switchMode.setBounds(left.getWidth() - 260, 138, 0, 0);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void setVisible(boolean b) {
            background.setVisible(true);
        }




        @SneakyThrows
        public static void setPicture(JLabel left) {
            ImageIcon Icon1;
            ImageIcon Icon2;
            ImageIcon Icon3;
            ImageIcon Icon4;
            ImageIcon Icon5;
            ImageIcon Icon6;
            ImageIcon Icon7;
            ImageIcon Icon8;
            ImageIcon Icon9;
            ImageIcon[] icons;
            try (InputStream icon1 = Resources.getResourceAsStream("newPicture/picture1.png");
                 InputStream icon2 = Resources.getResourceAsStream("newPicture/picture2.png");
                 InputStream icon3 = Resources.getResourceAsStream("newPicture/picture3.png");
                 InputStream icon4 = Resources.getResourceAsStream("newPicture/picture4.png");
                 InputStream icon5 = Resources.getResourceAsStream("newPicture/picture5.png");
                 InputStream icon6 = Resources.getResourceAsStream("newPicture/picture6.png");
                 InputStream icon7 = Resources.getResourceAsStream("newPicture/picture7.png");
                 InputStream icon8 = Resources.getResourceAsStream("newPicture/picture8.png");
                 InputStream icon9 = Resources.getResourceAsStream("newPicture/picture9.png")) {
                Icon1 = new ImageIcon(ImageIO.read(icon1));
                Icon2 = new ImageIcon(ImageIO.read(icon2));
                Icon3 = new ImageIcon(ImageIO.read(icon3));
                Icon4 = new ImageIcon(ImageIO.read(icon4));
                Icon5 = new ImageIcon(ImageIO.read(icon5));
                Icon6 = new ImageIcon(ImageIO.read(icon6));
                Icon7 = new ImageIcon(ImageIO.read(icon7));
                Icon8 = new ImageIcon(ImageIO.read(icon8));
                Icon9 = new ImageIcon(ImageIO.read(icon9));
                icons = new ImageIcon[]{Icon1, Icon2, Icon3, Icon4,Icon5,Icon6,Icon7,Icon8,Icon9};
            }
            JLabel background = new JLabel(Icon1);
            JLabel topBack = new JLabel();
            JLabel top = new JLabel(icons[1]);
            JLabel bottomBack = new JLabel();
            JLabel bottom = new JLabel(icons[1]);
            left.add(topBack);
            left.add(bottomBack);
            bottomBack.add(bottom);
            topBack.add(top);
            left.add(background);
            background.setBounds(0, 0, Icon1.getIconWidth(), Icon1.getIconHeight());
            topBack.setBounds(0, -Icon1.getIconHeight(), Icon1.getIconWidth(), Icon1.getIconHeight());
            top.setBounds(0, Icon1.getIconHeight(), Icon1.getIconWidth(), Icon1.getIconHeight());
            bottomBack.setBounds(0, Icon1.getIconHeight(), Icon1.getIconWidth(), Icon1.getIconHeight());
            bottom.setBounds(0, -Icon1.getIconHeight(), Icon1.getIconWidth(), Icon1.getIconHeight());
            if (switchFlag) {
                ANIMATION_KEEP_ON = !ANIMATION_KEEP_ON;

            } else {
                ANIMATION_KEEP_ON = !ANIMATION_KEEP_ON;
                Thread.sleep(1000);
                timer = new TimerTask() {
                    int index = 0;

                    @SneakyThrows
                    @Override
                    public void run() {
                        if (!ANIMATION_KEEP_ON) {
                            while (true) {
                                Thread.sleep(1000);
                                if (ANIMATION_KEEP_ON)
                                    break;
                            }
                        }
                        index++;
                        int All = Icon1.getIconHeight() / 2;
                        int Top11 = Icon1.getIconHeight();
                        int Top22 = -Icon1.getIconHeight();
                        int Bottom11 = -Icon1.getIconHeight();
                        int Bottom22 = Icon1.getIconHeight();
                        top.setBounds(0, Top11, Icon1.getIconWidth(), Icon1.getIconHeight());
                        topBack.setBounds(0, Top22, Icon1.getIconWidth(), Icon1.getIconHeight());
                        bottom.setBounds(0, Bottom11, Icon1.getIconWidth(), Icon1.getIconHeight());
                        bottomBack.setBounds(0, Bottom22, Icon1.getIconWidth(), Icon1.getIconHeight());

                        top.setIcon(icons[index % 9]);
                        bottom.setIcon(icons[index % 9]);
                        while ((All--) > 0) {
                            Thread.sleep(6);
                            top.setBounds(0, --Top11, Icon1.getIconWidth(), Icon1.getIconHeight());
                            topBack.setBounds(0, ++Top22, Icon1.getIconWidth(), Icon1.getIconHeight());
                            bottom.setBounds(0, ++Bottom11, Icon1.getIconWidth(), Icon1.getIconHeight());
                            bottomBack.setBounds(0, --Bottom22, Icon1.getIconWidth(), Icon1.getIconHeight());
                        }
                        background.setIcon(icons[(index) % 9]);
                    }
                };
                Thread.sleep(4000);
                timerFather.schedule(LoginHome.timer, new Date(), 9000);

            }
        }

        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == loginButton) {
                viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_MANUAL, isAddBlockRandom);
                background.setVisible(false);

            } else if (e.getSource() == AIButton) {
                viewServer.DisplayConnectedClient(ViewServer.PATTERN_MANUAL_VS_AI, isAddBlockRandom);
                background.setVisible(false);

            }
        }

    }


    class Chess extends RadioJLabel{

        public static int BLUE = 3;

        public static int RED = 2;

        public int type;

        public Chess(int type) {
            super();
            if (type == BLUE) {
                super.setColor(getColorFromHex("#AFD3E2"));
                this.type = BLUE;
            } else {
                super.setColor(getColorFromHex("#FF6969"));
                this.type = RED;
            }
            super.setArc(90,90);
            super.setSize(100,100);

        }

        @Override
        public Color getColor() {
            return super.getColor();
        }

        @Override
        public void setArc(int arcWidth, int arcHeight) {
            super.setArc(arcWidth, arcHeight);
        }
    }


    /**
     * A custom rimless container that requires an outside call:
     * Frameless.setDefaultLookAndFeelDecorated(true);  //Cancel the Windows local appearance
     * AWTUtilities.setWindowShape(this, new RoundRectangle2D.Double(30.0D, 30.0D, this.getWidth(), this.getHeight(), 26.0D, 26.0D));  //Rounded corner the container
     */
    class Frameless extends JFrame {
        private int xOld = 0;  //Used to handle drag events and represents the coordinates when the mouse is pressed, relative to the JFrame
        private int yOld = 0;

        /**
         * If flag is true, add a maximize minimize button
         */
        public Frameless(int width, int height, boolean flag) {
            super("");  //Set name

            this.setUndecorated(true);  //No border
            this.setBackground(new Color(0,0,0,0));
//        this.getContentPane().setBackground(Color.DARK_GRAY);  //Set the background
            this.setLayout(null);  //Absolute positioning
            this.setResizable(false);  //unamplified
            this.setLocationRelativeTo(null);  //center
            this.setSize(width, height);  //Set size
            if (flag) {  //If the flag bit is true, add minimize and maximize buttons
                this.setRadioButton();
            }
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(1);
                }
            });  //Join window listening;
            //Handle mouse click events
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    xOld = e.getX();  //Store the coordinates of the click
                    yOld = e.getY();
                }
            });
            //Handle mouse drag events
            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int xOnScreen = e.getXOnScreen(); //Gets the current mouse drag x coordinate
                    int yOnScreen = e.getYOnScreen(); //Gets the y coordinate of the current mouse drag
                    int xx = xOnScreen - xOld;  //Moving difference
                    int yy = yOnScreen - yOld;
                    Frameless.this.setLocation(xx,yy);  //移动窗口
                }
            });


        }

        /**
         * Parameterless construction
         */
        public Frameless() {
            this(800,400,false);
        }

        /**
         * Bind to a container and move together
         * @param brother
         */
        public void bind(JFrame brother) {
            //Handle mouse drag events
            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int xOnScreen = e.getXOnScreen(); //Gets the current mouse drag x coordinate
                    int yOnScreen = e.getYOnScreen(); //Gets the y coordinate of the current mouse drag
                    int xx = xOnScreen - xOld;  //Moving difference
                    int yy = yOnScreen - yOld;
                    Frameless.this.setLocation(xx,yy);  //Moving window
                }
            });
        }
        /**
         * Add Minimize and close to the top right corner of the container
         */
        private void setRadioButton() {
            // Create the round button in the upper right corner and add a listener
            RoundButton Rbut1 = new RoundButton("", new Color(58, 124, 243, 190), new Color(92, 143, 236, 221),new Color(132, 171, 243, 181)) {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Frameless.this.setExtendedState(JFrame.ICONIFIED);
                }

            };
            RoundButton Rbut2 = new RoundButton("", new Color(243, 58, 101, 192), new Color(238, 70, 109, 189),new Color(252, 108, 141, 189)){
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.exit(1);
                }

            };
            Rbut1.setBounds(Frameless.this.getWidth() - 80,  20, 20, 20);
            Rbut2.setBounds(Frameless.this.getWidth() - 50, 20, 20, 20);
            Frameless.this.add(Rbut1);
            Frameless.this.add(Rbut2);

        }
    }

    class LoadingHome extends RadioJLabel {

        private int yOld;
        private int xOld;

        public LoadingHome() {
            this.setArc(30, 30);
            this.setColor(new Color(166, 163, 163));
            this.setBounds(0, 50, 1300, 743);


            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    xOld = e.getX();
                    yOld = e.getY();
                }
            });
            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int xOnScreen = e.getXOnScreen();
                    int yOnScreen = e.getYOnScreen();
                    int xx = xOnScreen - xOld;
                    int yy = yOnScreen - yOld;
                    LoadingHome.this.setLocation(xx, yy - 50);  //移动窗口
                }
            });

            this.setColor(new Color(51, 51, 51));

            LoadingBack load1 = new LoadingBack(130, getColorFromHex("#F317FE"));
            LoadingBack load2 = new LoadingBack(170, getColorFromHex("#AD55CB"));
            LoadingBack load3 = new LoadingBack(210, getColorFromHex("#9172DB"));
            load1.show();
            load2.show();
            load3.show();
            this.add(load1);
            this.add(load2);
            this.add(load3);
            load1.setSize(130,130);
            load2.setSize(170,170);
            load3.setSize(210,210);
            load1.setLocation(100+this.getWidth()/2 - load1.getWidth()-39, 120-81+this.getHeight()/2 - load1.getHeight());
            load2.setLocation(100+this.getWidth()/2 - load2.getWidth()-19, 120-60+this.getHeight()/2 - load2.getHeight());
            load3.setLocation(100+this.getWidth()/2 - load3.getWidth(), 120+this.getHeight()/2 - load3.getHeight()-40);
        }
    }

     class LoadingBack extends RadioJLabel {

        private static final long serialVersionUID = 1551571546L;

        private Color color = Color.RED;
        private Timer timer;
        private int delay;
        private int startAngle=-10;
        private int arcAngle = 0;
        private int orientation;
        private int WIDTH = 130;


        public static final int CLOCKWISE = 0;
        public static final int ANTICLOCKWISE = 1;

        public LoadingBack() {
            this.delay = 1;
            this.orientation = CLOCKWISE;
            init();
        }

        public LoadingBack(int delay) {
            this.delay = delay;
            this.orientation = CLOCKWISE;
            init();
        }

        public LoadingBack(int width, Color color) {
            this.WIDTH = width;
            this.delay = 1;
            this.orientation = CLOCKWISE;
            this.color = color;
            if (WIDTH > 110 && WIDTH <= 130)
                this.setArc(WIDTH, WIDTH);
            if (WIDTH > 130 && WIDTH <= 170)
                this.setArc(WIDTH - 10, WIDTH - 10);
            if (WIDTH > 170 && WIDTH <= 210)
                this.setArc(WIDTH - 10, WIDTH - 10);
//
            init();
        }

        public LoadingBack(int delay, int orientation) {
            this.delay = delay;
            this.orientation = orientation;
            init();
        }

        @Override
        public void show() {
            this.timer.start();
        }

        /**
         * @param orientation set the direction of rotation
         * @beaninfo enum: CLOCKWISE LodingPanel.CLOCKWISE
         * ANTICLOCKWISE LodingPanel.ANTICLOCKWISE
         */
        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        private void init() {
            this.timer = new Timer(delay, new ReboundListener());
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawArc(g);
        }

        private void drawArc(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g2d.setColor(getColorFromHex("#333333"));
            g2d.drawArc(width / 2 - WIDTH / 2, height / 2 - WIDTH / 2, WIDTH, WIDTH, 0, 360);
            g2d.setColor(color);
            if (WIDTH > 110 && WIDTH <= 130)
                g2d.fillArc(width / 2 - WIDTH / 2, height / 2 - WIDTH / 2, WIDTH, WIDTH, startAngle, arcAngle);
            if (WIDTH > 130 && WIDTH <= 170)
                g2d.fillArc(width / 2 - WIDTH / 2, height / 2 - WIDTH / 2, WIDTH, WIDTH, startAngle + 30, arcAngle);
            if (WIDTH > 170 && WIDTH <= 210)
                g2d.fillArc(width / 2 - WIDTH / 2, height / 2 - WIDTH / 2, WIDTH, WIDTH, startAngle + 60, arcAngle);

            g2d.setColor(getColorFromHex("#333333"));
            g2d.fillArc(width / 2 - WIDTH / 2 + 5, height / 2 - WIDTH / 2 + 5, WIDTH - 10, WIDTH - 10, 0, 360);
            g2d.dispose();
        }

        private class ReboundListener implements ActionListener {

            private int o = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (WIDTH > 110 && WIDTH <= 130) {
                    startAngle = startAngle - 5;
                    arcAngle = 100;
                }
                if (WIDTH > 130 && WIDTH <= 170) {
                    startAngle = startAngle - 4;
                    arcAngle = 100;
                }
                if (WIDTH > 170 && WIDTH <= 210) {
                    startAngle = startAngle - 3;
                    arcAngle = 100;
                }

                repaint();
            }
        }
    }


    /**
     * Main interface of client, left is the menu, add friends, agree to friend invitation, reject friend invitation;
     * Check your friends' status and chat with them;
     * Change theme colors, etc
     * On the right is the blog, you can post the blog, delete the blog and move the blog location
     */
    class Home extends Observable implements ActionListener, Minimize {

        public final DynamicJLabel serverClosedMessage = new DynamicJLabel("Server disconnection", new Font("Serif", Font.BOLD, 10), 107);
        public static Point POINT;
        public static ServerLoading ServerCloseLoad;
        public static JLabel menu;
        public static ImageIcon menuIcon;
        public static JLabel iconLabel;
        public static BufferedImage icon;
        public static MouseAdapter menuOpen;
        public static BufferedImage transparencyIcon;
        public static RadioJLabelNew menuHomeUser;
        public static RadioJLabelNew menuHomeUser1;
        public static RadioJLabelNew menuHomeUser2;
        public static RadioJLabelNew menuHomeUser3;
        public static RadioJLabelNew menuHomeUser4;
        public static RadioJLabelNew menuHomeUser5;
        public static RadioJLabelNew menuHomeUser6;
        public static MouseAdapter mouseAd;
        public static JLabel menuHomeBack;
        public static Frameless back;
        public static RadioJLabelNew home;
        public static RadioJLabel homeBack;
        private ImageIcon newMenuIcon;
        public static JLabel menuBack = new JLabel();
        public static JLabel menuTop = new JLabel();
        public static boolean[] menuFlag = {false};
        public static boolean[] menuFlag1 = {false};
        public static boolean[] menuFlag2 = {false};
        public static boolean[] menuFlag3 = {false};
        public static boolean[] menuFlag4 = {false};
        public static boolean[] menuFlag5 = {false};
        public static boolean[] menuFlag6 = {false};
        public static int openMenuIndex = -1;
        public static boolean isMenuChild = false;
        public static boolean inHome;
        private boolean iconified;
        private int xOld;
        private int yOld;
        public int pattern = 0;
        public boolean isAddBlocks = false;
        public static DynamicJLabel wrongMessage;
        public static DynamicJLabel prompt;
        public static DynamicJLabel redName;
        public static DynamicJLabel redScore;
        public static DynamicJLabel blueName;
        public static DynamicJLabel blueScore;
        public static RadioJLabel[][] chessBoard = new RadioJLabel[7][7];
        public static DynamicJLabel abscissa;
        public static DynamicJLabel ordinate[];
        public static DynamicJLabel tip1;
        public static DynamicJLabel tip2;
        public static char[] XNumberLetters = {'\0', 'a', 'b', 'c', 'd','e', 'f', 'g'};






        MusicService musicService = new MusicServiceImpl();

        public ViewServer viewServer = new ViewServerImpl();


        public void setPattern(int pattern) {
            this.pattern = pattern;
        }

        public void setIsAddBlocks(boolean isAddBlocks) {
            this.isAddBlocks = isAddBlocks;
        }

        public static void setVisible(boolean b) {
            back.setVisible(b);
        }

        public Home() {
            new Thread(() ->{
                while (!Menu.isBacking) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println(Menu.isBacking);
                if (Menu.isBacking) {
                    LoginHome.background.setVisible(true);
                }
            }).start();
            back = new Frameless(1300, 843, false);
            back.setUndecorated(true);
            try {
                back.setIconImage(ImageIO.read(Resources.getResourceAsStream("login/sign.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            homeBack = new RadioJLabel("");
            homeBack.setColor(getColorFromHex("#212A3E"));
            homeBack.setArc(40, 40);

            back.addWindowListener(new WindowAdapter() {
                @SneakyThrows
                @Override
                public void windowIconified(WindowEvent e) {
                    Home.super.setChanged();
                    Home.super.notifyObservers(true);
                    iconified = true;
                    minimize();
                }

                @SneakyThrows
                @Override
                public void windowDeiconified(WindowEvent e) {
                    iconified = false;
                    maximize();
                    Thread.sleep(1000);
                }

            });



            home = new RadioJLabelNew("");
            menuHomeBack = new JLabel("");
            menuHomeUser = new RadioJLabelNew("");
            menuHomeUser.setColor(new Color(239, 238, 238));
            menuHomeUser1 = new RadioJLabelNew("");
            menuHomeUser1.setColor(new Color(239, 238, 238));
            menuHomeUser2 = new RadioJLabelNew("");
            menuHomeUser2.setColor(new Color(239, 238, 238));
            menuHomeUser3 = new RadioJLabelNew("");
            menuHomeUser3.setColor(new Color(239, 238, 238));
            menuHomeUser4 = new RadioJLabelNew("");
            menuHomeUser4.setColor(new Color(239, 238, 238));
            menuHomeUser5 = new RadioJLabelNew("");
            menuHomeUser5.setColor(new Color(239, 238, 238));
            menuHomeUser6 = new RadioJLabelNew("");
            menuHomeUser6.setColor(new Color(239, 238, 238));


            {
                final boolean[] isChooseOne = {false};
                final int[] chooseX = {-1};
                final int[] chooseY = {-1};
                final int[] chooseXEnd = {-1};
                final int[] chooseYEnd = {-1};
                final Chess[] frontChooseChess = new Chess[1];
                int initLeft = 180;
                int initTop = 10;
                final boolean[] isChoose = {false};
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 7; j++) {
                        chessBoard[i][j] = new RadioJLabel();
                        chessBoard[i][j].setArc(0,0);
                        chessBoard[i][j].setSize(90,90);
                        chessBoard[i][j].setLocation(initLeft+chessBoard[i][j].getWidth()*j,initTop+chessBoard[i][j].getHeight()*i);

                        if ((i + j) % 2 == 0) {
                            chessBoard[i][j].setColor(getColorFromHex("#9BA4B5"));
                        } else {
                            chessBoard[i][j].setColor(getColorFromHex("#F1F6F9"));
                        }
                        homeBack.add(chessBoard[i][j]);
                        int finalI = i;
                        int finalJ = j;
                        chessBoard[i][j].addMouseListener(new MouseAdapter() {
                            @SneakyThrows
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (!Menu.gameOn&&!Menu.isAddingBlocks) {

                                    tip2.setTextDynamic("The game has not begun!");
                                    musicService.playErrorMP3();
                                    tip2.setFont(new Font("Serif", Font.BOLD, 14));
                                    tip2.setForeground(new Color(245, 87, 87));
                                    tip2.setCenter(230);
                                    return;

                                }
                                if (Menu.gameIsEnd) {
                                    tip2.setTextDynamic("Please Refresh Match!");
                                    musicService.playErrorMP3();
                                    tip2.setFont(new Font("Serif", Font.BOLD, 14));
                                    tip2.setForeground(new Color(245, 87, 87));
                                    tip2.setCenter(230);
                                    return;
                                }

                                if (Menu.gamePause) {
                                    tip2.setTextDynamic("The game was paused!");
                                    musicService.playErrorMP3();
                                    tip2.setFont(new Font("Serif", Font.BOLD, 14));
                                    tip2.setForeground(new Color(245, 87, 87));
                                    tip2.setCenter(230);
                                    return;

                                }
                                if (Menu.isAddingBlocks) {
                                    if (finalI == 0 && finalJ == 0 || finalI == 0 && finalJ == 6 || finalI == 6 && finalJ == 6 || finalI == 6 && finalJ == 0) {
                                        tip2.setTextDynamic("Pick Wrong!");
                                        musicService.playErrorMP3();
                                        tip2.setForeground(new Color(245, 87, 87));
                                        tip2.setFont(new Font("Serif", Font.BOLD, 21));
                                        tip2.setCenter(200);

                                        return;
                                    }
                                    ImageIcon blockImage = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/block.png")));
                                    RadioJLabel blockLabel = new RadioJLabel(blockImage);
                                    blockLabel.setColor(new Color(0, 0, 0, 0));
                                    chessBoard[finalI][finalJ].add(blockLabel);
                                    blockLabel.setBounds(8, 8, blockImage.getIconWidth(), blockImage.getIconHeight());
                                    Menu.promptNow = "block " + XNumberLetters[finalJ + 1] + (7 - finalI);
                                    GUI.blocks[finalI][finalJ] = 1;
                                    GUI.board.setBlock(XNumberLetters[finalJ+1],Character.forDigit(finalI+1,10) );

                                    musicService.playChessMP3();

                                    RadioJLabel blockLabelXReflect = new RadioJLabel(blockImage);
                                    blockLabelXReflect.setColor(new Color(0, 0, 0, 0));
                                    chessBoard[finalI][6-finalJ].add(blockLabelXReflect);
                                    blockLabelXReflect.setBounds(8, 8, blockImage.getIconWidth(), blockImage.getIconHeight());
                                    GUI.blocks[finalI][6-finalJ] = 1;


                                    RadioJLabel blockLabelYReflect = new RadioJLabel(blockImage);
                                    blockLabelYReflect.setColor(new Color(0, 0, 0, 0));
                                    chessBoard[6-finalI][finalJ].add(blockLabelYReflect);
                                    blockLabelYReflect.setBounds(8, 8, blockImage.getIconWidth(), blockImage.getIconHeight());
                                    GUI.blocks[6-finalI][finalJ] = 1;


                                    RadioJLabel blockLabelXYReflect = new RadioJLabel(blockImage);
                                    blockLabelXYReflect.setColor(new Color(0, 0, 0, 0));
                                    chessBoard[6-finalI][6-finalJ].add(blockLabelXYReflect);
                                    blockLabelXYReflect.setBounds(8, 8, blockImage.getIconWidth(), blockImage.getIconHeight());
                                    GUI.blocks[6-finalI][6-finalJ] = 1;

                                    return;
                                }
                                if (!isChooseOne[0]) {
                                    if (!(GUI.board.ataxxBoard[Board.index(XNumberLetters[finalJ+1], Character.forDigit(7-finalI,10))].ordinal() == Menu.COLOR_NOW.ordinal())) {
                                        tip2.setTextDynamic("Pick Wrong!");
                                        musicService.playErrorMP3();
                                        tip2.setForeground(new Color(245, 87, 87));
                                        tip2.setCenter(200);
                                        tip2.setFont(new Font("Serif", Font.BOLD, 21));
                                        return;
                                    }
                                }
                                if (GUI.board.ataxxBoard[Board.index(XNumberLetters[finalJ+1], Character.forDigit(7-finalI,10))] == PieceState.RED || GUI.board.ataxxBoard[Board.index(Home.XNumberLetters[finalJ+1], Character.forDigit(7-finalI,10))] == PieceState.BLUE) {
                                    if (frontChooseChess[0]!=null) {
                                        if (frontChooseChess[0].type == Chess.RED) {
                                            frontChooseChess[0].setColor(getColorFromHex("#FF6969"));
                                        } else {
                                            frontChooseChess[0].setColor(getColorFromHex("#AFD3E2"));
                                        }
                                    }
                                    tip2.setTextDynamic("");
                                    isChooseOne[0] = true;
                                    chooseX[0] = finalI;
                                    chooseY[0] = finalJ;
                                    if (GUI.chess[6-finalI][finalJ].type == Chess.RED) {
                                        GUI.chess[6-finalI][finalJ].setColor(new Color(250, 85, 85));
                                        GUI.chess[6-finalI][finalJ].repaint();
                                        frontChooseChess[0] = GUI.chess[6 - finalI][finalJ];
                                    } else {
                                        GUI.chess[6-finalI][finalJ].setColor(new Color(123, 198, 229));
                                        GUI.chess[6-finalI][finalJ].repaint();
                                        frontChooseChess[0] = GUI.chess[6 - finalI][finalJ];

                                    }
                                } else {
                                    if (isChooseOne[0]) {
                                        isChooseOne[0] = false;
                                        chooseXEnd[0] = finalI;
                                        chooseYEnd[0] = finalJ;
                                        musicService.playChessMP3();
                                        Menu.promptNow = ""+XNumberLetters[chooseY[0]+1] + (7-chooseX[0]) + "-" + XNumberLetters[chooseYEnd[0]+1] + (7-chooseXEnd[0]);
                                        if (frontChooseChess[0].type == Chess.RED) {
                                            frontChooseChess[0].setColor(getColorFromHex("#FF6969"));
                                        } else {
                                            frontChooseChess[0].setColor(getColorFromHex("#AFD3E2"));
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                GUI.HomeIsInit = true;
                abscissa = new DynamicJLabel("a            b            c            d            e            f            g", new Font("Serif", Font.BOLD, 25), initTop-7+chessBoard[0][0].getHeight()*7);
                ordinate = new DynamicJLabel[7];
                tip1 = new DynamicJLabel(Menu.COLOR_NOW.toString()+" Round", new Font("Serif", Font.BOLD, 21), initTop-25+chessBoard[0][0].getHeight());
                tip2 = new DynamicJLabel("", new Font("Serif", Font.BOLD, 21), initTop - 7 + chessBoard[0][0].getHeight()+50 );
                int ordinateValue = 7;
                for (int i = 0; i < 7; i++) {
                    ordinate[i] = new DynamicJLabel(String.valueOf(ordinateValue--), new Font("Serif", Font.BOLD, 25), initTop+25+chessBoard[0][0].getHeight()*i);
                    ordinate[i].setForeground(new Color(255, 255, 255));
                    homeBack.add(ordinate[i]);
                    ordinate[i].setCenter(325);


                }
                abscissa.setForeground(new Color(255, 255, 255));
                tip1.setForeground(new Color(224, 78, 78));
                tip2.setForeground(new Color(245, 87, 87));
                tip1.setCenter(200);
                tip2.setCenter(200);
                abscissa.setCenter(1000);
                homeBack.add(abscissa);
                homeBack.add(tip1);
                homeBack.add(tip2);

                //Add the scoreboard
                redName = new DynamicJLabel("RED", new Font("Serif", Font.BOLD, 25), 140);
                redName.setForeground(new Color(224, 78, 78));
                redName.setCenter(1790);
                homeBack.add(redName);

                redScore = new DynamicJLabel("0", new Font("Serif", Font.BOLD, 28), 203);
                redScore.setForeground(new Color(255,255,255));
                redScore.setCenter(1790);
                homeBack.add(redScore);

                ImageIcon vs = null;
                try {
                    vs = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/vs.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RadioJLabel VSLabel = new RadioJLabel(vs);
                VSLabel.setColor(new Color(0, 0, 0, 0));
                homeBack.add(VSLabel);
                VSLabel.setBounds(860, 290, vs.getIconWidth(), vs.getIconHeight());

                blueName = new DynamicJLabel("BLUE", new Font("Serif", Font.BOLD, 25), 390);
                blueName.setForeground(new Color(102, 164, 245, 118));
                blueName.setCenter(1790);
                homeBack.add(blueName);

                blueScore = new DynamicJLabel("0", new Font("Serif", Font.BOLD, 28), 463);
                blueScore.setForeground(new Color(255,255,255));
                blueScore.setCenter(1790);
                homeBack.add(blueScore);



            }

            final java.util.Timer[] timer = {new java.util.Timer(), new java.util.Timer()};

            //Handle mouse click events
            home.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    xOld = e.getX();  //Store the coordinates of the click
                    yOld = e.getY();
                }
            });
            //Handle mouse drag events
            home.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int xOnScreen = e.getXOnScreen(); //Gets the current mouse drag x coordinate
                    int yOnScreen = e.getYOnScreen();
                    int xx = xOnScreen - xOld;  //Moving difference
                    int yy = yOnScreen - yOld;
                    back.setLocation(xx, yy - 50);  //Moving window
                }
            });


            // Create the round button in the upper right corner and add a listener
            RoundButton Rbut1 = new RoundButton("", new Color(58, 124, 243, 190), new Color(92, 143, 236, 221), new Color(132, 171, 243, 181)) {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    musicService.playErrorMP3();
                    float MAXTRANS = 1;  //transparency
                    while (MAXTRANS >= 0) {
                        Thread.sleep(2);
                        MAXTRANS -= 0.03;
                    }
                    back.setExtendedState(JFrame.ICONIFIED);
                }

            };
            RoundButton Rbut2 = new RoundButton("", new Color(243, 58, 101, 192), new Color(238, 70, 109, 189), new Color(252, 108, 141, 189)) {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    musicService.playErrorMP3();
                    float MAXTRANS = 1;  //transparency
                    while (MAXTRANS >= 0) {
                        Thread.sleep(2);
                        MAXTRANS -= 0.03;
                    }
                    System.exit(1);
                }

            };

            home.setArc(30, 30);

            menuHomeUser.setArc(30, 30);
            menuHomeUser1.setArc(30, 30);
            menuHomeUser2.setArc(30, 30);
            menuHomeUser3.setArc(30, 30);
            menuHomeUser4.setArc(30, 30);
            menuHomeUser5.setArc(30, 30);
            menuHomeUser6.setArc(30, 30);
            home.setColor(new Color(Menu.homeColor1, Menu.homeColor2, Menu.homeColor3));
            home.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    inHome = true;  //The main page is displayed
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    inHome = false;
                }
            });


//

            {  //Menu add picture
                try {
                    menuIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/menubar2.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                menu = new JLabel(menuIcon);
                //Initialization menu
                Menu.init();

            }

            {  //Add avatar
                Constant.getMyIcon = true;  //Modify flag bit
                setIcon();
                prompt = new DynamicJLabel("Please start", new Font("Serif", Font.BOLD, 12), 113);
                prompt.setForeground(getColorFromHex("#212a3e"));
                prompt.setCenter(90);
                menu.add(prompt);
            }


            back.setLocation(310, 120);
            Rbut1.setArc(25, 25);   //Modify the rounded camber of the small button
            Rbut2.setArc(25, 25);




            LoadingHome loadingHome = new LoadingHome();//Add loading interface
            back.add(loadingHome);
            back.setVisible(true);  //Window visualization
            float MAXTRANS = 0;  //transparency
            while (MAXTRANS <= 1.0) {
                try {
                    Thread.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MAXTRANS += 0.02;
            }

            //Adds a load bar when the server is disconnected
            //Prompt when not connected
            serverClosedMessage.setForeground(new Color(250, 38, 38, 0));
            serverClosedMessage.setCenter(90);
            menu.add(serverClosedMessage);


            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    Thread.sleep(2000);

                    float MAXTRANS = 1;  //transparency
                    while (MAXTRANS >= 0) {
                        Thread.sleep(4);
                        MAXTRANS -= 0.02;
                    }

                    loadingHome.setSize(0, 0);



                    back.add(menu);  //Add menu
                    back.add(menuTop);  //background
                    menuBack.add(menuTop);


                    back.add(menuBack);  //background
                    back.add(menuHomeBack);  //Click on the content background of the menu expansion
                    menuHomeBack.add(menuHomeUser);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser1);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser2);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser3);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser4);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser5);   //Click the contents of the menu expansion
                    menuHomeBack.add(menuHomeUser6);   //Click the contents of the menu expansion


                    home.add(Rbut1);  //Add top right button
                    home.add(Rbut2);
                    back.add(homeBack); //background
                    back.add(home);  //Join the main page



                    home.setBounds(0, 50, 1300, 743);
                    homeBack.setBounds(240, 80, 960, 673);
                    menu.setBounds(50, 20, menuIcon.getIconWidth(), menuIcon.getIconHeight());
                    Rbut1.setBounds(home.getWidth() - 90, 20, 25, 25);  //Set the small button position
                    Rbut2.setBounds(home.getWidth() - 50, 20, 25, 25);


                    float MAXTRANS2 = 0;  //透明度
                    while (MAXTRANS2 <= 1.0) {
                        Thread.sleep(2);
                        MAXTRANS2 += 0.02;
                    }

                }
            }.start();

            int INITLEFT = back.getX();   //The first left coordinate
            int INITOP = back.getY();   //The top coordinate at the beginning
            final int[] MOVEAMOUNTX = {0};  //Move the number of X's
            final int[] MOVEAMOUNTY = {0};  //I'm going to move Y

            java.util.Timer timer3 = new java.util.Timer();
            timer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    MOVEAMOUNTX[0] = back.getX() - INITLEFT;
                    MOVEAMOUNTY[0] = back.getY() - INITOP;
                    POINT = MouseInfo.getPointerInfo().getLocation();
                }
            }, 1000, 100);

            // Start the background thread to detect menu expansion
            java.util.Timer timer2 = new java.util.Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Thread() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            if (Constant.whetherFriendsToTableIndex) {  // Check whether the initialization is complete to prevent the null pointer exception. Here the label is whether the user name corresponds to the chat window
                                if (menuTop.getX() > -118 && (POINT.getX() >= 309 + MOVEAMOUNTX[0] && POINT.getX() <= 357 + MOVEAMOUNTX[0]) && (POINT.getY() >= 171 + MOVEAMOUNTY[0] && POINT.getY() <= 912 + MOVEAMOUNTY[0])) {  //Exit in the left half
                                    Thread.sleep(500);
                                    if (menuTop.getX() > -117) {
                                        viewServer.MenuShrink();  //Start to shrink
                                        Thread.sleep(2000);
                                        Menu.isShrink = false;
                                        Menu.isOut = false;
                                    }

                                } else if (menuTop.getX() > -117 && (POINT.getX() >= 578 + MOVEAMOUNTX[0]) && (POINT.getY() >= 171 + MOVEAMOUNTY[0] && POINT.getY() <= 912 + MOVEAMOUNTY[0])) { //The far right half is out
                                    Thread.sleep(500);
                                    if (menuTop.getX() > -118) {
                                        viewServer.MenuShrink();  //Start to shrink
                                        Thread.sleep(2000);
                                        Menu.isShrink = false;
                                        Menu.isOut = false;
                                    }
                                }
                            }
                        }
                    }.start();
                }
            }, 1000, 500);


        }



        /**
         * Set profile picture
         */
        @SneakyThrows
        public void setIcon() {
            ImageIcon defaultIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/icon.png")));
            iconLabel = new JLabel(defaultIcon);  //Default avatar

            ServerCloseLoad = new ServerLoading();
            ServerCloseLoad.setBounds(3, 16, 73, 90);
            iconLabel.setBounds(1, 12, 72, 84);
            ServerCloseLoad.setBackground(new Color(0,0,0,0));

            ServerCloseLoad.setLayout(null);
            ServerCloseLoad.show();


            menu.setLayout(null);
            menu.add(ServerCloseLoad);
            ServerCloseLoad.add(iconLabel); //Add profile picture tag

            iconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isMenuChild = true;  //Is a child component
                }
            });
            final boolean[] canDo = {true, true};
            menuOpen = new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {

                }

            };
            iconLabel.addMouseListener(menuOpen);

        }


        /**
         * Minimized animation
         */
        @SneakyThrows
        @Override
        public void minimize() {

        }

        /**
         * Maximized animation
         */
        @Override
        public void maximize() {
//Transparent opening effect
            new Thread() {  //Open window animation
                @SneakyThrows
                @Override
                public void run() {

                }
            }.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }



        /**
     * Convert #FFFFFF Color to color
     */
    public static Color getColorFromHex(String hex) {
        if (hex == null || hex.length() != 7) {  //判断输入的长度是否正确
            try {
                throw new Exception("This type of color cannot be converted");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        int r = Integer.valueOf(hex.substring(1, 3), 16);  //Convert objects in hexadecimal
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5), 16);
        return new Color(r, g, b);
    }


    // Complete the codes here
    GUI(String ataxx) {
        viewServer.DisplayConnectedLogin();
        new Thread(() -> {
            while (!HomeIsDeliver) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (home.pattern == ViewServer.PATTERN_MANUAL_VS_MANUAL) {
                Menu.promptNow = "manual blue";
                new Thread(() -> {
                    label1:
                    {
                        for (; ; ) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (Menu.promptNow.equals("")) {
                                Menu.promptNow = "manual red";
                                break label1;
                            }
                        }
                    }
                }).start();
            } else {
                Menu.promptNow = "ai blue";
                new Thread(() -> {
                    label1:
                    {
                        for (; ; ) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (Menu.promptNow.equals("")) {
                                Menu.promptNow = "manual red";
                                break label1;
                            }
                        }
                    }
                }).start();
            }
        }).start();


    }





    // These methods could be modified
	
    @SneakyThrows
    @Override
    public void update(Board board) {  //更新面板
        //notifier接收到改变 收到board
        this.board = board;
        Home.XNumberLetters = new char[]{'\0', 'a', 'b', 'c', 'd', 'e','f', 'g'};
        //重新绘制棋盘
        PieceState[] ataxxBoard = board.ataxxBoard;

        new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                while (!HomeIsInit) {
                    Thread.sleep(100);
                }

                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 7; j++) {
                        Home.chessBoard[i][j].removeAll();
                        Home.chessBoard[i][j].repaint();
                    }
                }

                for (int i = 1; i <= 7; i++) {
                    for (int j = 1; j <= 7; j++) {
                        if (blocks[i - 1][j - 1] == 1) {
                            ImageIcon blockImage = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("main/block.png")));
                            RadioJLabel blockLabel = new RadioJLabel(blockImage);
                            blockLabel.setColor(new Color(0, 0, 0, 0));
                            Home.chessBoard[i-1][j-1].add(blockLabel);
                            blockLabel.setBounds(8, 8, blockImage.getIconWidth(), blockImage.getIconHeight());
                        }
                        if (ataxxBoard[Board.index(Home.XNumberLetters[j], Character.forDigit(i,10))] == PieceState.RED) {
                            Chess redChess = new Chess(Chess.RED);
                            redChess.setSize(90,90);
                            chess[i - 1][j - 1] = redChess;
                            Home.chessBoard[7-i][j-1].add(redChess,0);
                        } else if (ataxxBoard[Board.index(Home.XNumberLetters[j], Character.forDigit(i,10))] == PieceState.BLUE) {
                            Chess blueChess = new Chess(Chess.BLUE);
                            blueChess.setSize(90,90);
                            chess[i - 1][j - 1] = blueChess;
                            Home.chessBoard[7-i][j-1].add(blueChess,0);
                        }
                    }
                }
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 7; j++) {
                        Home.chessBoard[i][j].repaint();
                        if (chess[i][j]!=null)
                            chess[i][j].repaint();
                    }
                }
                if (isTheFirstTime) {
                    isTheFirstTime = !isTheFirstTime;
                    Thread.sleep(1500);
                   update(board);
                }
            }
        }.start();



    }

    @SneakyThrows
    @Override
    public String getCommand(String prompt) { //返回一个命令字符串，去掉前后的空格并转换为大写。如果CommandSource提示输入，那么使用PROMPT(如果不为空的话)。
        System.out.println(prompt);
        String newPrompt = prompt.trim().substring(0, prompt.length() - 2);
        if (newPrompt.equals("-")) {  //结束游戏
            Home.redScore.setTextDynamic(board.getColorNums(PieceState.RED)+"");
            Home.blueScore.setTextDynamic(board.getColorNums(PieceState.BLUE)+"");

            return "SCORE";
        }
        Menu.COLOR_NOW = PieceState.colorParse(newPrompt);
        new Thread(()->{
            while (!HomeIsInit) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (Menu.COLOR_NOW.ordinal() == Chess.RED) {
                Home.tip1.setForeground(new Color(224, 78, 78));
            } else {
                Home.tip1.setForeground(new Color(102, 164, 245, 118));
            }
            Home.tip1.setTextDynamic(Menu.COLOR_NOW.toString()+" Round");
            new Thread(()->{
                //0.5秒后修改计分板，让计算指令先执行完
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Home.redScore.setTextDynamic(board.getColorNums(PieceState.RED)+"");
                Home.blueScore.setTextDynamic(board.getColorNums(PieceState.BLUE)+"");
            }).start();

        }).start();

        for (; ; ) {
            if (Menu.promptNow.equals("") || Menu.promptNow.isEmpty()) {
                Thread.sleep(100);
            } else {
                String myPrompt = Menu.promptNow;
                Menu.promptNow = "";
                System.out.println(myPrompt);
                return myPrompt;
            }
        }

    }

    @Override
    public void announceWinner(PieceState state) {  //显示一个与其状态相同的玩家获胜的公告。空表示平局
        Home.tip1.setTextDynamic(state.toString()+" win!");
        new MusicServiceImpl().playWinMP3();
        Menu.gameIsEnd = true;
        Home.tip2.setTextDynamic("");
        Menu.gameOn = false;
        Home.tip1.setForeground(new Color(255,255,255));
    }

    @Override
    public void announceMove(Move move, PieceState player) {  //报告球员的移动
        Home.prompt.setTextDynamic(player+": "+move);
        Home.prompt.setForeground(getColorFromHex("#212a3e"));
        Home.prompt.setFont(new Font("Serif", Font.BOLD, 13));
    }

    @Override
    public void message(String format, Object... args) {  //显示由FORMAT和ARGS指示的消息，其含义与String.format相同
        Home.prompt.setTextDynamic(String.format(format,args));
        Home.prompt.setForeground(getColorFromHex("#212a3e"));
        Home.prompt.setFont(new Font("Serif", Font.BOLD, 13));

    }

    @Override
    public void error(String format, Object... args) {  //报错
        Home.prompt.setTextDynamic(String.format(format,args));
        Home.prompt.setForeground(getColorFromHex("#FF6969"));
        Home.prompt.setFont(new Font("Serif", Font.BOLD, 13));

    }

    public void setVisible(boolean b) {  //可视化
		LoginHome.setVisible(b);

    }

    public void pack() {  //组件打包
        LoginHome.pack();
		
    }
	
}
